(ns leiningen.tasks
  (:require [leiningen.lein-sass.renderer :refer :all]
            [leiningen.help :as lhelp]
            [leiningen.core.main :as lmain]
            [cemerick.pomegranate :only [add-dependencies]]))

(def renderer-data (init-renderer))
(def container (:container renderer-data))
(def runtime (:runtime renderer-data))

(def ^:private default-options {:src "resources"
                                :dst ""
                                :gem-name "sass"
                                :gem-version "3.3.0"
                                :src-type :sass
                                :style :nested})

(defn get-sass-options [project]
  (if :sass project
    (merge default-options (:sass options))
    (lmain/warn "No sass entry found in project definition.")))

(defn sass
  {:help-arglists [once watch]
   :subtasks [once watch]}

  ([project]
    ((resolve 'leiningen.core.main/abort) (lhelp/help-for "sass")))

  ([project subtask & args]
    (if-let [options (get-sass-options project)]
      (case subtask
        "once"  (render-all! container runtime options)
        "watch"  (watch-and-render! container runtime options)
        (lmain/warn subtask " not found."))
      ((resolve 'leiningen.core.main/abort) "Invalid options in project.clj."))))
