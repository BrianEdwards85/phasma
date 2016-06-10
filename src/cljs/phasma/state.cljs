(ns phasma.state
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            ))
(defn request-url [& path]
  (str js/window.location.protocol "//" js/window.location.host (apply str path)))


(defonce device (reagent/atom ""))

(defonce device-info (reagent/atom nil))

(defn select-device [d]
  (reset! device d)
  (reset! device-info nil)
  (GET (request-url "/api/v0/devices/" d)
      {:handler
       (fn [r]
         (let [m (js->clj r)]
           (.log js/console (str "M: " r))
           (reset! device-info m)
           )
         )
       })
  )



(defonce devices (reagent/atom []))

(GET (str
           js/window.location.protocol
           "//"
           js/window.location.host
           "/api/v0/devices")
    {
     :handler
     (fn [r]
       (reset! devices (js->clj r))
       )
     }
 )

