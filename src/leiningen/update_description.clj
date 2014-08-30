(ns leiningen.update-description)

(defn select-not-empty-strings [strings]
  (filter #(not (empty? %)) strings))

(defn split-index-after-string-plus-space [string split-string]
  (let [starting-index-pos (.indexOf string split-string)
        length (count split-string)]
    (+ starting-index-pos length)))

(defn make-function-name-distinct [name]
  (str " " name " "))

(defn combine-test-framework-name-with-test-names [test-framework tests]
  (str "\"" test-framework (reduce str (map #(str "\n\n" %) tests)) "\" "))


(defn add-test->commend [{:keys [function-name tests test-framework]} updated-string]
  (if (not-empty (select-not-empty-strings tests))
    (let [split-position (split-index-after-string-plus-space
                           updated-string
                           (make-function-name-distinct function-name))
          substring-before-split (subs updated-string 0 split-position)
                comment-string (combine-test-framework-name-with-test-names test-framework tests)
                substring-afret-split (subs updated-string split-position)]
      (str substring-before-split comment-string substring-afret-split))
    updated-string))

