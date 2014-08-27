(ns leiningen.update-description)

(defn select-not-empty-strings [strings]
  (filter #(not (empty? %)) strings))

(defn split-index-after-string-plus-space [string split-string]
  (let [starting-index-pos (.indexOf string split-string)
        length (count split-string)
        space-size 1]
    (+ starting-index-pos length space-size)))

(defn add-test->commend [{:keys [function-name tests test-framework]} updated-string]
  (if (not-empty (select-not-empty-strings tests))
    (let [split-position (split-index-after-string-plus-space updated-string function-name)
          substring-before-split (subs updated-string 0 split-position)
          comment-string (str "\"" test-framework " " (first tests) "\" ")
          substring-afret-split (subs updated-string split-position)]
      (str substring-before-split comment-string substring-afret-split ))
    updated-string))
