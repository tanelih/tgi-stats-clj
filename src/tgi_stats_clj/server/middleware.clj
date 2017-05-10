(ns tgi-stats-clj.server.middleware
  (:require [clojure.tools.logging   :as log]
            [ring.middleware.json    :as json]
            [ring.util.http-status   :as status]
            [ring.util.http-response :as response]))

(defn wrap-internal-error
  [handler]
  (json/wrap-json-response
    (fn [request]
      (try
        (handler request)
        (catch Exception e
          (do
            (log/error e)
            (response/internal-server-error
              (status/status status/internal-server-error))))))))
