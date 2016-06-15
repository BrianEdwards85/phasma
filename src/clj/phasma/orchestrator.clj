(ns phasma.orchestrator
  (:require [phasma.mqtt :as mqtt]
            [phasma.state :refer [state]]
            [phasma.service :as service]))

(def sensor-prefix "sensor")
(def location "orion")

(defn update-sensor! [device proto id reading]
  (if-let [sensor-map (service/get-sensor device proto id @state)]
    (do
      (mqtt/publish (mqtt/build-path sensor-prefix location device proto id) (str reading))
      (swap! state #(service/set-sensor device proto (assoc sensor-map :reading reading) %)))
                 ))


(defn update-pin-reading! [device id reading]
  (if-let [pin (service/get-pin device id @state)]
    (do
      (mqtt/publish (mqtt/build-path sensor-prefix location device "pin" id) (str reading))
      (swap! state #(service/set-pin device (assoc pin :state reading) %))
      )))

(defn update-pin-type! [device id type]
  (if-let [pin (service/get-pin device id @state)]
    (do
     (mqtt/publish (mqtt/build-path "configured" location device "pin" id) (name type))
     (swap! state #(service/set-pin device (assoc pin :type type) %))
     )))


(defn inc-mills! [i]
  (let [s (swap! state #(service/inc-mills i %))]
    (doseq [d s]
        (mqtt/publish (mqtt/build-path "event" location (:id d) "mills" ) (str (:mills 10))))
    ))
