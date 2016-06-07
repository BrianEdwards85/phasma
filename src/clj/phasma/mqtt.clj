(ns phasma.mqtt
  (:require [clojurewerkz.machine-head.client :as mh]))


(def conn-opts {:username "android" :password "nairb2792"})

(defonce conn (mh/connect "tcp://127.0.0.1:1883" (mh/generate-id) conn-opts))

(defn build-path [& path]
  (reduce #(str %1 "/" %2) "" path))


