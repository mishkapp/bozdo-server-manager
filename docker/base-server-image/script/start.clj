#!/bin/bb
(require '[babashka.process :as p :refer [process]]
         '[babashka.tasks :as tasks])

(tasks/shell
  {:dir "/server"}
  "java" "-jar" "packwiz-installer-bootstrap.jar"
  "-g"
  "-s"
  "server"
  "https://raw.githubusercontent.com/Prooty/BozdoG/refs/heads/main/bozdo-packwiz/pack.toml")



(tasks/shell {:out "eula.txt"} "echo eula=true")


(tasks/shell
  {:dir "/server"}
  "java" "-Xmx1G" "-Xms1G" "-jar" "forge-1.7.10-10.13.4.1614-1.7.10-universal.jar")