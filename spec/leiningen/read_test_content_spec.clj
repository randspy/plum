(ns leiningen.read-test-content-spec
  (:require [speclj.core :refer :all]
            [leiningen.read-test-content :refer :all]
            [leiningen.debug :refer :all]
            [clojure.zip :as zip]))


(def doc-marker "PlumMarker:")

(describe "Gets a function name."
          (it "Name exist."
              (should= "name" (function-name-after-marker
                                 (str doc-marker "name desc") doc-marker)))
          (it "Name exist with a space."
              (should= "" (function-name-after-marker
                                 (str doc-marker " name") doc-marker)))
          (it "Name is a last string present."
              (should= "name" (function-name-after-marker
                                 (str doc-marker "name ") doc-marker)))
          (it "Name does not exist."
              (should= "" (function-name-after-marker
                                 doc-marker doc-marker))))

(describe "Gets test block without a function name and a marker."
          (describe "Nothing will be removed"
                    (it "No marker and a function name."
                        (should= "(describe \"text\")"
                                 (extract-text  "(describe \"text\")" doc-marker))))
          (it "Marker with a name will be removed."
              (should= "(describe \"text\")"
                       (extract-text  (str "(describe \"" doc-marker "name text\")") doc-marker)))
          (it "Marker will be removed."
              (should= "(describe \" \")"
                       (extract-text (str "(describe \"" doc-marker " \")" ) doc-marker)))
          (describe "Convention is not respected."
                    (it "Marker will be removed."
                        (should= "" (extract-text (str "(describe \"" doc-marker "\")" ) doc-marker)))))

