(ns tgi-stats-clj.server.core
  (:gen-class)
  (:require [environ.core                    :refer [env]]
            [compojure.core                  :refer [defroutes context GET]]
            [org.httpkit.server              :refer [run-server]]
            [ring.middleware.json            :refer [wrap-json-response]]
            [ring.middleware.resource        :refer [wrap-resource]]
            [ring.middleware.file-info       :refer [wrap-file-info]]
            [tgi-stats-clj.utils             :refer [parse-int]]
            [tgi-stats-clj.server.handlers   :refer [render-app get-matches]]
            [tgi-stats-clj.server.middleware :refer [wrap-internal-error]]))

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
    (wrap-file-info)
    (wrap-internal-error)))

(defn -main
  []
  (run-server application {:port port}))

(cheshire.generate/add-encoder
  org.joda.time.DateTime
  (fn [date generator]
    (.writeString generator (clj-time.coerce/to-string date))))
