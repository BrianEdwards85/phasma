(ns phasma.handler
  (:require [compojure.core :refer [GET routes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [phasma.middleware :refer [wrap-middleware]]
            [phasma.controler.http :as http-controler]
            [config.core :refer [env]]))

(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-js "https://code.jquery.com/jquery-2.2.4.js")
   (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
   (include-js "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.js")
  ;; (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))

   ])

(def loading-page
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))


(def route
  (routes
    (GET "/" [] loading-page)
    (GET "/dev" [] loading-page)
    (GET "/about" [] loading-page)
    (resources "/")
    (not-found "Not Found")
  ))

(def app
  (routes http-controler/route
          (wrap-middleware route)))
