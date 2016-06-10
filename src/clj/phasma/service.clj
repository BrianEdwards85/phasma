(ns phasma.service
  (:require [com.rpl.specter :as s]
            [com.rpl.specter.macros :as sm]
            [phasma.state :refer [state]])
  )

(defn key-filter [key val] #(= (key %) val))

(defn id-filter [id] (key-filter :id id))

(defn get-devices
  ([] (get-devices @state))
  ([s] (set (sm/select [s/ALL :id]  s))))

(defn get-device [device s]
  (first (sm/select [s/ALL (id-filter device)]  s)))

(defn get-device-init
  ([device] (get-device-init @state))
  ([device s] (first (sm/select [s/ALL (id-filter device) :init]  s))))

(defn set-device-init
  ([device init] (swap! state #(set-device-init device init %)))
  ([device init s] (sm/transform [s/ALL (id-filter device) :init] (fn [_] init) s)))

(defn inc-mills
  ([i] (swap! state #(inc-mills i %)))
  ([i s] (sm/transform [s/ALL :mills] #(+ i %) s)))

(defn get-device-pins
  ([device] (get-device-pins device @state))
  ([device type-or-state]
   (if (sequential? type-or-state)
      (set (sm/select [s/ALL (id-filter device) :ports s/ALL :id] type-or-state))
      (get-device-pins device type-or-state @state)))
  ([device type s]
   (set(sm/select [s/ALL (id-filter device) :ports s/ALL (fn [p] (= (:type p) type)) :id ] s))))

(defn get-pin 
  ([device pin] (get-pin device pin @state))
  ([device pin s] (first (sm/select [s/ALL (id-filter device) :ports s/ALL (id-filter pin)] s))))

(defn merge-pin [old new]
  (let [id (:id old)
       type (or (:type new) (:type old))
       state (or (:state new) (:state old))]
       {:id id :type type :state state}))

(defn set-pin  ;; [device pin m] m)
  ([device m](swap! state #(set-pin device m %)))
  ([device m s]
   (sm/transform
    [s/ALL (id-filter device) :ports s/ALL (id-filter (:id m))]
    #(merge-pin % m) s)))

(defn get-sensor-protocols ;;[device] #{})
  ([device] (get-sensor-protocols device @state))
  ([device s] (set (sm/select [s/ALL (id-filter device) :sensors s/ALL s/FIRST] s ))))

(defn get-sensors ;; [device proto] #{})
  ([device proto] (get-sensors device proto @state))
  ([device proto s]
   (sm/select [s/ALL (id-filter device) :sensors #(get % proto ) s/ALL s/LAST s/ALL :id] s)))



(defn get-sensor ;;[device proto sensor] {})
  ([device proto sensor] (get-sensor device proto sensor @state))
  ([device proto sensor s]
   (first
    (sm/select [s/ALL (id-filter device) :sensors #(get % proto) s/ALL s/LAST s/ALL (id-filter sensor)] s))))

(defn merge-sensor [old new]
  (assoc old :reading (or (:reading new) (:reading old ))))

(defn set-sensor ;;[device proto sensor m] m)
  ([device proto m] (swap! state #(set-sensor device proto m %)))
  ([device proto m s]
   (sm/transform
    [s/ALL (id-filter device) :sensors #(get % proto) s/ALL s/LAST s/ALL (id-filter (:id m))]
    #(merge-sensor % m) s)))

