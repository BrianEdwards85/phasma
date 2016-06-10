(ns phasma.controler.http
  (:require [clojure.data.json :as json]
            [compojure.core :refer [GET routes]]
            [phasma.state :refer [state]]
            [phasma.service :as service]
            [phasma.orchestrator :refer :all]
            ))


(defn devices [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str (service/get-devices @state))
   }
  )

(defn device [request]
  (if-let [dev (service/get-device (get-in request [:params :device]) @state)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str dev)}
    {:status 404
     :headers {"Content-Type" "application/json"}
     :body (json/write-str {:error "Device not found"})}
    )
  )



(def route
  (routes
   (GET "/api/v0/devices" [] devices)
   (GET "/api/v0/devices/:device" [] device)
   ))




