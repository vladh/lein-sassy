(ns leiningen.lein-sassy.renderer
  (:require [leiningen.lein-sassy.ruby :refer :all]
            [leiningen.lein-sassy.file-utils :refer :all]
            [clojure.string :as s]
            [panoptic.core :refer :all]
            [clojure.java.io :as io]))

(def ^:private watch-poll-rate 50)

(defn- print-message [& args]
  (println (apply str (into [] (concat [(str "[" (java.util.Date.) "] ")] args)))))

(defn- init-gems
  "Installs and loads the needed gems."
  [container options]
  (do (install-gem (:gem-name options) (:gem-version options))
      (require-gem container (str (:gem-name options) "/util"))
      (require-gem container (str (:gem-name options) "/engine"))
      (require-gem container (:gem-name options))))

(defn init-renderer
  "Creates a container and runtime for the renderer to use."
  [options]
  (let [container (make-container)
        runtime (make-runtime container)]
    (do (init-gems container options)
        {:container container :runtime runtime})))

(defn render
  "Renders one template and returns the result."
  [container runtime options template]
  (let [sass-options (make-rb-hash runtime (select-keys options [:syntax :style :load_paths]))
        args (to-array [template sass-options])
        sass (run-ruby container "Sass::Engine")
        engine (call-ruby-method container sass "new" args Object)]
    (try (call-ruby-method container engine "render" String)
         (catch Exception e (print-message "Compilation failed:" e)))))

(defn render-all!
  "Renders all templates in the directory specified by (:src options)."
  [container runtime options]
  (let [directory (clojure.java.io/file (:src options))
        files (filter is-compilable-sass-file (file-seq directory))
        directories (filter #(.isDirectory %) (file-seq directory))
        load-paths (conj (map #(.getPath %) directories) (:src options))
        options (merge options {:load_paths load-paths})]
    (doseq [file files]
      (let [syntax (get-file-syntax file options)
            options (merge options {:syntax syntax})
            inpath (.getPath file)
            insubpath (s/replace-first inpath (:src options) "")
            outsubpath (sass-filename-to-css insubpath)
            outpath (str (:dst options) outsubpath)
            rendered (render container runtime options (slurp file))]
        (print-message inpath " to " outpath)
        (if-not (.exists (io/file (.getParent (io/file outpath)))) (io/make-parents outpath))
        (spit outpath rendered)))))

(defn- file-change-handler
  "Prints the file that was changed then renders all templates."
  [container runtime options _1 _2 file]
  (do (print-message "File" (:path file) "changed.")
      (render-all! container runtime options)))

(defn watch-and-render!
  "Watches the directory specified by (:src options) and calls a handler that
  renders all templates."
  [container runtime options]
  (print-message "Watching" (:src options) "for changes.")
  (let [handler (partial file-change-handler container runtime options)
        fw (->  (file-watcher)
                (on-file-create handler)
                (on-file-modify handler)
                (unwatch-on-delete)
                (run!))
        dw (->  (directory-watcher :recursive true)
                (on-directory-create (fn [_1 _2 dir]
                  (doseq [child (:files (:panoptic.data.core/children dir))]
                    (watch-entity! fw (str (:path dir) "/" child) :created))))
                 (on-file-create #(watch-entity! fw (:path %3) :created))
                 (run!))]
    (watch-entity! dw (:src options) :created)
   @dw))
