(ns leiningen.lein-sass.renderer-spec
  (:require [clojure.test :refer :all]
            [leiningen.lein-sass.renderer :refer :all]))

(def options {:src "test/files-in"
              :dst "test/files-out"
              :src-type :sass
              :style :compressed})

(def renderer-data (init-renderer))
(def container (:container renderer-data))
(def runtime (:runtime renderer-data))

(deftest renderer
  (testing "Renderer"
    (testing "compiles basic SASS"
      (is (= "body{background:red}\n"
             (render container runtime options "body\n  background: red"))))))

; (deftest renderer-watch
;   (testing "Renderer (watching)"
;     (testing "watches directory for changes"
;       (watch-and-render! container runtime options))))
