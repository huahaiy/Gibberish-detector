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


(defn- count-words
  [input]
  (count (re-seq #"\S+" (string/trim input))))

(defn- split-words
  [input]
  (re-seq #"\S+" (string/trim input)))

(defn- dictionary-test
  "Test to see if each word of the input exists in
  the english language dictionary. Will return true
  if more than 50% of the words are NOT in the dictionary
  and false if more than 50% are in teh dictionary (if more
  than 50% of the input is gibberish true will be returned)"
  [input]
  (let [input (-> input
                  string/lower-case
                  (string/replace #"\.|\?|!|," "")
                  split-words)
        num-true-values (->> input
                             (mapv #(get @trie %))
                             (filter identity)
                             count)]
    ;; (println (mapv #(get @trie %) input))
    ;; returns true if 50% or more words are gibberish
    (>= 0.50 (/ num-true-values (count input)))))

(defn is-gibberish?
  "Determine if the given input is gibberish. Returns true if it is gibberish, false otherwise"
  [input]
  (if (and (>= 5 (count-words input))
           (not= (count (string/trim input)) 0))
    (dictionary-test input)
    (let [data (edn/read-string (slurp (io/resource "gib_model.edn")))
          mat (:mat data)
          thresh (:thresh data)]
      (<= (trainer/avg-transition-prob input mat) thresh))))


;; (split-words "")

;; (dictionary-test "hello dafa fd a")
;; (dictionary-test "hello")
;; (dictionary-test "faaadfadddd")
;; (dictionary-test "hello my old friend it ")
;; (dictionary-test "yeah")

;; (time (pmap read-line (io/file (io/resource "words.txt"))))
;; (println (time (slurp (io/resource "words.txt"))))
