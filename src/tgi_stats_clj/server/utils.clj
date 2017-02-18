(ns tgi-stats-clj.server.utils
  (:require [throttler.core      :as th]
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

(defn to-account-id
  "Convert given SteamID (64bit) to an AccountID (32bit)."
  [steam-id]
  (- (utils/parse-int steam-id) magic-number))

