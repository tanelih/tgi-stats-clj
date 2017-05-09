(ns tgi-stats-clj.server.utils
  (:require [clj-time.core       :as t]
            [clj-time.coerce     :as c]
            [throttler.core      :as th]
            [cheshire.core       :as json]
            [org.httpkit.client  :as http]
            [tgi-stats-clj.utils :as utils]))

(def magic-number 76561197960265728)

(defn- non-throttled-fetch
  "Wrapper around the HTTPKit GET request."
  [url options]
  (let [{:keys [body error]} @(http/get url options)]
    (if error
      {:error error :body body}
      (try
        {:error nil :body (json/parse-string body true)}
        (catch Exception e
          {:error (.getMessage e) :body body})))))

(def fetch (th/throttle-fn non-throttled-fetch 1 :second))

(defn to-steam-id
  "Convert given AccountID (32bit) to a SteamID (64bit)."
  [account-id]
  (+ (utils/parse-int account-id) magic-number))

(defn to-account-id
  "Convert given SteamID (64bit) to an AccountID (32bit)."
  [steam-id]
  (- (utils/parse-int steam-id) magic-number))

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
