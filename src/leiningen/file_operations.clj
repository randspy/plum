(ns leiningen.file-operations)

(defn- string-is-ending-with? [text endings]
  (some  #(.endsWith text %) endings))

(defn- get-files-paths [path endings]
  (filter #(string-is-ending-with? % endings)
                         (for [file (file-seq (clojure.java.io/file path))]
                            (.getPath file))))


(defn- filenames-in-paths [paths endings]
  (let [paths (map #(get-files-paths % endings) paths)]
    (reduce concat paths)))

(defn- read-file [filename]
  (let [filename-without-path (last (clojure.string/split filename #"(\\|/)"))
        file-content (slurp filename)]
    {:filename filename-without-path :content file-content}))

(defn read-files [paths endings]
  (let [file-list (filenames-in-paths paths endings)]
    (map read-file file-list)))
