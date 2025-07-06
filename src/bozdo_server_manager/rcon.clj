(ns bozdo-server-manager.rcon
  (:require [clojure.tools.logging :as log])
  (:import (java.net InetSocketAddress)
           (java.nio.channels SocketChannel)
           (java.nio.charset StandardCharsets)
           (nl.vv32.rcon RconBuilder)))


(defn ^{:private true}
  create-rcon [address port]
  (-> (RconBuilder/new)
      (.withChannel (SocketChannel/open ^InetSocketAddress (InetSocketAddress/new ^String address ^Integer port)))
      (.withCharset StandardCharsets/UTF_8)
      (.withReadBufferCapacity 4096)
      (.withWriteBufferCapacity 1446)
      (.build)))

(defn send-cmd
  [server cmd]
  (let [rcon      (create-rcon (:address server) (:rcon-port server))
        password  (:rcon-password server)]
    (try
      (do
        (.tryAuthenticate rcon password)
        (.sendCommand rcon cmd))
      (catch Exception e (log/error "RCON error:" e)))
    ))

