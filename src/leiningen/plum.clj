(ns leiningen.plum
  (require [leiningen.file-operations :as fs]))

(defn plum
  "TODO"
  [project & args]
  (do (leiningen.core.main/info (:test-paths project))
    (leiningen.core.main/info (fs/get-filenames-in-paths (:test-paths project) ["clj" "cljs"]))))

