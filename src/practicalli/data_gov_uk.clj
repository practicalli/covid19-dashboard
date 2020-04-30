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




;; Extract data from Excel Spreadsheets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(->> (spreadsheet/load-workbook-from-resource "data/historic-covid-19-dashboard-data.xlsx")
     (spreadsheet/select-sheet "Countries")
     (spreadsheet/select-cell "C9"))

(def covid19-uk-coutry-data-cumulative-cases
  (->> (spreadsheet/load-workbook-from-resource "data/historic-covid-19-dashboard-data.xlsx")
       (spreadsheet/select-sheet "Countries")
       spreadsheet/row-seq
       (remove nil?)
       (map spreadsheet/cell-seq)
       (map #(map spreadsheet/read-cell %))))


(defn add-dates
  [data-set]
  (let [dates (drop 3 (nth data-set 7))]
    (mapv #(hash-map "day" %) dates)))

(add-dates covid19-uk-coutry-data-cumulative-cases)


(defn covid19-uk-countries-data-cumulative-cases-map
  [data-set]
  (let [country (clojure.string/trimr (second data-set))
        values  (drop 3 data-set)]
    (mapv #(hash-map "location" country  "cases" %) values) ))

;; Using 3 to avoid nil values in Scotland, Wales and Northern Ireland countries.  4 is the first cell where all countries have a value.  Index of data starts at zero.

;; Extract out specific rows of data
;; Would have been easier if it were columns.

(def covid19-uk-england-data-cumulative-cases
  (map merge
       (add-dates covid19-uk-coutry-data-cumulative-cases)
       (covid19-uk-countries-data-cumulative-cases-map
         (nth covid19-uk-coutry-data-cumulative-cases 8))))

covid19-uk-england-data-cumulative-cases

(def covid19-uk-scotland-data-cumulative-cases
  (map merge
       (add-dates covid19-uk-coutry-data-cumulative-cases)
       (covid19-uk-countries-data-cumulative-cases-map
         (nth covid19-uk-coutry-data-cumulative-cases 9))))

(def covid19-uk-wales-data-cumulative-cases
  (map merge
       (add-dates covid19-uk-coutry-data-cumulative-cases)
       (covid19-uk-countries-data-cumulative-cases-map
         (nth covid19-uk-coutry-data-cumulative-cases 10))))

(def covid19-uk-northern-ireland-data-cumulative-cases
  (map merge
       (add-dates covid19-uk-coutry-data-cumulative-cases)
       (covid19-uk-countries-data-cumulative-cases-map
         (nth covid19-uk-coutry-data-cumulative-cases 11))))

(def covid19-uk-countries-all-cumulative-cases
  (concat
    covid19-uk-england-data-cumulative-cases
    covid19-uk-scotland-data-cumulative-cases
    covid19-uk-wales-data-cumulative-cases
    covid19-uk-northern-ireland-data-cumulative-cases))

;; TODO: refactor defs above to remove duplicate code,
;; create a function to generate complete data.

;; covid19-uk-countries-all-cumulative-cases
