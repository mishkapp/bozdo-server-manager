(ns bozdo-server-manager.core
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [bozdo-server-manager.telegram :as tg])
  (:use [bozdo-server-manager.database])
  (:gen-class))

(defn init-db
  []
  (let [db-dir-file (io/as-file db-dir)]
    (cond (not (.exists db-dir-file))
          (.mkdir db-dir-file))))

(defn -main
  []
  (log/info "Starting bozdo-server-manager...")
  (init-db)
  (tg/init)
  )