(defproject tgi-stats-clj "0.1.0"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/core.async "0.2.395"]
                 [org.clojure/clojurescript "1.9.293"]
                 [environ "1.1.0"]
                 [http-kit "2.1.16"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [metosin/ring-http-response "0.8.0"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [yesql "0.5.3"]
                 [ragtime "0.6.0"]
                 [com.carouselapps/to-jdbc-uri "0.5.0"]
                 [clj-time "0.8.0"]
                 [danlentz/clj-uuid "0.1.6"]
                 [factual/timely "0.0.3"]
                 [cljs-http "0.1.42"]
                 [reagent "0.6.0"]
                 [re-frame "0.9.1"]
                 [secretary "1.2.3"]]

  :plugins [[lein-ring "0.10.0"]
            [lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.8"]]

  :prep-tasks ["compile" ["cljsbuild" "once" "production"]]

  :main tgi-stats-clj.server.core
  :ring {:handler tgi-stats-clj.server.core/application}

  :source-paths ["src"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]
             :server-port 8000
             :ring-handler tgi-stats-clj.server.core/application}

  :cljsbuild {:builds [{:id "development"
                        :figwheel {:on-jsload "tgi-stats-clj.client.core/on-fig-reload"}
                        :source-paths ["src"]
                        :compiler {:main tgi-stats-clj.client.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/tgi-stats-clj.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}
                       {:id "production"
                        :source-paths ["src"]
                        :compiler {:main tgi-stats-clj.client.core
                                   :output-to "resources/public/js/compiled/tgi-stats-clj.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]})
