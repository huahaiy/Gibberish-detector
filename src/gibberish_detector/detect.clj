(ns gibberish-detector.detect
  (:require [gibberish-detector.trainer :as trainer]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.edn :as edn]
            [trie.core :as t]))

(def trie (atom (t/trie [])))

(defn- process-file
  [file]
  (with-open [rdr (clojure.java.io/reader file)]
    (doseq [line (line-seq rdr)]
      (swap! trie #(conj % (string/lower-case (string/trim line)))))))

(process-file (io/file (io/resource "words.txt")))


#_(defn- count-words
  [input]
  (count (re-seq #"\S+" (string/trim input))))

(defn- split-words
  [input]
  (re-seq #"\S+" (string/trim input)))

(defn- dictionary-test
  "Test to see if each word of the input exists in
  the english language dictionary. Will return true
  if 50% or more of the words are NOT in the dictionary
  and false if more than 50% are in the dictionary."
  [input]
  (let [input (-> input
                  string/lower-case
                  (string/replace #"\.|\?|!|,|\d|;|\\|\/|@|:|#|\$|%|\^|&|\*|\(|\)|\-|_|\+|=|\{|\[|\}|\]|\||\"|'|<|>|`|~" " ")
                  split-words)
        num-true-values (->> input
                             (mapv #(get @trie %))
                             (filter identity)
                             count)]
    ;; (println (mapv #(get @trie %) input))
    ;; returns true if 50% or more words are gibberish
    (if (seq input)
      (>= 0.50 (/ num-true-values (count input)))
      false)))

#_(defn is-gibberish?
  "Determine if the given input is gibberish. Returns true if it is gibberish, false otherwise"
  [input]
  (if (and (>= 5 (count-words input))
           (not= (count (string/trim input)) 0))
    (dictionary-test input)
    (let [data (edn/read-string (slurp (io/resource "gib_model.edn")))
          mat (:mat data)
          thresh (:thresh data)]
      (<= (trainer/avg-transition-prob input mat) thresh))))

(defn is-gibberish?
  "Determine if the given input is gibberish. Returns true if it is gibberish, false otherwise"
  [input]
  (let [escaped-patterns #"\d+((\.|\,|\-|\s)\d+)*"
        escaped-input    (string/trim (string/replace input escaped-patterns ""))]
    (if (string/blank? escaped-input)
      false
      (dictionary-test input))))
