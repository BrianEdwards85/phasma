(ns phasma.service
     (:require [ajax.core :refer [GET PUT]]
               [reagent.core :as reagent :refer [atom]]
               [phasma.state :refer [update-device-info! update-devices! device]]
               [phasma.shared.util :refer [new-sensor-value]]
               [clojure.walk :refer [keywordize-keys]]
     ))


(defn request-url [& path]
  (str js/window.location.protocol "//" js/window.location.host (apply str path)))

(defn set-pin-state [device pin state]
  (PUT (request-url "/phasma/api/v0/devices/" device "/pins/" pin "/state")
      {:body (str  state)
       :handler update-device-info!
       }))

(defn set-pin-type [device pin type]
  (PUT (request-url "/phasma/api/v0/devices/" device "/pins/" pin "/type")
      {:body (str type)
       :handler update-device-info!
       }))
(defn update-sensor [device sensor-type sensor]
  (PUT (request-url "/phasma/api/v0/devices/" device "/sensors/" sensor-type "/" (:id sensor))
      {:body (str (new-sensor-value sensor))
       :handler update-device-info!
       }))

(defn get-devices []
  (GET (request-url "/phasma/api/v0/devices")
      {:handler update-devices!}))

(defn get-device [d]
  (GET (request-url "/phasma/api/v0/devices/" d)
      {:handler update-device-info!})
  )

(defn select-device [d]
  (reset! device d)
  (update-device-info! nil)
  (get-device d))

(get-devices)


(js/setInterval
 #(do
    (get-devices)
    (if (some? @device) (get-device @device))
    )
 15000
 )


