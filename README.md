[![Clojars Project](https://img.shields.io/clojars/v/juji/gibberish-detector.svg?color=success)](https://clojars.org/juji/gibberish-detector)


# gibberish-detector

Gibberish-detector is built upon a Clojure port of @rrenaud's python [gibberish detector](https://github.com/rrenaud/Gibberish-Detector).
For a full description of how the HMM classifier works, please refer to his README.

A key difference between this implementation and @rrenaud's is that in this implementation, false will be printed if a sentence is not gibberish and true will be printed if it is. Besides, this detector include some heristic rules in determining when to use HMM classifer and when to use direct dictionary check.

## Installation

```clojure
[juji/gibberish-detector "0.3.0"]
```

OR

```bash
git clone https://github.com/huahaiy/Gibberish-detector
```
Note: You will have to do ```lein run``` in order for the program to generate the data file it needs to determine if input is gibberish.
After that simply call is the ```is-gibberish?``` function in ```detect.clj```.

## Usage

Require ```[gibberish-detector.detect]```


## Examples

```(is-gibberish? "dafsfaenadf")``` => true

```(is-gibberish? "hello")``` => false

```(is-gibberish? "dafdsfa hello")``` => true

```(is-gibberish? "Hey what's up")``` => false

```(is-gibberish? "i adfsaf afeia dasf safsa nnn12")``` => true

```(is-gibberish? "aksdjflak" {:gt 4 :unk-thresh 0.6 :custom-only true})``` => false

```(is-gibberish? "aksdjflak asdfas jalsdkjlkaj jasdlkfjlk kljlkjlkj" {:gt 4 :unk-thresh 0.6 :custom-only true})``` => true


### Might be Useful
#### Adding more data
##### big.txt
The ```big.txt``` file is used in ```trainer.clj``` to initialize the markov chain.
```clojure
(with-open [rdr (clojure.java.io/reader "big.txt")]
  (doseq [line (line-seq rdr)]
    (doseq [[a b] (ngram 2 line)]
      (swap! counts #(update-in % [(pos a) (pos b)] inc)))))
```
If you wish to replace big.txt with another large text file, replace ```"big.txt"``` with the name of the new file.
The new file should be located in the root project directory.

##### good.txt
The ```good.txt``` file is used in ```trainer.clj``` to obtain a vector of average-transition-probabilities of words we know to be not gibberish.
```clojure
(with-open [rdr (clojure.java.io/reader "good.txt")]
        (doseq [line (line-seq rdr)]
          (swap! good-probs #(conj % (avg-transition-prob line @counts)))))
```
If you wish to replace ```goood.txt``` with another file whose lines are NOT gibberish then simply change ```"good.txt"``` to the name of a new file located in the root project directory.

##### bad.txt
The ```bad.txt``` file is used in ```trainer.clj``` to obtain a vector of average-transition-probabilities of words we know to be gibberish.
Every line in ```bad.txt``` consists of random characters (aka gibberish).
```clojure
(with-open [rdr (clojure.java.io/reader "bad.txt")]
        (doseq [line (line-seq rdr)]
          (swap! bad-probs #(conj % (avg-transition-prob line @counts)))))
```
If you wish to replace ```bad.txt``` with another file whose lines are NOT gibberish then simply change ```"bad.txt"``` to the name of the new file located in the root project directory.
## License

Copyright © 2017

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
