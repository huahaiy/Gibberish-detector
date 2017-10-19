(ns gibberish-detector.detect
  (:require [gibberish-detector.trainer :as trainer]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))


(defn is-gibberish?
  [input]
  (let [data (edn/read-string (slurp (io/resource "gib_model.edn")))
        mat (:mat data)
        thresh (:thresh data)]
    (<= (trainer/avg-transition-prob input mat) thresh)))
