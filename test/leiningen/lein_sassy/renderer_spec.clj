(ns leiningen.lein-sassy.renderer-spec
  (:require [clojure.test :refer :all]
            [leiningen.lein-sassy.options :refer :all]
            [leiningen.lein-sassy.renderer :refer :all]))

(def project {:sass {:src "test/files-in"
                     :dst "test/files-out"
                     :src-type :sass
                     :style :compressed}})
(def options (get-sass-options project))

(def renderer-data (init-renderer options))
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
