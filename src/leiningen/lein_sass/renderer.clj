(ns leiningen.lein-sass.renderer
  (:require [leiningen.lein-sass.ruby :refer :all]))

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

(defn render [container runtime template]
  (let [sass-options (make-rb-hash runtime {
          :src-type :sass
          :style :compressed})
        args (to-array [template sass-options])
        sass (run-ruby container "Sass::Engine")
        engine (call-ruby-method container sass "new" args Object)]
    (try (call-ruby-method container engine "render" String)
         (catch Exception e (println "Compilation failed:" e)))))

(defn render-all! [container runtime options watch?]
  (loop []
    (doseq [file-pair-descriptor (file-pairs-from (:src options))]
      (render container runtime template))))
