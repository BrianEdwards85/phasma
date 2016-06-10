(ns phasma.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [phasma.state :refer [device devices device-info select-device]]
              [accountant.core :as accountant]))

;; -------------------------
;; Views


(defn device-tabs [all-devices selected-device]
  [:ul {:class "nav nav-tabs" :style {:margin "20px 0px 22px"}}
   (for [dev all-devices]
     [:li {:key (str "tab_" dev)} [:a
      (let [url (str "/#dev/" dev)]
        (assoc
         (if (= dev selected-device) {:class "active"} {})
         :href url
         :key (str "link_" dev)
         :on-click #(secretary/dispatch! url)
         )
        )
      dev]])
   ]
  )

(defn home-page-hash []
  [:div [:h2 "Welcome to #phasma"]
   [:h3 (str "Device: " @device)]
   [device-tabs @devices @device]
   [:div (str "Poll: "
              (if @device-info
                (get @device-info "poll")
                "(NONE)"
                )

              )]
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

(secretary/defroute "/#dev/:id"  {:as params}
  (js/console.log (str "User: " (:id params)))
  ;;  (swap! device (fn [_] (:id params)))
  (select-device (:id params))
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
