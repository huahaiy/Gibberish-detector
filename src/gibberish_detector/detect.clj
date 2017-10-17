(ns gibberish-detector.detect
  (:require [gibberish-detector.trainer :as trainer]
            [clojure.edn :as edn]))


(defn is-gibberish?
  [input]
  (let [data (edn/read-string (slurp "gib_model.edn"))
        mat (:mat data)
        thresh (:thresh data)]
    (println "Threash is " thresh)
    (println "avg-transtion is " (trainer/avg-transition-prob input mat))
    (<= (trainer/avg-transition-prob input mat) thresh)))
