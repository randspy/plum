(ns leiningen.update.code-description-spec
  (:require [speclj.core :refer :all]
            [leiningen.update.code-description :refer :all]
            [leiningen.debug :refer :all]))

(def basic-function-definition "(defn fun 1)")
(defn gen-test-structure [funtion-name tests]
  {:function-name funtion-name :test-framework "Speclj" :tests tests})


(describe "Funtion description is not updated."
          (it "There is nothing to add."
              (should= basic-function-definition
                       (add-test->commend {} basic-function-definition)))
          (it "Test value is empty."
              (should= basic-function-definition
                       (add-test->commend {:function-name "fun" :tests []}
                                          basic-function-definition))
              (should= basic-function-definition
                       (add-test->commend {:function-name "fun" :tests ["" ""]}
                                          basic-function-definition))
              (should= basic-function-definition
                       (add-test->commend {:function-name "fun"}
                                          basic-function-definition)))
          (it "No function name."
              (should= basic-function-definition
                       (add-test->commend {:tests ["" ""]}
                                          basic-function-definition))
              (should= basic-function-definition
                       (add-test->commend {:function-name "" :tests ["" ""]}
                                          basic-function-definition)))
          (it "Diffrent function name."
              (should= basic-function-definition
                       (add-test->commend {:function-name "diffrent_fun" :tests ["test"]}
                                          basic-function-definition)))
          )


(describe "Funtion description is updated."
          (it "One test case is present."
              (should= "(defn fun\n\"Speclj\n\ntest\" 1)"
                       (add-test->commend (gen-test-structure "fun" ["test"])
                                          basic-function-definition)))
          (it "Extra spaces between elements."
              (should= "(defn  fun\n\"Speclj\n\ntest\"  \n  1)"
                       (add-test->commend (gen-test-structure "fun" ["test"])
                                          "(defn  fun  \n  1)")))
          (it "More than one test case is present."
              (should= "(defn fun\n\"Speclj\n\ntest one\n\ntest two\" 1)"
                       (add-test->commend (gen-test-structure "fun" ["test one" "test two"])
                                          basic-function-definition)))
          (it "More functions present inside the source file."
              (should= "(defn diffrent_fun [] 1)\n(defn fun\n\"Speclj\n\ntest\" [] 1)"
                       (add-test->commend (gen-test-structure "fun" ["test"])
                                          "(defn diffrent_fun [] 1)\n(defn fun [] 1)")))
          (describe "There is an existing function description."
                    (it "Updates description with unit tests content."
                        (should= "(defn fun \"Description\nSpeclj\n\ntest\" [] 1)"
                                 (add-test->commend (gen-test-structure "fun" ["test"])
                                                    "(defn fun \"Description\" [] 1)")))
                    (it "Updatas description with more than one unit test case."
                        (should= "(defn fun \"Description\nSpeclj\n\ntest one\n\ntest two\" [] 1)"
                                 (add-test->commend (gen-test-structure "fun" ["test two"])
                                                    "(defn fun \"Description\nSpeclj\n\ntest one\" [] 1)")))))

