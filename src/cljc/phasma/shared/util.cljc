(ns phasma.shared.util)

(defn foo-cljc [x]
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn validate-pwm [v]
  (min 255 (max 0 v)))

(defn new-sensor-value [sensor]
  (min (:max sensor)
       (max (:min sensor)
            (let [op (if (= 0 (mod 2 (+ 1 (rand-int 9)))) - +)
                  r (rand-int (- (:max sensor) (:min sensor)))]
              (op (:reading sensor) r)))))
