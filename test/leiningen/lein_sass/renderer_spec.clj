(ns leiningen.lein-sass.renderer-spec
  (:require [clojure.test :refer :all]
            [leiningen.lein-sass.renderer :refer :all]))

(deftest renderer
  (let [{container :container runtime :runtime} (init-renderer)]
    (testing "Renderer"
      (testing "compiles basic SASS"
        (is (= "body{background:red}\n"
               (render container runtime "body\n  background: red")))))))
