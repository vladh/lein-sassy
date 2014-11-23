(ns leiningen.sass
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
  (if (:sass project)
    (merge default-options (:sass project))
    (lmain/warn "No sass entry found in project definition.")))

(defn- once
  "Compile files once."
  [options]
  (render-all! container runtime options))

(defn- watch
  "Automatically recompile when files are modified."
  [options]
  (watch-and-render! container runtime options))

(defn sass
  {:help-arglists '[[once] [watch]]
   :subtasks [#'once #'watch]
   :doc "Compile Sass files."}

  ([project]
    ((resolve 'leiningen.core.main/abort) (lhelp/help-for "sass")))

  ([project subtask & args]
    (if-let [options (get-sass-options project)]
      (case subtask
        "once" (once options)
        "watch" (watch options)
        (lmain/warn subtask " not found."))
      ((resolve 'leiningen.core.main/abort) "Invalid options in project.clj."))))
