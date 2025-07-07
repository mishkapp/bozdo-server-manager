(ns bozdo-server-manager.database
  (:require [konserve.filestore :as kfs]
            [konserve.core :as k]
            [clojure.core.async :refer (<!!)]))

(def ^:private storages (atom {}))

(def db-dir "./database/")

(defn get-storage
  [db-key]
  (if (contains? @storages db-key)
    (get @storages db-key)
    (get (swap! storages assoc db-key (<!! (kfs/connect-fs-store (str db-dir (name db-key))))) db-key)))

(defn db-exists?
  [db-id key]
  (<!! (k/exists? (get-storage db-id) key)))

(defn db-get-in
  [db-id key-vec]
  (<!! (k/get-in (get-storage db-id) key-vec)))

(defn db-assoc-in
  [db-id key-vec value]
  (<!! (k/assoc-in (get-storage db-id) key-vec value)))

(defn db-update-in
  [db-id key-vec fn]
  (<!! (k/update-in (get-storage db-id) key-vec fn)))

(defn db-dissoc
  [db-id key]
  (<!! (k/dissoc (get-storage db-id) key)))

(defn db-list-keys
  [db-id]
  (<!! (kfs/list-files (get-storage db-id))))