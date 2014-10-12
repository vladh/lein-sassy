(ns leiningen.lein-sass.renderer-spec
  (:require [clojure.test :refer :all]
            [leiningen.lein-sass.renderer :refer :all]))

(deftest renderer
  (testing "Renderer"
    (testing "does stuff"
      (is (= "body{background:red}\n" (render "body\n  background: red"))))))
