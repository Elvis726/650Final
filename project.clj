(defproject webch16 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.1"] ;"1.0.0"?
                 [compojure "1.1.6"]
                 [javax.servlet/servlet-api "2.5"]  ;recommended in ring github
                 [org.clojure/java.jdbc "0.2.3"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [korma "0.3.0-RC5"]
                 [org.clojure/tools.trace "0.7.5"]

                 [org.clojure/data.json "0.2.4"]

                 [lib-noir "0.8.1"]
                 ])
