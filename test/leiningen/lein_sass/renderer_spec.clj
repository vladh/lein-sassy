(ns leiningen.lein-sass.renderer-spec
  (:require [clojure.test :refer :all]
            [leiningen.lein-sass.renderer :refer :all]))

(deftest renderer
  (testing "Renderer"
    (testing "compiles basic SASS"
      (is (= "body{background:red}\n" (render "body\n  background: red"))))))
