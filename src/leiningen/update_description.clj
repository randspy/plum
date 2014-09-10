(ns leiningen.update-description
  (:require [clojure.zip :as zip]
            [leiningen.debug :refer :all]))

(defn node-children [node]
  (loop [node (zip/down node) children []]
    (if (or (= node nil)
            (zip/end? node))
      children
      (recur (zip/right node) (conj children (zip/node node))))))


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

(defn description-exist-at-position? [last current function-name]
  (and (string? current)
       (= (str last) function-name)))

(defn split-tokens-with-description [tokens function-name]
  (loop [rest-of-tokens tokens
         tokens-without-descrtiption []
         description nil
         last-elem nil]
    (let [current-token (first rest-of-tokens)]
      (if (not (seq rest-of-tokens))
        [tokens-without-descrtiption description]
        (recur (rest rest-of-tokens)
               (if (not (description-exist-at-position? last-elem current-token function-name))
                 (conj tokens-without-descrtiption current-token)
                 tokens-without-descrtiption)
               (if (description-exist-at-position? last-elem current-token function-name)
                 current-token
                 description)
               current-token)))))

(split-tokens-with-description ['def 'ds "sdd" 3] "def")


(defn convert-token->string [token]
  (if (string? token)
    (str "\"" token "\"")
    (str token)))


(defn combine-test-framework-name-with-test-names [old-documentation test-framework tests]
  (if (seq test-framework)
    (if (not-empty old-documentation)
      (str " \"" old-documentation "\n" test-framework (reduce str (map #(str "\n\n" %) tests)) "\"")
      (str "\n\"" test-framework (reduce str (map #(str "\n\n" %) tests)) "\""))
    old-documentation))

(def regex-char-esc-smap
  (let [esc-chars "[]*&^%$#"]
    (zipmap esc-chars
            (map #(str "\\" %) esc-chars))))

(defn retrieve-function-matching-text [tokens text]
  (let [pattern (clojure.string/join "\\s+" tokens)
        sanitized-pattern (reduce str (replace regex-char-esc-smap pattern))
        text (re-find (re-pattern sanitized-pattern) text)]
    (if text
        text
        "")))

;(retrieve-function-matching-text ["defn" "fun" "\"Description\"" "[]" "1"] "(defn fun \"Description\" [] 1)")
(clojure.string/replace "rer" (re-pattern "r") "")

(defn add-test->commend [{:keys [function-name tests test-framework]} source-file-text]
  (let [wrapped-source-file-text (str "(" source-file-text ")")
         tokens (find-tokens-of-searched-function wrapped-source-file-text function-name)
         [tokens-without-description description] (split-tokens-with-description tokens function-name)
         str-tokens (map convert-token->string tokens)]
    (if tokens
      (let [function-matching-text (retrieve-function-matching-text str-tokens source-file-text)
            begin-index-in-source (.indexOf source-file-text function-matching-text)
            end-index-in-source (+ begin-index-in-source (count function-matching-text))
            documentation (combine-test-framework-name-with-test-names description test-framework tests)
            function-without-description (clojure.string/replace function-matching-text (if (not-empty description)
                                                                                          (re-pattern (str "\"" description "\" "))
                                                                                          #"") "")
            index-after-function-name (+ (.indexOf function-without-description function-name)
                                         (count function-name))
            merged-text (str
                          (subs function-without-description 0 index-after-function-name)
                          documentation
                          (subs function-without-description index-after-function-name))]
        (str
          (subs source-file-text 0 begin-index-in-source)
          merged-text
          (subs source-file-text end-index-in-source)))
      source-file-text)))
