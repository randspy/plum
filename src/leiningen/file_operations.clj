(ns leiningen.file-operations)

(defn- string-is-ending-with? [text endings]
  (some  #(.endsWith text %) endings))

(defn- get-files-paths [path endings]
  (filter #(string-is-ending-with? % endings)
                         (for [file (file-seq (clojure.java.io/file path))]
                            (.getPath file))))


(defn get-filenames-in-paths [paths endings]
  (let [paths (map #(get-files-paths % endings) paths)]
    (reduce concat paths)))
