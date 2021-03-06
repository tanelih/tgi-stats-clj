(defproject tgi-stats-clj "0.1.0"
  :uberjar-name     "tgi-stats-clj.jar"
  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/core.async "0.2.395"]
                 [org.clojure/clojurescript "1.9.293"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [org.clojure/data.json "0.2.6"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [environ "1.1.0"]
                 [http-kit "2.1.16"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 [metosin/ring-http-response "0.8.0"]
                 [yesql "0.5.3"]
                 [ragtime "0.6.0"]
                 [com.carouselapps/to-jdbc-uri "0.5.0"]
                 [clj-time "0.13.0"]
                 [jarohen/chime "0.2.0"]
                 [cljs-http "0.1.42"]
                 [reagent "0.6.0"]
                 [re-frame "0.9.1"]
                 [secretary "1.2.3"]
                 [throttler "1.0.0"]]

  :plugins [[lein-ring "0.10.0"]
            [lein-cljsbuild "1.1.5"]
            [lein-figwheel "0.5.8"]]

  :profiles {:dev {:source-paths ["development"]
                   :plugins [[cider/cider-nrepl "0.13.0"]]
                   :dependencies [[org.clojure/tools.nrepl "0.2.12"]
                                  [org.clojure/java.classpath "0.2.0"]
                                  [org.clojure/tools.namespace "0.2.11"]]
                   :aliases {"migrate"  ["run" "-m" "user/migrate"]
                             "rollback" ["run" "-m" "user/rollback"]}}
             :uberjar {:aot :all
                       :main tgi-stats-clj.server.core
                       :prep-tasks ["compile" ["cljsbuild" "once" "production"]]}}

  :repl-options {:timeout 120000}

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
