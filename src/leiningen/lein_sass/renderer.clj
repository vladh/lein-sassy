(ns leiningen.lein-sass.renderer
  (:require [leiningen.lein-sass.ruby :refer :all]))

(def ^:private sass-gem {:gem-name "sass" :gem-version "3.2.7"})
(println (ensure-gem-installed! sass-gem))
(require-gem (:gem-name sass-gem))
(def sass (run-ruby "Sass::Engine"))

(defn render [template]
  (try
    (let [args (to-array [template])
          engine (call-ruby-method sass "new" args Object)]
      (call-ruby-method engine "render" String))
    (catch Exception e
      (println "Compilation failed:" e))))

(defn render-all!
  []
  (render "body"))

(defn clean-all! [{:keys [output-directory delete-output-dir] :as options}]
  ())
