(ns gibberish-detector.trainer
  (:require [clojure.string :as s]
            [clojure.java.io :as io]))


(def accepted-chars #{"a" "b" "c" "d" "e" "f" "g" "h" "i"
                      "j" "k" "l" "m" "n" "o" "p" "q" "r"
                      "s" "t" "u" "v" "w" "x" "y" "z" " "})

(def pos (apply merge (map-indexed (fn [idx value] {value idx}) accepted-chars)))

(defn- normalize
  "Return only the subset of chars from accepted_chars.
    This helps keep the  model relatively small by ignoring punctuation,
    infrequenty symbols, etc."
  [line]
  (vec (filter #(accepted-chars %) (mapv s/lower-case line))))


(defn- ngram
  "Return all ngrams from l after normalizing"
  [n l]
  (let [filtered (normalize l)
        max-len (-> filtered
                    count
                    (- n)
                    (+ 1)
                    )
        acc (atom "")]
    (for [x (range 0 max-len)]
      (subvec filtered x (+ x n)))))

(defn avg-transition-prob
  "Return the average transition prob from l through log_prob_mat."
  [l log_prob_mat]
  (let [
        transition_ct (atom 0)
        log_prob (apply + (mapv (fn [[a b]]
                                  (swap! transition_ct inc)
                                  (get-in log_prob_mat [(pos a) (pos b)])
                                  )
                                (ngram 2 l)))]
    (Math/exp (/ log_prob (if (zero? @transition_ct) 1 @transition_ct)))
    ))

(defn perform-log
  "Perform logerithm on val / s"
  [val s]
  (Math/log (/ val s)))

(defn- train
  " Write a simple model as a pickle file "
  []
  (let [k (count accepted-chars)

        ; Assume we have seen 10 of each character pair.  This acts as a kind of
        ; prior or smoothing factor.  This way, if we see a character transition
        ; live that we've never observed in the past, we won't assume the entire
                                        ; string has 0 probability.
        counts (atom (vec (repeat k (vec (repeat k 10)))))
        ]


    ; Count transitions from big text file, taken
    ; from http://norvig.com/spell-correct.html

    (with-open [rdr (clojure.java.io/reader (io/resource "big.txt"))]
      (doseq [line (line-seq rdr)]
        (doseq [[a b] (ngram 2 line)]
          (swap! counts #(update-in % [(pos a) (pos b)] inc)))))

    ; Normalize the counts so that they become log probabilities.
    ; We use log probabilities rather than straight probabilities to avoid
    ; numeric underflow issues with long texts.
    ; This contains a justification:
    ; http://squarecog.wordpress.com/2009/01/10/dealing-with-underflow-in-joint-probability-calculations/
    (into [] (map-indexed (fn [idx row] ;; make this not lazy anymore
                    (let [s (apply + row)]
                      (swap! counts (fn [co]
                                      (update-in co [idx] (fn [_] (mapv #(perform-log % s) row)))))))
                  @counts))

    (let [
          good-probs (atom [])
          bad-probs (atom [])
          ]


      (with-open [rdr (clojure.java.io/reader (io/resource "good.txt"))]
        (doseq [line (line-seq rdr)]
          (swap! good-probs #(conj % (avg-transition-prob line @counts)))
          ))

      (with-open [rdr (clojure.java.io/reader (io/resource "bad.txt"))]
        (doseq [line (line-seq rdr)]
          (swap! bad-probs #(conj % (avg-transition-prob line @counts)))))

      (spit (io/resource "gib_model.edn" )(prn-str {:mat @counts :thresh (/ (+ (apply min @good-probs) (apply max @bad-probs)) 2)}))
      )
))

