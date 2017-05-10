(ns tgi-stats-clj.server.worker.user
  (:require [tgi-stats-clj.utils             :refer [parse-int]]
            [tgi-stats-clj.server.utils      :as utils]
            [tgi-stats-clj.server.db.actions :as db]))

(def api-endpoint {:player-summary "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002"})

(defn- map-user
  [user]
  {:steam_id     (parse-int (:steamid user))
   :avatar_url   (:avatar user)
   :display_name (:personaname user)})

(defn fetch-users
  [api-key steam-ids]
  (let [options {:query-params {:key      api-key
                                :steamids (clojure.string/join "," steam-ids)}}
        response (utils/fetch (:player-summary api-endpoint) options)]
    (db/update-users
      (map map-user (get-in response [:body :response :players])))))
