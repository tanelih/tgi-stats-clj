(ns user
  (:require [clojure.test                     :as test]
            [clojure.tools.namespace.repl     :as repl]
            [environ.core                     :as environ]
            [ragtime.repl                     :as ragtime-repl]
            [tgi-stats-clj.server.core        :as server]
            [tgi-stats-clj.server.worker.core :as worker]))

(apply repl/set-refresh-dirs (environ/env :directories))

(defn migrate
  []
  (ragtime-repl/migrate tgi-stats-clj.server.db.config/migration))

(defn rollback
  []
  (ragtime-repl/rollback tgi-stats-clj.server.db.config/migration))
