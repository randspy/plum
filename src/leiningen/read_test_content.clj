(ns leiningen.read-test-content)

(defn function-name-after-marker [text marker]
  (let [name-start-position (+ (count marker) (.indexOf text marker))
        after-marker (subs text name-start-position)]
    (first (clojure.string/split (subs text name-start-position) #" "))))

(defn extract-text
  "Removes marker and name from the input text, if there is no marker returns
   the original text"
  [text marker]
  (if (.contains text marker)
    (let [name (function-name-after-marker text marker)
          space-between-name-and-text (if (zero? (count name)) 0 1)
          marker-begin-index (.indexOf text marker)
          marker-after-index (+ marker-begin-index
                                (count marker)
                                (count name)
                                space-between-name-and-text)]
      (try
        (str (subs text 0 marker-begin-index) (subs text marker-after-index))
        (catch StringIndexOutOfBoundsException e "")))
    text))
