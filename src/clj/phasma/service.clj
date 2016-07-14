(ns phasma.service
  (:require [com.rpl.specter :as s]
            [com.rpl.specter.macros :as sm])
  )

(defn key-filter [key val] #(= (key %) val))

(defn id-filter [id] (key-filter :id id))

(defn get-devices [s]
  (set (sm/select [s/ALL :id]  s)))

(defn get-device [device s]
  (first (sm/select [s/ALL (id-filter device)]  s)))

(defn get-device-init [device s]
  (first (sm/select [s/ALL (id-filter device) :init]  s)))

(defn set-device-init [device init s]
  (sm/transform [s/ALL (id-filter device) :init] (fn [_] init) s))

(defn inc-mills [i s]
  (sm/transform [s/ALL :mills] #(+ i %) s))

(defn get-device-pins
  ([device s]
   (set (sm/select [s/ALL (id-filter device) :ports s/ALL :id] s)))
  ([device type s]
   (set(sm/select [s/ALL (id-filter device) :ports s/ALL (fn [p] (= (:type p) type)) :id ] s))))

(defn get-pin [device pin s]
  (first (sm/select [s/ALL (id-filter device) :ports s/ALL (id-filter pin)] s)))

(defn merge-pin [old new]
  (let [id (:id old)
       type (or (:type new) (:type old))
       state (or (:state new) (:state old))]
       {:id id :type type :state state}))

(defn set-pin [device m s]
  (sm/transform
   [s/ALL (id-filter device) :ports s/ALL (id-filter (:id m))]
   #(merge-pin % m) s))

(defn get-sensor-protocols [device s]
  (set (sm/select [s/ALL (id-filter device) :sensors s/ALL s/FIRST] s )))

(defn get-sensors [device proto s]
  (sm/select [s/ALL (id-filter device) :sensors #(get % proto ) s/ALL s/LAST s/ALL :id] s))

(defn get-sensor [device proto sensor s]
  (first
   (sm/select [s/ALL (id-filter device) :sensors #(get % proto) s/ALL s/LAST s/ALL (id-filter sensor)] s)))

(defn merge-sensor [old new]
  (assoc old :reading (or (:reading new) (:reading old ))))

(defn set-sensor [device proto m s]
  (sm/transform
   [s/ALL (id-filter device) :sensors #(get % proto) s/ALL s/LAST s/ALL (id-filter (:id m))]
   #(merge-sensor % m) s))

