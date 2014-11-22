(ns leiningen.lein-sass.renderer
  (:require [leiningen.lein-sass.ruby :refer :all]
            [clojure.string :refer [replace-first]]
            [panoptic.core :refer :all]
            [clojure.java.io :as io]))

(def ^:private sass-gem {:gem-name "sass" :gem-version "3.3.0"}) ;; hardcoded
(def ^:private sass-extensions [:sass :scss])
(def ^:private watch-poll-rate 50)

(defn- init-gems [container]
  "Installs and loads the needed gems."
  (do (install-gem (:gem-name sass-gem) (:gem-version sass-gem))
      (require-gem container (str (:gem-name sass-gem) "/util"))
      (require-gem container (str (:gem-name sass-gem) "/engine"))
      (require-gem container (:gem-name sass-gem))))

(defn init-renderer []
  "Creates a container and runtime for the renderer to use."
  (let [container (make-container)
        runtime (make-runtime container)]
    (do (init-gems container)
        {:container container :runtime runtime})))

(defn render [container runtime options template]
  "Renders one template and returns the result."
  (let [sass-options (make-rb-hash runtime (select-keys options [:src-type :style]))
        args (to-array [template sass-options])
        sass (run-ruby container "Sass::Engine")
        engine (call-ruby-method container sass "new" args Object)]
    (try (call-ruby-method container engine "render" String)
         (catch Exception e (println "Compilation failed:" e)))))

(defn render-all! [container runtime options]
  "Renders all templates in the directory specified by (:src options)."
  (let [directory (clojure.java.io/file (:src options))
        files (remove #(.isDirectory %) (file-seq directory))]
    (doseq [file files]
      (let [subpath (replace-first (.getPath file) (:src options) "")
            outpath (str (:dst options) subpath) ;; TODO change extension to CSS
            rendered (render container runtime options (slurp file))]
        (if-not (.exists (io/file (.getParent (io/file outpath)))) (io/make-parents outpath))
        (spit outpath rendered)))))

(defn- file-change-handler [file container runtime options]
  "Prints the file that was changed then renders all templates."
  (do (println "File" (:path file) "changed.")
      (println container runtime options)
      (render-all! container runtime options)))

(defn watch-and-render! [container runtime options]
  "Watches the directory specified by (:src options) and calls a handler that
  renders all templates."
  (println "Watching" (:src options) "for changes")
  (let [fw (->  (file-watcher)
                (on-file-create #(file-change-handler %3 container runtime options))
                (on-file-modify #(file-change-handler %3 container runtime options))
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
