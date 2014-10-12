(ns leiningen.lein-sass.renderer
  (:require [leiningen.lein-sass.ruby :refer :all]))

(def ^:private sass-gem {:gem-name "sass" :gem-version "3.3.0"})
(install-gem (:gem-name sass-gem) (:gem-version sass-gem))
(require-gem (str (:gem-name sass-gem) "/util"))
(require-gem (str (:gem-name sass-gem) "/engine"))
(require-gem (:gem-name sass-gem))
(def sass (run-ruby "Sass::Engine"))

(defn render [template]
  (try
    (let [args (to-array [template (get-sass-options)])
          engine (call-ruby-method sass "new" args Object)]
      (call-ruby-method engine "render" String))
    (catch Exception e
      (println "Compilation failed:" e))))

(defn render-all!
  []
  (render "body"))

(defn clean-all! [{:keys [output-directory delete-output-dir] :as options}]
  ())
