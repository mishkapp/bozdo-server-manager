(ns bozdo-server-manager.server
  (:require [bozdo-server-manager.docker :as d]
            [bozdo-server-manager.rcon :as rcon])
  (:import (java.io File)))


(defn start [server]
    (d/start server)
  )

(defn stop [server]

  )

(defn restart [server]
  )

(defn create [server]
  (d/create server)
  )

(defn remove [server]

  )

(defn status [server]
  )