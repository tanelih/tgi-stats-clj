(ns tgi-stats-clj.server.handlers
  "Request handlers for the application."
  (:require [environ.core                    :refer [env]]
            [ring.util.response              :refer [resource-response]]
            [ring.util.http-status           :refer [status]]
            [ring.util.http-response         :refer [ok bad-request]]
            [tgi-stats-clj.utils             :refer [parse-int]]
            [tgi-stats-clj.server.mock       :refer [gen-mock-data]]
            [tgi-stats-clj.server.spec       :refer [is-valid-date]]
            [tgi-stats-clj.server.db.actions :refer [get-match-data]]))

(def steam-key
  (env :steam-api-key))

(def production?
  (= (env :env) "production"))

(defn render-app
  "Render the application HTML. Do note that there is no server-side rendering
   or anything like that, just a standard SPA."
  []
  (resource-response "index.html" {:root "public"}))

(defn get-matches
  "Get the matches for the given :year and the given :week. Performs several
   requests against the Steam API in the background, so this might need some
   optimizing in the future."
  [year-str week-str]
  (let [year (parse-int year-str)
        week (parse-int week-str)]
    (if (is-valid-date year week)
      (ok
        (if production?
          (get-match-data year week)
          (gen-mock-data year week)))
      (bad-request (status 400)))))
