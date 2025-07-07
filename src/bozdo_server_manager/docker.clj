(ns bozdo-server-manager.docker
  (:require [contajners.core :as c]
            )
  (:import (java.io File)))

(def ^{:private true} base-image "ghcr.io/mishkapp/bsm-base-server-image:v0.0.1")

(def ^{:private true} container-prefix "bsm-")

(def ^{:private true} docker-conn {:uri "tcp://localhost:2375"})

(def ^{:private true} images
  (c/client {:engine   :docker
             :category :images
             :version  "v1.41"
             :conn     docker-conn}))

(def ^{:private true} containers
  (c/client {:engine   :docker
             :category :containers
             :version  "v1.41"
             :conn     docker-conn}))

(c/invoke images {:op     :ImageCreate
                  :params {:fromImage base-image}})

(defn start [server]
  (let [id (str container-prefix (:name server))]
    (c/invoke containers {:op     :ContainerStart
                          :params id}))
  )

(defn stop [server]
  (let [id (str container-prefix (:name server))]
    (c/invoke containers {:op     :ContainerStop
                          :params id}))
  )

(defn restart [server]
  )

(defn create [server]
  (let [name      (str container-prefix (:name server))
        port      (str (:port server))
        rcon-port (str (:rcon-port server))
        binds     [(str (.getAbsolutePath (new File "")) "/data/" (:name server) ":" "/data")]
        ]
    (c/invoke containers {:op     :ContainerCreate
                          :params {:name name}
                          :data   {:Image        base-image
                                   :ExposedPorts {"25565/tcp" {}
                                                  "25575/tcp" {}}
                                   :HostConfig   {:PortBindings {"25565/tcp" [{:HostPort port}]
                                                                 "25575/tcp" [{:HostPort rcon-port}]}
                                                  :Binds binds}
                                   }}))
  )

(defn remove [server]
  (let [id {:id server}]
    (c/invoke containers {:op     :ContainerPrune
                          :params id}))
  )

(defn status [server]
  )


