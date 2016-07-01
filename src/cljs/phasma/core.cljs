(ns phasma.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [phasma.state :refer [device devices device-info]]
              [phasma.service :as service]
              [accountant.core :as accountant]))

;; -------------------------
;; Views


(defn device-tabs [all-devices selected-device]
  [:ul {:class "nav nav-tabs" :style {:margin "20px 0px 22px"}}
   (for [dev all-devices]
     [:li (assoc
           (if (= dev selected-device) {:class "active"} {})
           :key (str "tab_" dev))
      [:a
      (let [url (str "/phasma/#dev/" dev)]
        {:href url
         :key (str "link_" dev)
         :on-click #(secretary/dispatch! url)
         })
      dev]])
   ]
  )


(defn binary-state [pin dev]
 [:td
  (if (= 0 (:state pin))
    [:button
     {:type "button"
      :class "btn btn-default btn-sm"
      :on-click #(service/set-pin-state dev (:id pin) 1)}
     "Off"]
    [:button
     {:type "button"
      :class "btn btn-primary btn-sm active"
      :on-click #(service/set-pin-state dev (:id pin) 0)}
     "On"]
    )
  ])

(def pin-types ["out" "in" "pwm" "1wire" "i2c"])


(defn type-selector [pin dev]
  [:td
   [:select
    {:class "form-control"
     :id (str (:id pin) "_type")
     :key (str (:id pin) "_type")
;;     :on-change #(.log js/console (str "CH " (-> % .-target .-value) " -> " dev ":" (:id pin)))
     :on-change #(service/set-pin-type dev (:id pin) (-> % .-target .-value))
     }
    (for [tp pin-types]
      [:option
       (assoc
        (if (= tp (:type pin)) {:selected true} {})
        :key (str (:id pin) "_type_" tp)
        :value tp
        )
       tp
       ])]])

(defn in-pin-row [pin dev]
  [:tr 
   [:td (:id pin)]
   [type-selector pin dev]
   [binary-state pin dev]
   ])

(defn out-pin-row [pin dev]
  [:tr 
   [:td (:id pin)]
   [type-selector pin dev]
   [binary-state pin dev]
   ])

(defn default-pin-row [pin dev]
  [:tr 
   [:td (:id pin)]
   [type-selector pin dev]
   [:td (:state pin)]
   ])




(defn pin-row [pin dev]
  (case (:type pin)
    "out" [out-pin-row pin dev]
    "in" [out-pin-row pin dev]
    [default-pin-row pin dev]
    )

  )


(defn pin-table [dev]
  [:table {:class "table"}
   [:thead>tr [:th "Pin"] [:th "Type"] [:th "State"]]
   (for [pin (:ports dev)]
     ^{:key (str "pin_" (:id  pin))}
     [pin-row pin (:id dev)])]
  )

(defn home-page-hash []
  [:div {:class "container"}
   [:h2 "Welcome to #phasma"]
   [:h3 (str "Device: " @device)]
   [device-tabs @devices @device]
   [:div (str "Pollt: "
              (if @device-info
                (:poll @device-info)
                "(NONE)"
                )

              )]
   [pin-table @device-info]
   [:div [:a {:href "/#dev/333" :on-click #(secretary/dispatch! "/#dev/333")} "go to about page"]]])

(defn home-page []
  [:div [:h2 "Welcome to phasma"]
   [:div [:a {:href "/about"} "go to about page"]]])

(defn about-page []
  [:div [:h2 "About phasma"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/phasma/#dev/:id"  {:as params}
  (service/select-device (:id params))
  (session/put! :current-page #'home-page-hash))

(secretary/defroute "/#dev/:id"  {:as params}
  (service/select-device (:id params))
  (session/put! :current-page #'home-page-hash))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
