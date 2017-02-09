(ns tgi-stats-clj.server.core
  "Entry point for the server side application."
  (:gen-class)
  (:require [environ.core                  :refer [env]]
            [compojure.core                :refer [defroutes context GET]]
            [org.httpkit.server            :refer [run-server]]
            [ring.middleware.json          :refer [wrap-json-response]]
            [ring.middleware.resource      :refer [wrap-resource]]
            [ring.middleware.file-info     :refer [wrap-file-info]]
            [tgi-stats-clj.utils           :refer [parse-int]]
            [tgi-stats-clj.server.worker   :refer [work]]
            [tgi-stats-clj.server.handlers :refer [render-app get-matches]]))

(def port
  (let [port-num (parse-int (env :port))]
    (if (= port-num nil) 8000 port-num)))

(defroutes route-map
  (context "/api" []
    (GET "/matches/:year/:week"
      [year week]
      (fn [req]
        (get-matches year week))))
  (GET "*" [] (fn [req] (render-app))))

(def application
  (->
    route-map
    (wrap-json-response)
    (wrap-resource "public")
    (wrap-file-info)))

(defn -main
  []
  (work)
  (run-server application {:port port}))
