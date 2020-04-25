;; practicalli.data-gov-uk
;;
;; Data taken from Gov.uk website
;; https://www.gov.uk/government/publications/covid-19-track-coronavirus-cases
;; Data only available as Microsoft Excel documents
;; There is no JSON format API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.data-gov-uk
  (:require [clojure.java.io   :as io]
            [clojure.data.csv  :as csv]
            [semantic-csv.core :as semantic-csv]
            [jsonista.core     :as json]
            [dk.ative.docjure.spreadsheet :as spreadsheet]))


;; Data Transformation helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn maximum-cases
  "Calculates the maximum value of cases.
  Used to calculate top end of scale in GeoJSON view"
  [data-set]
  (apply max
         (map
           #(Integer/parseInt
              (get % "Cumulative lab-confirmed cases"))
           data-set)))


(defn combine-data-sets
  [geo-json-data-set cases-data-set]

  (update
    geo-json-data-set
    :features
    (fn [features]  ;; as we are using update, features represents the whole geo-json data set
      (mapv
        (fn [feature]
          (assoc
            feature
            :Cases
            (get
              (first
                (filter
                  #(some #{(:LAD13NM (:properties feature))} (vals %))
                  cases-data-set))
              "Cumulative lab-confirmed cases" -1)

            :Location (:LAD13NM (:properties feature))))

        features))))


;; Extract data from CVS files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Original individual data sets

(defn covid19-uk-data
  [data-file]
  (let [data-extract
        (csv/read-csv
          (slurp
            (io/resource data-file)))]
    (zipmap (first data-extract) (second data-extract)) )
  )

(def covid19-uk-data-latest
  (covid19-uk-data "data/daily-indicators-2020-04-10.csv") )

(def covid19-uk-data-latest-fixed
  "Name of DailyUKCases has been changed to NewUKCases.
  A simple fix by copying data to the old key."
  (let [daily-uk-cases (get covid19-uk-data-latest "NewUKCases")]
    (assoc covid19-uk-data-latest "DailyUKCases" daily-uk-cases)))



;; Extract data from CVS files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; New combined data from Gov.uk
;; April 14th 2020 onwards


(defn extract-data-from-csv
  "Convert CSV file to sequence of vectors
  Each hash-map uses the heading text as a key
  for each element in the row of data.

  Return: a sequence of vectors"
  [data-source]
  (->> data-source
       io/resource
       slurp
       csv/read-csv))

(def covid19-uk-england-combined-data
  (extract-data-from-csv "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"))



;; Extract data from GeoJSON
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; GeoJSON of England Local Area Districts source:
;; https://raw.githubusercontent.com/martinjc/UK-GeoJSON/master/json/administrative/eng/lad.json

(def geojson-england-local-area-district
  (json/read-value
    (io/resource "public/geo-data/uk-england-lad.geo.json")
    (json/object-mapper {:decode-key-fn true})))



;; Transform Gov.uk data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Filter non district settings to make maximum-cases work correctly.
(def covid19-cases-uk-local-authority-district
  (remove #(some #{"Country" "Region"} %)
          covid19-uk-england-combined-data))

(defn data-set-specific-date
  "Transform to map for visualization,
  including only the specific date.

  Use csv headings as keys in each map.

  Return: a sequence of maps"
  [extracted-data-set date]

  (let [heading (first extracted-data-set)]

    (semantic-csv/mappify
      {:keyify false}
      (conj
        (filter #(some #{date} %) extracted-data-set)
        heading))))


(def covid19-cases-uk-local-authority-district-date-specific
  (data-set-specific-date covid19-cases-uk-local-authority-district "2020-04-14"))


(def england-lad-geojson-with-cases-date-specific-lad
  (combine-data-sets geojson-england-local-area-district
                     covid19-cases-uk-local-authority-district-date-specific))


