(ns leiningen.read.test_content
  (:require [leiningen.read.tokens :as tokens]))

(defn function-name-after-marker [text marker]
  (let [name-start-position (+ (count marker) (.indexOf text marker))]
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

(defn- convert-token->string [token]
  (if (string? token)
    (str "\"" token "\"")
    (str token)))

(defn- one-test-base-comment [tokens marker]
  (let [comment (str "(" (clojure.string/join " " (map convert-token->string tokens)) ")")]
       {:function-name  (function-name-after-marker comment marker)
        :test-framework "Speclj"
        :tests          (extract-text comment marker)}))

(defn test-base-coments [source-code marker]
  (if (.contains source-code marker)
    (let [test-tokens (tokens/find-tokens-of-scope-containing-marker source-code marker)]
          (map #(one-test-base-comment % marker) test-tokens))
    []))
