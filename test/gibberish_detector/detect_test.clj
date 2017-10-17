(ns gibberish-detector.detect_test
  (:require [clojure.test :refer :all]
            [gibberish-detector.detect :refer :all]))



(deftest evaluation-test
  (is (= true
         (evaluate "afdalfsafsaiaafdasfasasfasff")))
  (is (= false
         (evaluate "hello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankjs")))
  (is (= true
         (evaluate "jadfadadfa")))
  (is (= false
         (evaluate "yo"))))
