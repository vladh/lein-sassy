(ns leiningen.lein-sass.renderer
  (:require [leiningen.lein-sass.ruby :refer :all]
            [clojure.string :refer [replace-first]]
            [clojure.java.io :as io]))

(def ^:private sass-gem {:gem-name "sass" :gem-version "3.3.0"})

(defn- init-gems [container]
  (do (install-gem (:gem-name sass-gem) (:gem-version sass-gem))
      (require-gem container (str (:gem-name sass-gem) "/util"))
      (require-gem container (str (:gem-name sass-gem) "/engine"))
      (require-gem container (:gem-name sass-gem))))

(defn init-renderer []
  (let [container (make-container)
        runtime (make-runtime container)]
    (do (init-gems container)
        {:container container :runtime runtime})))

(defn render [container runtime options template]
  (let [sass-options (make-rb-hash runtime (select-keys options [:src-type :style]))
        args (to-array [template sass-options])
        sass (run-ruby container "Sass::Engine")
        engine (call-ruby-method container sass "new" args Object)]
    (try (call-ruby-method container engine "render" String)
         (catch Exception e (println "Compilation failed:" e)))))

(defn render-all!
  ([container runtime options] (render-all! container runtime options false))
  ([container runtime options watch?]
    (loop []
      (let [directory (clojure.java.io/file (:src options))
            files (remove #(.isDirectory %) (file-seq directory))]
        (doseq [file files]
          (let [subpath (replace-first (.getPath file) (:src options) "")
                outpath (str (:dst options) subpath)
                rendered (render container runtime options (slurp file))]
            (if-not (.exists (io/file (.getParent (io/file outpath)))) (io/make-parents outpath))
            (spit outpath rendered)))))))
