(ns leiningen.update.code-description
  (:require [leiningen.update.tokens :as tokens]
            [leiningen.debug :refer :all]))

(defn- description-exist-at-position? [last current function-name]
  (and (string? current)
       (= (str last) function-name)))

(defn- description-from-tokens [tokens function-name]
  (loop [rest-of-tokens tokens
         description nil
         last-elem nil]
    (let [current-token (first rest-of-tokens)]
      (if (not (seq rest-of-tokens))
        description
        (recur (rest rest-of-tokens)
               (if (description-exist-at-position? last-elem current-token function-name)
                 current-token
                 description)
               current-token)))))

(defn- convert-token->string [token]
  (if (string? token)
    (str "\"" token "\"")
    (str token)))

(defn- wrap-in-quotations [elements-for-quotations]
  (str " \"" (reduce str elements-for-quotations) "\""))

(defn- combine-test-framework-name-with-test-names [old-documentation test-framework tests]
  (if (seq test-framework)
    (let [test-string (reduce str (map #(str "\n\n" %) tests))]
      (if (not-empty old-documentation)
        (if (< 0 (.indexOf old-documentation test-framework))
          (wrap-in-quotations [old-documentation test-string])
          (wrap-in-quotations [old-documentation "\n" test-framework test-string]))
        (str "\n\"" test-framework test-string "\"")))
    old-documentation))

(def regex-char-esc-smap
  (let [esc-chars "[]*&^%$#"]
    (zipmap esc-chars
            (map #(str "\\" %) esc-chars))))

(defn- function-matching-text [tokens text]
  (let [tokens-in-form-of-strings (map convert-token->string tokens)
        pattern (clojure.string/join "\\s+" tokens-in-form-of-strings)
        sanitized-pattern (reduce str (replace regex-char-esc-smap pattern))]
    (re-find (re-pattern sanitized-pattern) text)))

(defn- insert-string-at-ranges [string substring begin end]
  (str (subs string 0 begin)
       substring
       (subs string end)))

(defn- function-without-description [function-text description]
  (clojure.string/replace function-text
                          (if (not-empty description)
                              (re-pattern (str "\"" description "\" "))
                              #"")
                          ""))
(defn- function-name-end-position-in-function [function-text function-name]
  (+ (.indexOf function-text function-name)
     (count function-name)))

(defn- function-with-updated-description [{:keys [function-name tests test-framework]} tokens function-matching-text]
  (let [existing-description (description-from-tokens tokens function-name)
        documentation (combine-test-framework-name-with-test-names existing-description test-framework tests)
        function-without-description (function-without-description function-matching-text existing-description) 
        index-after-function-name (function-name-end-position-in-function function-without-description function-name)]
     (insert-string-at-ranges
                       function-without-description
                       documentation
                       index-after-function-name index-after-function-name)))

(defn add-test->commend [parsed-tests source-file-text]
  (if-let [tokens (tokens/find-tokens-of-searched-function source-file-text (:function-name parsed-tests))]
    (let [function-matching-text (function-matching-text tokens source-file-text)
          begin-index-in-source (.indexOf source-file-text function-matching-text)
          end-index-in-source (+ begin-index-in-source (count function-matching-text))
          updated-function (function-with-updated-description parsed-tests tokens function-matching-text)]
      (insert-string-at-ranges
        source-file-text updated-function begin-index-in-source end-index-in-source))
    source-file-text))
