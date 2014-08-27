(ns leiningen.update-description-spec
  (:require [speclj.core :refer :all]
            [leiningen.update-description :refer :all]
            [leiningen.debug :refer :all]))

(def basic-function-definition "(defn fun [] 1)")

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
                                          basic-function-definition))))

(describe "Funtion description is updated."
          (it "One test is present."
              (should= "(defn fun \"Speclj test\" [] 1)"
                       (add-test->commend {:function-name "fun" :test-framework "Speclj" :tests ["test"]}
                                          basic-function-definition))))
