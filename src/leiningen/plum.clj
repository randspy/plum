(ns leiningen.plum
  (require [leiningen.file-operations :as fs]
           [leiningen.mapping.source_test_mapping :as mapping]
           [leiningen.read.test_content :as tc]
           [leiningen.update.code-description :as cd]))


(defn- add-comments-to-function [source-file comments]
  (loop [source-file source-file comments comments]
    (if-not (seq comments)
      source-file
      (recur (cd/add-test->commend (first comments) source-file)
             (rest comments)))))

(defn- update-file [mapped-file]
  (fs/write-file (str
                   (:source-path mapped-file)
                   (:source-filename mapped-file))
                   (add-comments-to-function
                     (:source-file mapped-file)
                     (tc/test-based-comments (:test-file mapped-file)))))

(defn- update [project-properties]
  (let [tests (fs/read-files (:test-paths project-properties) ["clj" "cljs"])
        sources (fs/read-files (:source-paths project-properties) ["clj" "cljs"])
        mapped-files (mapping/map-tests-with-source-code tests sources)]
        (map update-file mapped-files)))

(defn plum [project & args]
  (let [result (update project)]
    (leiningen.core.main/info result)))

