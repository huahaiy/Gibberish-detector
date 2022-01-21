(defproject juji/gibberish-detector "0.3.0"
  :description "Detects if user input is gibberish."
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [trie "0.1.1"]]
  :main ^:skip-aot gibberish-detector.core
  :target-path "target/%s"
  :deploy-repositories [["clojars" {:url           "https://repo.clojars.org"
                                    :username      :env/clojars_username
                                    :password      :env/clojars_password
                                    :sign-releases false}]]
  :profiles {:uberjar {:aot :all}})
