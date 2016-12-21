(ns tgi-stats-clj.client.core
  (:require [reagent.core  :as reagent]
            [re-frame.core :as reframe]))

(enable-console-print!)

(defn ^:export run  [] (println "Run!"))
(defn on-fig-reload [] (println "Reload!"))
