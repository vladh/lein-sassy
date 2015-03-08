(ns leiningen.lein-sassy.file-utils
  (:require [clojure.string :as s]))

(def ^:private sass-extensions ["sass" "scss"])
(def ^:private css-extension ".css")

(defn get-file-extension [file]
  (when file
    (let [filename (.getPath file)
          dot (.lastIndexOf filename ".")]
      (when (pos? dot)
        (subs filename (inc dot))))))

(defn is-sass-file
  "Returns whether or not a file ends in a sass extension."
  [file]
  (and (.isFile file)
       (.contains sass-extensions (get-file-extension file))))

(defn is-partial
  "Returns whether or not a file is a partial (i.e. starts with an
  underscore)."
  [file]
  (.startsWith (.getName file) "_"))

(defn is-compilable-sass-file
  "Returns whether or not a file is a sass file that can be compiled (i.e.
  not a partial)."
  [file]
  (and (is-sass-file file) (not (is-partial file))))

(defn get-file-syntax
  "Gets the syntax given a file and options hash. If the hash defines the
  syntax, return that. Otherwise, return the file's extension."
  [file options]
  (or (:syntax options)
      (get-file-extension file)))

(defn sass-filename-to-css
  "Changes a sass extension to the css extension."
  [filename]
  (s/replace filename (re-pattern (str ".(" (s/join "|" sass-extensions) ")$")) css-extension))
