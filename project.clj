(defproject bozdo-server-manager "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.12.1"]
                 [telegrambot-lib "2.15.0"]
                 [cheshire "5.10.1"]
                 [org.clojure/tools.logging "1.3.0"]
                 [com.fzakaria/slf4j-timbre "0.4.1"]
                 [org.clojars.lispyclouds/contajners "1.0.7"]
                 [nl.vv32.rcon/rcon "1.2.0"]
                 [yogthos/config "1.2.1"]
                 [io.replikativ/konserve "0.8.321"]
                 ]
  :repl-options {:init-ns bozdo-server-manager.core}
  :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]
  )
