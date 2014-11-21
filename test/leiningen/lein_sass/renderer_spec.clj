(ns leiningen.lein-sass.renderer-spec
  (:require [clojure.test :refer :all]
            [leiningen.lein-sass.renderer :refer :all]))

(def options {:src "test/files-in"
              :dst "test/files-out"
              :src-type :sass
              :style :compressed})

(deftest renderer
  (let [{container :container runtime :runtime} (init-renderer)]
    (testing "Renderer"
      (testing "compiles basic SASS"
        (is (= "body{background:red}\n"
               (render container runtime options "body\n  background: red"))))
      (testing "finds files and compiles them"
        (is (= "" (render-all! container runtime options)))))))
