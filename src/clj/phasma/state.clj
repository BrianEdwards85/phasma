(ns phasma.state
  (:require [overtone.at-at :as at]
            [com.rpl.specter :as specter]
            [com.rpl.specter.macros :as sm])
  )

(def init-state
  [
    {
     :id "ESP0001"
     :init false
     :poll 100
     :mills 0
     :ports [
              {:id 3
               :type :out
               :state 0}
              {:id 4
               :type :out
               :state 0}
              {:id 5
               :type :out
               :state 0}
              {:id 6
               :type :out
               :state 0}
              ]
     :sensors {
               "1wire" [
                        {:id "BC1234"
                         :reading 33
                         :min 0
                         :max 100}
                        {:id "C12345"
                         :reading 32
                         :min 0
                         :max 100}
                        {:id "ABC123"
                         :reading 31
                         :min 0
                         :max 100}
                        ]
               "i2c"
               []
               }
     }
    ]
  )

(def rand-bool (mod (rand-int 10) 2))

(defonce state (atom init-state))


