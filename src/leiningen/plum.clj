(ns leiningen.plum
  (require [leiningen.file-operations :as fs]
           [leiningen.mapping.source_test_mapping :as mapping]
           [leiningen.read.test_content :as tc]
           [leiningen.update.code-description :as cd]))

(defn update-functions [project-properties]
  (let [tests (fs/read-files (:test-paths project-properties) ["clj" "cljs"])
        sources (fs/read-files (:source-paths project-properties) ["clj" "cljs"])
        mapped-files (mapping/map-tests-with-source-code tests sources)
        source-files-new-content (map (fn [mapped-file]
                              (let [comments (tc/test-based-comments (:test-file mapped-file))]
                                (loop [source-file (:source-file mapped-file) comments comments]
                                  (if-not (seq comments)
                                    (fs/write-file (str
                                                     (:source-path mapped-file)
                                                     (:source-filename mapped-file))
                                                   source-file)
                                    (recur (cd/add-test->commend (first comments) source-file)
                                           (rest comments))))))
                            mapped-files)]
    source-files-new-content))

(defn plum [project & args]
  (let [result (update-functions project)]
    (leiningen.core.main/info result)))

