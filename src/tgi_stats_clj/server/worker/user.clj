(ns tgi-stats-clj.server.worker.user
  (:require [tgi-stats-clj.server.utils      :as utils]
            [tgi-stats-clj.server.db.actions :as db]))

(defn- map-user
  [user]
  {:steam_id     (:steamid user)
   :avatar_url   (:avatar user)
   :display_name (:personaname user)})

(defn fetch-users
  [api-key steam-ids]
  (let [options {:query-params {:key      api-key
                                :steamids steam-ids}}
        endpoint "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002"
        response (utils/fetch endpoint options)]
    (if (nil? (:error response))
      (db/update-users
        (map map-user (get-in response [:body :response :players])))
      (throw (ex-info (:error response) response)))))
