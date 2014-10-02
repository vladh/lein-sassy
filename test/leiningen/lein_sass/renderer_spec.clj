(ns leiningen.lein-sass.renderer-spec
  (:require [clojure.test :refer :all]
            [leiningen.lein-sass.renderer :refer :all]))

(testing "renderer"
  (testing "it does stuff"
    (is (= "" (render "body")))))
