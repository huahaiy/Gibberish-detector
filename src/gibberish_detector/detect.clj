(ns gibberish-detector.detect
  (:require [gibberish-detector.trainer :as trainer]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.edn :as edn]
            [trie.core :as t]))

(def trie (atom (t/trie [])))
(def data (edn/read-string (slurp (io/resource "gib_model.edn"))))
(def mat (:mat data))
(def thresh (:thresh data))

(defn- process-file
  [file]
  (with-open [rdr (clojure.java.io/reader file)]
    (doseq [line (line-seq rdr)]
      (swap! trie #(conj % (string/lower-case (string/trim line)))))))

(process-file (io/file (io/resource "words.txt")))

(defn- split-words
  [input]
  (re-seq #"\S+" (string/trim input)))

(defn- count-words
  [input]
  (-> input split-words count))

(defn- escape-numbers
  [input]
  (string/replace input #"\d+((\.|\,|\-|\s)\d+)*" ""))

(defn- remove-puncs
  [input]
  (string/replace input #"\.|\?|!|,|\d|;|\\|\/|@|:|#|\$|%|\^|&|\*|\(|\)|\-|_|\+|=|\{|\[|\}|\]|\||\"|'|<|>|`|~" " "))

(defn- dictionary-test
  "Test to see if each word of the input exists in
  the english language dictionary. Will return true
  if 50% or more of the words are NOT in the dictionary
  and false if more than 50% are in the dictionary."
  [input]
  (let [num-true-values (->> input
                             (mapv #(get @trie %))
                             (filter identity)
                             count)]
    ;; (println (mapv #(get @trie %) input))
    ;; returns true if 50% or more words are gibberish
    (if (seq input)
      (>= 0.50 (/ num-true-values (count input)))
      false)))

(defn is-gibberish?
  "Determine if the given input is gibberish. Returns true if it is gibberish, false otherwise"
  [input]
  (let [escaped-input (-> input escape-numbers remove-puncs string/trim)]
    (cond
      (string/blank? escaped-input) false
      (let [word-count (count-words escaped-input)]
        (and (< word-count 5)
             (>
              (count (filter #(-> % count (< 3)) (split-words escaped-input)))
              (/ word-count 2))))
      (<= (trainer/avg-transition-prob input mat) thresh)
      :else
      (dictionary-test (-> escaped-input string/lower-case split-words)))))

#_(defn is-gibberish?
  "Determine if the given input is gibberish. Returns true if it is gibberish, false otherwise"
  [input]
  (let [escaped-patterns #"\d+((\.|\,|\-|\s)\d+)*"
        escaped-input    (string/trim (string/replace input escaped-patterns ""))]
    (if (string/blank? escaped-input)
      false
      (dictionary-test input))))
