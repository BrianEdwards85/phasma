(ns phasma.shared.util)

(defn foo-cljc [x]
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn validate-pwm [v]
  (min 255 (max 0 v)))
