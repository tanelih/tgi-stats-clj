(ns tgi-stats-clj.server.handlers
  "Request handlers for the application."
  (:require [environ.core                    :refer [env]]
            [ring.util.response              :refer [resource-response]]
            [ring.util.http-status           :refer [status]]
            [ring.util.http-response         :refer [ok bad-request]]
            [tgi-stats-clj.utils             :refer [parse-int]]
            [tgi-stats-clj.server.spec       :refer [is-valid-date]]
            [tgi-stats-clj.server.db.actions :refer [get-match-data]]))

(defn render-app
  "Render the application HTML. Do note that there is no server-side rendering
   or anything like that, just a standard SPA."
  []
  (resource-response "index.html" {:root "public"}))

(defn get-matches
  "Get the matches for the given :year and the given :week."
  [year-str week-str]
  (let [year (parse-int year-str)
        week (parse-int week-str)]
    (if (is-valid-date year week)
      (ok (get-match-data {:year year :week week}))
      (bad-request (status 400)))))
