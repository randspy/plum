(ns leiningen.update-description
  (:require [clojure.zip :as zip]
            [leiningen.debug :refer :all]))

(defn node-children [node]
  (loop [node (zip/down node) children []]
    (if (or (= node nil)
            (zip/end? node))
      children
      (recur (zip/right node) (conj children (str (zip/node node)))))))

(defn one-of-children-is-function-name? [node name]
  (loop [node (zip/down node) match false]
    (if (or (= node nil)
            (zip/end? node)
            match)
      match
      (recur (zip/right node) (= name (str (zip/node node)))))))

(defn find-tokens-of-searched-function [text name]
  (let [head-node (zip/seq-zip (read-string text))]
    (loop [node head-node tokens nil]
      (if (zip/end? node)
        tokens
        (recur (zip/next node)
               (if (one-of-children-is-function-name? node name)
                 (node-children node)
                 tokens))))))

(defn combine-test-framework-name-with-test-names [test-framework tests]
  (if (seq test-framework)
    (str "\n\"" test-framework (reduce str (map #(str "\n\n" %) tests)) "\"")
    nil))

(def regex-char-esc-smap
  (let [esc-chars "[]()*&^%$#!"]
    (zipmap esc-chars
            (map #(str "\\" %) esc-chars))))

(defn retrieve-function-matching-text [tokens text]
  (let [pattern (clojure.string/join "\\s+" tokens)
        sanitized-pattern (reduce str (replace regex-char-esc-smap pattern))]
    (re-find (re-pattern sanitized-pattern) text)))


(defn add-test->commend [{:keys [function-name tests test-framework]} source-file-text]
  (let [wrapped-source-file-text (str "(" source-file-text ")")
         tokens (find-tokens-of-searched-function wrapped-source-file-text function-name)]
    (if tokens
      (let [function-matching-text (retrieve-function-matching-text tokens source-file-text)
            begin-index-in-source (.indexOf source-file-text function-matching-text)
            end-index-in-source (+ begin-index-in-source (count function-matching-text))
            documentation (combine-test-framework-name-with-test-names test-framework tests)
            index-after-function-name (+ (.indexOf function-matching-text function-name) (count function-name))
            merged-text (str
                          (subs function-matching-text 0 index-after-function-name)
                          documentation
                          (subs function-matching-text index-after-function-name))]
        (str
          (subs source-file-text 0 begin-index-in-source)
          merged-text
          (subs source-file-text end-index-in-source)))
      source-file-text)))
