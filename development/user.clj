(ns user
  (:require [clojure.test                      :as test]
            [clojure.tools.namespace.repl      :as repl]
            [clojure.string                    :refer [split]]
            [environ.core                      :as environ]
            [ragtime.repl                      :as ragtime-repl]
            [tgi-stats-clj.utils               :refer [parse-int]]
            [tgi-stats-clj.server.db.actions   :as db]
            [tgi-stats-clj.server.core         :as server]
            [tgi-stats-clj.server.worker.core  :as worker]
            [tgi-stats-clj.server.worker.user  :as user-worker]
            [tgi-stats-clj.server.worker.match :as match-worker]))

(def api-key       (environ/env :steam-api-key))
(def steam-id-list (map parse-int (split (environ/env :steam-id-list) #",")))

(defn migrate
  []
  (ragtime-repl/migrate tgi-stats-clj.server.db.config/migration))

(defn rollback
  []
  (ragtime-repl/rollback tgi-stats-clj.server.db.config/migration))

(apply repl/set-refresh-dirs (environ/env :directories))
