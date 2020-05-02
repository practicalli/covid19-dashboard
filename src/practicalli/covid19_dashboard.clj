;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; practicalli.covid19-dashboard
;;
;; Displays a dashboard of Covid19 data
;; for the United Kingdom
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.covid19-dashboard
  (:gen-class)
  (:require
   [oz.core                     :as oz]
   [practicalli.vega-lite-views :as views]
   [practicalli.view-helpers    :as view-helpers]
   [practicalli.data-gov-uk     :as data-gov-uk]
   [practicalli.data-geo-json   :as data-geo-json]))


(defn dashboard-corvid19-uk
  [geojson-view]
  [:div
   ;; Web page structure and content
   view-helpers/include-bulma-css

   ;; Heading
   view-helpers/webpage-heading

   ;; Daily Headline figures
   (view-helpers/headline-figures)

   ;; Oz visualization
   [:section {:class "section"}
    [:vega-lite views/line-plot-uk-countries-cumulative-cases]
    [:vega-lite views/stacked-bar-uk-countries-cumulative-cases]
    [:vega-lite geojson-view]]])


(defn -main
  "Display a given Oz view"
  []

  ;; Data Extraction and transformation pipleline
  (let [cases-data
        (data-gov-uk/coronavirus-cases-data
          {:csv-file  "data-sets/uk-coronavirus-cases.csv"
           :locations #{"Nation" "Country" "Region"}
           :date      "2020-04-30"})

        geojson-cases-data
        (data-geo-json/geojson-cases-data
          "public/geo-data/uk-local-area-districts-administrative-martinjc-lad.json"
          cases-data)]

    (oz/view!
      (dashboard-corvid19-uk
        (views/geo-json-view geojson-cases-data 1000)))))


;; View Vega-lite components
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment

  ;; Call dashboard - depricated
  #_(-main dashboard-corvid19-uk)

  (-main)

  ;; Modify -main argument list to take a map
  ;; {:view dashboard-corvid19-uk :data-file "xxx.csv"}


  ;; Working, Gov.uk data
  (oz/view! views/line-plot-uk-countries-cumulative-cases)

  ;; Working, using mock data
  (oz/view! views/line-plot)
  (oz/view! views/stacked-bar)

  ;; Under development
  (oz/view! views/geo-map-great-britain)

  (oz/view! views/geo-map-england-full-clipped-boundaries)

  (oz/view! views/geo-map-uk-england-local-area-districts-date-specific)

  )


;; TODO for project
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO: refactor -main to take a date, which will then
;; show headline figures from that day.
;; TODO: create a clojureScript app that allows specific dates to be displayed, updating headline figures
;; TODO: use cumulative cases data set, rather than daily indicators file, reducing the number of documents required for data extraction.
