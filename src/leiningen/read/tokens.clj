(ns leiningen.read.tokens
  (:require [clojure.zip :as zip]))

(defn- nodes-children [node]
  (loop [node (zip/down node) children []]
    (if (or (= node nil)
            (zip/end? node))
      children
      (recur (zip/right node) (conj children (zip/node node))))))


(defn- one-of-children-has-marker?[node marker]
  (loop [node (zip/down node) match false]
    (if (or (= node nil)
            (zip/end? node)
            match)
      match
      (recur (zip/right node)
             (and (.contains (str (zip/node node)) marker)
                  (string? (zip/node node)))))))

(defn find-tokens-of-scope-containing-marker [text marker]
  (let [wrapped-text (str "(" text ")")
        head-node (zip/seq-zip (read-string wrapped-text))]
    (loop [node head-node tokens []]
      (if (zip/end? node)
        tokens
        (recur (zip/next node)
               (if (one-of-children-has-marker? node marker)
                 (conj tokens (nodes-children node))
                 tokens))))))
