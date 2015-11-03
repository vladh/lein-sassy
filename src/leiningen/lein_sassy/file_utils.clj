(ns leiningen.lein-sassy.file-utils
  (:require [clojure.string :as s]
            [me.raynes.fs :as fs]
            [clojure.java.io :as io]))

(def sass-extensions #{"sass" "scss"})
(def css-extension "css")

(defn file-extension
  [file]
  (some-> (.getPath file) fs/extension (subs 1)))

(defn sass-file?
  "Returns whether or not a file ends in a sass extension."
  [file]
  (contains? sass-extensions (file-extension file)))

(defn sass-partial?
  "Returns whether or not a file is a partial (i.e. starts with an
  underscore)."
  [file]
  (.startsWith (.getName file) "_"))

(defn compilable-sass-file?
  "Returns whether or not a file is a sass file that can be compiled (i.e.
  not a partial)."
  [file]
  (and (.isFile file)
       (sass-file? file)
       (not (sass-partial? file))))

(defn get-file-syntax
  "Gets the syntax given a file and options hash. If the hash defines the
  syntax, return that. Otherwise, return the file's extension."
  [file options]
  (or (:syntax options)
      (file-extension file)))

(defn filename-to-css
  "Changes a file's extension to the css extension."
  [filename]
  (let [basename (fs/base-name filename true)]
    (str basename "." css-extension)))


(defn- dest-file
  [src-file src-dir dest-dir]
  (let [src-dir (.getCanonicalPath (io/file src-dir))
        dest-dir (.getCanonicalPath (io/file dest-dir))
        src-path (.getCanonicalPath src-file)
        src-base-name (fs/base-name src-path)
        rel-dest-path (filename-to-css src-base-name)]
    (io/file (str dest-dir rel-dest-path))))

(defn sass-css-mapping
  [{:keys [src dst]}]
  (let [sass-files (fs/find-files* src sass-file?)]
    (reduce
     (fn [sass-mapping sass-file]
       (assoc sass-mapping sass-file (dest-file sass-file src dst)))
     {} sass-files)))

(defn dir-empty?
  [dir]
  (not-any? #(.isFile %)
            (file-seq (io/file dir))))

(defn delete-file!
  [file]
  (when (.exists file)
    (println (str "Deleting: " file))
    (io/delete-file file)))

(defn delete-directory-recursively!
  [base-dir]
  (doseq [file (reverse (file-seq (io/file base-dir)))]
    (delete-file! file)))

(defn map-file
  [f]
  (str (.getPath dest-file) ".map"))

(defn clean-all!
  [{:keys [dst delete-output-dir] :as options}]
  (doseq [[_ dest-file] (sass-css-mapping options)]
    (delete-file! (io/file dest-file))
    (delete-file! (io/file (map-file dest-file))))

  (when (and delete-output-dir (fs/exists? dst) (dir-empty? dst))
    (println (str "Destination folder " dst " is empty - Deleting it"))
    (delete-directory-recursively! dst)))
