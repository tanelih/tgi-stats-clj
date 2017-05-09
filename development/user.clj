(ns user
  (:require [clojure.test                     :as test]
            [clojure.tools.namespace.repl     :as repl]
            [environ.core                     :as environ]
            [tgi-stats-clj.server.core        :as server]
            [tgi-stats-clj.server.worker.core :as worker]))

(apply repl/set-refresh-dirs (environ/env :directories))
