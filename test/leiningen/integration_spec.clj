(ns leiningen.integration-spec
  (:require [clojure.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [clojure.java.io :as io]))

(defn sass [arg] (sh "lein" "with-profile" "example" "sass" arg))

(deftest integration
  (testing "sass"
    (testing "once"
      (println (sass "once")))))
