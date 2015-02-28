(ns leiningen.lein-sass.options
  (:require [leiningen.core.main :as lmain]))

(def ^:private default-options {:src "resources/public/stylesheets"
                                :dst "resources/app/stylesheets"
                                :gem-name "sass"
                                :gem-version "3.3.0"
                                :src-type :sass
                                :style :nested})

(defn get-sass-options [project]
  (if (:sass project)
    (merge default-options (:sass project))
    (lmain/warn "No sass entry found in project definition.")))
