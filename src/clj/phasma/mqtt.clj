(ns phasma.mqtt
  (:require [clojurewerkz.machine-head.client :as mh]))


(def conn-opts {:username "android" :password "nairb2792"})

(defonce conn (mh/connect "tcp://127.0.0.1:1883" (mh/generate-id) conn-opts))

(defn build-path [& path]
  (reduce #(str %1 "/" %2) "" path))

(defn publish [topic payload]
  (mh/publish conn topic payload))

(defn subscribe [topics handler-fn]
  (mh/subscribe conn (hash-map (map #(vector % 0) topics))
                (fn [topic meta payload]
                  (handler-fn
                   (assoc meta :topic topic :payload (String. payload "UTF-8"))))))
