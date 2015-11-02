(ns leiningen.lein-sassy.file-utils
  (:require [clojure.string :as s]
            [me.raynes.fs :as fs]
            [clojure.java.io :as io]))

(def sass-extensions #{"sass" "scss"})
(def css-extension "css")

(defn file-extension
  [file]
  (some-> (.getPath file) fs/extension (subs 1)))

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


(defn- dest-file
  [src-file src-dir dest-dir]
  (let [src-dir (.getCanonicalPath (io/file src-dir))
        dest-dir (.getCanonicalPath (io/file dest-dir))
        src-path (.getCanonicalPath src-file)
        rel-src-path (s/replace src-path src-dir "")
        rel-dest-path (s/replace rel-src-path (fs/extension src-file) ".css")]
    (io/file (str dest-dir rel-dest-path))))

(defn files-from
  [{:keys [src dst]}]
  (let [file-filter (fn [file]
                      (case (fs/extension file)
                        ".scss" true
                        ".sass" true
                        false))
        source-files (fs/find-files* src file-filter)]
    (reduce #(assoc %1 %2 (io/file (dest-file %2 src dst))) {} source-files)))

(defn exists
  [dir]
  (and dir (.exists (io/file dir))))

(defn dir-empty?
  [dir]
  (not (reduce (fn [memo path] (or memo (.isFile path))) false (file-seq (io/file dir)))))

(defn delete-file!
  [file]
  (when (.exists file)
    (println (str "Deleting: " file))
    (io/delete-file file)))

(defn delete-directory-recursively!
  [base-dir]
  (doseq [file (reverse (file-seq (io/file base-dir)))]
    (delete-file! file)))

(defn clean-all!
  [{:keys [dst delete-output-dir] :as options}]
  (doseq [[_ dest-file] (files-from options)]
    (delete-file! (io/file dest-file))
    (delete-file! (io/file (str (.getPath dest-file) ".map"))))

  (when (and delete-output-dir (exists dst) (dir-empty? dst))
    (println (str "Destination folder " dst " is empty - Deleting it"))
    (delete-directory-recursively! dst)))
