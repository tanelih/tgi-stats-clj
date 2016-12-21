(ns tgi-stats-clj.server.core
  (:gen-class)
  (:require [environ.core :refer [env]]
            [compojure.core :refer [defroutes context GET]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [tgi-stats-clj.server.handlers :as handlers]))

(defroutes route-map
  (context "/api" []
    (GET "/matches/:year/:week" [year week] (fn [req] (handlers/get-matches year week))))
  (GET "*" [] (fn [req] (handlers/render-app))))

(def application
  (->
    route-map
    (wrap-json-response)
    (wrap-resource "public")
    (wrap-file-info)))

(defn -main [] (run-server application {:port 8080}))
