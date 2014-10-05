(ns leiningen.mapping.source_test_mapping)

(defn- test-matches-source? [test source]
  (let [test-without-spec (clojure.string/replace test #"_spec" "")]
    (= test-without-spec source)))

(defn- create-structure [test source]
  {:source-filename (:filename source)
   :source-path (:file-path source)
   :source-file (:content source)
   :test-file (:content test)})

(defn map-tests-with-source-code [tests sources]
  (flatten
    (map (fn [test]
           (let [matching-sources
                 (filter #(test-matches-source? (:filename test) (:filename %)) sources)]
             (map #(create-structure test %) matching-sources)))
         tests)))

