(ns gibberish-detector.detect
  (:require [gibberish-detector.trainer :as trainer]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.edn :as edn]
            [trie.core :as t]))

(def trie (atom (t/trie [])))
(def data (edn/read-string (slurp (io/resource "gib_model.edn"))))
(def mat (:mat data))
(def thresh (:thresh data))

(defn- process-file
  [file]
  (with-open [rdr (io/reader file)]
    (doseq [line (line-seq rdr)]
      (swap! trie #(conj % (s/lower-case (s/trim line)))))))

(process-file (io/file (io/resource "words.txt")))

(defn- split-words
  [input]
  (re-seq #"\S+" (s/trim input)))

(defn- count-words
  [input]
  (-> input split-words count))

(defn- escape-numbers
  [input]
  (s/replace input #"\d+((\.|\,|\-|\s)\d+)*" ""))

(defn- remove-puncs
  [input]
  (s/replace input #"\.|\?|!|,|\d|;|\\|\/|@|:|#|\$|%|\^|&|\*|\(|\)|\-|_|\+|=|\{|\[|\}|\]|\||\"|'|<|>|`|~" " "))

(defn- dictionary-test
  "Test to see if each word of the input exists in
  the english language dictionary. Will return true
  if `unk-thresh` or more of the words are NOT in the dictionary
  and false if more than `unk-thresh` are in the dictionary.
  `unk-thresh` is between 0 and 1, default is 0.5"
  ([input] (dictionary-test input 0.5))
  ([input unk-thresh]
   (assert (and (number? unk-thresh) (> 1 unk-thresh 0))
           "Please provide valid unk-thresh which is a number between 0 and 1.")
   (let [num-unk-values (->> input
                              (mapv #(get @trie %))
                              (remove identity)
                              count)]
     ;; (println (mapv #(get @trie %) input))
     ;; returns true if 50% or more words are gibberish
     (if (seq input)
       (>= (/ num-unk-values (count input)) unk-thresh)
       false))))

(defn is-gibberish?
  "Determine if the given input is gibberish.
  Returns true if it is gibberish, false otherwise
  Option can be provided to customize the gibberish detection.
  `gt`: add a greater than the given value of words dictionary test
  `unk-thresh`: unkown thresh for the dictionary test
  `custom-only`: only check the custom condition"
  ([input] (is-gibberish? input nil))
  ([input {:keys [gt unk-thresh custom-only]}]
   (when custom-only
     (assert gt "Please add a condition for custom only detection."))
   (let [escaped-input (-> input escape-numbers remove-puncs s/trim)
         word-count    (if (s/blank? escaped-input) 0 (count-words escaped-input))]
     (cond
       (= 0 word-count) false
       (and gt (> word-count gt))
       (dictionary-test (-> escaped-input s/lower-case split-words)
                        (or unk-thresh 0.5))

       (and (< word-count 5)
            (>
              (count (filter #(-> % count (<= 3)) (split-words escaped-input)))
              (/ word-count 2))
            (not custom-only))
       (<= (trainer/avg-transition-prob input mat) thresh)

       (not custom-only)
       (dictionary-test (-> escaped-input s/lower-case split-words))

       :else false))))

#_(defn is-gibberish?
    "Determine if the given input is gibberish. Returns true if it is gibberish, false otherwise"
    [input]
    (let [escaped-patterns #"\d+((\.|\,|\-|\s)\d+)*"
          escaped-input    (s/trim (s/replace input escaped-patterns ""))]
      (if (s/blank? escaped-input)
        false
        (dictionary-test input))))
