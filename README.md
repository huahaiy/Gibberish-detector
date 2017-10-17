# gibberish-detector

Gibberish-detector is a Clojure port of @rrenaud's python [gibberish detector](https://github.com/rrenaud/Gibberish-Detector).
For a full description of how it works, please refer to his README.

A key difference between this implementation and @rrenaud's is that in this implementation, false will be printed if a sentence is not gibberish and true will be printed if it is.

## Installation

Download from http://example.com/FIXME.

## Usage

Require ```[gibberish-detector.detect]```


## Examples

```(is-gibberish? "dafsfaenadf")``` => true

```(is-gibberish? "hello")``` => false

```(is-gibberish? "dafdsfa hello")``` => true

```(is-gibberish? "Hey what's up")``` => false

```(is-gibberish? "i adfsaf afeia dasf safsa nnn12")``` => true


### Might be Useful


## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
