(ns tgi-stats-clj.server.utils
  (:require [clj-time.core       :as t]
            [clj-time.coerce     :as c]
            [throttler.core      :as th]
            [cheshire.core       :as json]
            [org.httpkit.client  :as http]
            [tgi-stats-clj.utils :as utils]))

(def magic-number 76561197960265728)

(defn- non-throttled-fetch
  [url options]
  (let [{:keys [body error]} @(http/get url options)]
    (if error
      (throw (ex-info error {:type :http
                                   :request {:url     url
                                             :options options}}))
      (try
        {:error nil :body (json/parse-string body true)}
        (catch Exception e
          (throw (ex-info (.getMessage e) {:type :http
                                           :request {:url     url
                                                     :options options}} e)))))))

(def fetch (th/throttle-fn non-throttled-fetch 1 :second))

(defn to-steam-id
  [account-id]
  (+ account-id magic-number))

(defn to-account-id
  [steam-id]
  (- steam-id magic-number))

(defn to-date
  [timestamp]
  (c/from-long (* timestamp 1000)))

(defn to-year
  [timestamp]
  (t/year (to-date timestamp)))

(defn to-week
  [timestamp]
  (t/week-number-of-year (to-date timestamp)))

(defn to-side
  [player-slot]
  (if (= 128 (bit-and player-slot 128))
    "dire"
    "radiant"))
