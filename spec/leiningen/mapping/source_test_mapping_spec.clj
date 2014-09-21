(ns leiningen.mapping.source_test_mapping_spec
  (:require [speclj.core :refer :all]
            [leiningen.mapping.source_test_mapping :refer :all]))

(describe "Mapping connection between tests and code they are testing."
          (describe "There is no connection."
                    (it "Has not input."
                        (should= []
                                 (map-tests-with-source-code [] [])))
                    (it "Tests files with source files do not match."
                        (should= []
                                 (map-tests-with-source-code [{:filename "core/core_spec.clj"}]
                                                             [{:filename "core/cores.clj"}]))))
          (describe "Connection exists."
                    (it "Connects source with test."
                        (should= [{:source-filename "core/core.clj" :source-file "" :test-file ""}]
                                 (map-tests-with-source-code [{:filename "core/core_spec.clj" :content ""}]
                                                             [{:filename "core/core.clj" :content ""}])))
                    (it "Connects source with test when there are meny sources and tests"
                        (should= [{:source-filename "core/core.clj" :source-file "" :test-file ""}]
                                 (map-tests-with-source-code [{:filename "core/core_spec.clj" :content ""}
                                                              {:filename "case/core_spec.clj" :content ""}]
                                                             [{:filename "run/core.clj" :content ""}
                                                              {:filename "core/core.clj" :content ""}])))))


