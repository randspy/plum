(ns leiningen.file-operations
  (:require [clojure.java.io :as io]))

(defn- string-is-ending-with? [text endings]
  (some  #(.endsWith text %) endings))

(defn- get-files-paths [path endings]
  (filter #(string-is-ending-with? % endings)
                         (for [file (file-seq (clojure.java.io/file path))]
                            (.getPath file))))


(defn- filenames-in-paths [paths endings]
  (let [paths (map #(get-files-paths % endings) paths)]
    (reduce concat paths)))

(defn- read-file [path filename]
  (let [filename-without-path (clojure.string/replace filename (re-pattern path) "")
        file-content (slurp filename)]
    {:file-path path :filename filename-without-path :content file-content}))

(defn read-files [paths endings]
  (let [file-list (filenames-in-paths paths endings)]
    (map read-file paths file-list)))

(defn write-file [filename string]
  (with-open [write-string (io/writer filename)]
    (.write write-string string)))