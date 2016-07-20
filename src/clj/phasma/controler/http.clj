(ns phasma.controler.http
  (:require [clojure.data.json :as json]
            [compojure.core :refer [GET PUT routes]]
            [phasma.state :refer [state]]
            [phasma.service :as service]
            [phasma.orchestrator :refer :all]
            [clojure.java.io :as io]
            ))


(defn devices [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str (service/get-devices @state))
   })

(defn device [request]
  (if-let [dev (service/get-device (get-in request [:params :device]) @state)]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/write-str dev)}
    {:status 404
     :headers {"Content-Type" "application/json"}
     :body (json/write-str {:error "Device not found"})}
    ))

(defn get-pin [request]
  (let [d (get-in request [:params :device])
        p (Integer/parseInt (get-in request [:params :pin]))]
    (if-let [pin (service/get-pin d p @state)]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/write-str pin)}
      {:status 404
       :headers {"Content-Type" "application/json"}
       :body (json/write-str {:error "Device pin not found"})}
      )))

(defn put-pin-state [request]
  (let [s (Integer/parseInt (first (line-seq (io/reader (:body request) :encoding "UTF-8"))))
        d (get-in request [:params :device])
        p (Integer/parseInt (get-in request [:params :pin]))]
    (update-pin-reading! d p s))
  (device request) 
  )


(defn put-pin-type [request]
  (let [t (keyword (first (line-seq (io/reader (:body request) :encoding "UTF-8"))))
        d (get-in request [:params :device])
        p (Integer/parseInt (get-in request [:params :pin]))]
    (update-pin-type! d p t))
  (device request) 
  )

(defn update-sensor-value [request]
  (let [r (Integer/parseInt (first (line-seq (io/reader (:body request) :encoding "UTF-8"))))
        d (get-in request [:params :device])
        p (get-in request [:params :proto])
        s (get-in request [:params :sensor])]
    (println (str r ":" d ":" p ":" s))
    (update-sensor! d p s r)
    (device request)
    ))

(def route
  (routes
   (GET "/api/v0/devices" [] devices)
   (PUT "/api/v0/devices/:device/pins/:pin/state" [] put-pin-state)
   (PUT "/api/v0/devices/:device/pins/:pin/type" [] put-pin-type)
   (GET "/api/v0/devices/:device/pins/:pin" [] get-pin)
   (GET "/api/v0/devices/:device" [] device)
   (PUT "/api/v0/devices/:device/sensors/:proto/:sensor" [] update-sensor-value) 
   ))




