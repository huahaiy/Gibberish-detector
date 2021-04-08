(ns gibberish-detector.detect-test
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
         (is-gibberish? "yo")))
  (is (= false
         (is-gibberish? "Sup dawg. what up? How's it going there.")))
  (is (= true
         (is-gibberish? "hello ad af saf e aifdsaj fsadifj asljds iflasdj fdasilf sajif salf saijdfsa")))
  (is (= false
         (is-gibberish? "")))
  (is (= false
         (is-gibberish? ",")))
  (is (= false
         (is-gibberish? "9173232232")))
  (is (= false
         (is-gibberish? "917-323-2232")))
  (is (= false
         (is-gibberish? "20/04/2018")))
  (is (= false
         (is-gibberish? "09.92.91")))
  (is (= true
         (is-gibberish? "A.b. C.")))
  (is (= false
         (is-gibberish? "A.b. C." {:gt 4 :unk-thresh 0.6 :custom-only true})))
  (is (= false
         (is-gibberish? "aksdjflak" {:gt 4 :unk-thresh 0.6 :custom-only true})))
  (is (= true
         (is-gibberish? "aksdjflak asdfas jalsdkjlkaj jasdlkfjlk kljlkjlkj" {:gt 4 :unk-thresh 0.6 :custom-only true})))
  (is (= false
         (is-gibberish? "my name is ajkdljf asldkfja")))
  (is (= true
         (is-gibberish? "my name is ajkdljf asldkfja" {:gt 4 :unk-thresh 0.33})))
  )
