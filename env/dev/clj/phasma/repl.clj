(ns phasma.repl
  (:use phasma.handler
        figwheel-sidecar.repl-api
        ring.server.standalone
        [ring.middleware file-info file]))

(defonce server (atom nil))

(defn get-handler []
  ;; #'app expands to (var app) so that when we reload our code,
  ;; the server is forced to re-resolve the symbol in the var
  ;; rather than having its own copy. When the root binding
  ;; changes, the server picks it up without having to restart.
  (-> #'app
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
      (wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body
      (wrap-file-info)))

(defn start-server
  "used for starting the server in development mode from REPL"
  [& [port]]
  (let [port (if port (Integer/parseInt port) 3448)]
    (reset! server
            (serve (get-handler)
                   {:port port
                    :open-browser? false
                    :auto-reload? true
                    :join? false}))
    (println (str "You can view the site at http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))


(def figwheel-config
  {:figwheel-options {
                      :http-server-root "public"
                      :server-port 3448
                      :server-ip   "0.0.0.0"
                      :ring-handler phasma.handler/app}
   :all-builds[{
                :id "dev"
                :figwheel true
                :source-paths ["src/cljs" "src/cljc"]
                :compiler {:output-to "target/cljsbuild/public/js/app.js"
                           :output-dir "target/cljsbuild/public/js/out"
                           :asset-path   "/phasma/js/out"}}]})

