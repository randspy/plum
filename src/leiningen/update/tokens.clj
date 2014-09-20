(ns leiningen.update.tokens
  (:require [clojure.zip :as zip]))

(defn- nodes-children [node]
  (loop [node (zip/down node) children []]
    (if (or (= node nil)
            (zip/end? node))
      children
      (recur (zip/right node) (conj children (zip/node node))))))


(defn- one-of-children-is-function-name?[node function-name]
  (loop [node (zip/down node) match false]
    (if (or (= node nil)
            (zip/end? node)
            match)
      match
      (recur (zip/right node) (= function-name (str (zip/node node)))))))

(defn find-tokens-of-searched-function [text function-name]
  (let [wrapped-text (str "(" text ")")
        head-node (zip/seq-zip (read-string wrapped-text))]
    (loop [node head-node tokens nil]
      (if (zip/end? node)
        tokens
        (recur (zip/next node)
               (if (one-of-children-is-function-name? node function-name)
                 (nodes-children node)
                 tokens))))))

