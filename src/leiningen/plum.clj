(ns leiningen.plum
  (require [leiningen.file-operations :as fs]))

(defn plum
  "TODO"
  [project & args]
  (do (leiningen.core.main/info project)
    (leiningen.core.main/info (fs/read-files (:test-paths project) ["clj" "cljs"]))
    (leiningen.core.main/info (fs/read-files (:source-paths project) ["clj" "cljs"]))))

