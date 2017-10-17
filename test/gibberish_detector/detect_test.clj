(ns gibberish-detector.detect_test
  (:require [clojure.test :refer :all]
            [gibberish-detector.detect :refer :all]))



(deftest evaluation-test
  (is (= true
         (is-gibberish? "afdalfsafsaiaafdasfasasfasff")))
  (is (= false
         (is-gibberish? "hello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankshello what's up I'm good how about you. Good thankjs")))
  (is (= true
         (is-gibberish? "jadfadadfa")))
  (is (= false
         (is-gibberish? "yo"))))
