(defproject juji/gibberish-detector "0.1.2-SNAPSHOT"
  :description "Detects if user input is gibberish."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot gibberish-detector.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
