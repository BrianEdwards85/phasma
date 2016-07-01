(ns phasma.state
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [clojure.walk :refer [keywordize-keys]]
            ))

(defonce device (reagent/atom ""))

(defonce device-info (reagent/atom nil))

(defonce devices (reagent/atom []))

(defn update-device-info! [json-txt]
  (if (nil? json-txt)
    (reset! device-info nil)
    (reset! device-info (keywordize-keys (js->clj json-txt)))))

(defn update-devices! [d]
  (reset! devices (js->clj d)))

