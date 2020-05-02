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
            [semantic-csv.core :as semantic-csv]))


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
  (extract-data-from-csv "data-sets/uk-coronavirus-cases.csv"))

(def uk-coronavirus-cases
  (extract-data-from-csv "data-sets/uk-coronavirus-cases.csv"))




;; Sub-sets of Gov.uk data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Location specific data

(defn data-set-filter-locations
  [data-set location-set]
  (filter #(some location-set %)
          data-set))

(defn data-set-remove-locations
  [data-set location-set]
  (remove #(some location-set %)
          data-set))


;; Heading and local area district data
;; - passed to maximum-cases for sizing the scale of data
(def covid19-cases-uk-local-authority-district
  (data-set-remove-locations covid19-uk-england-combined-data
                             #{"Nation" "Country" "Region"}))



;; Date specific sub-set of data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data sets as sequence of hash-maps


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


;; Transformed Gov.uk data sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

#_(def covid19-cases-uk-local-authority-district-date-specific
    (data-set-specific-date covid19-cases-uk-local-authority-district "2020-04-14"))

(def covid19-cases-uk-local-authority-district-date-specific
  (data-set-specific-date covid19-cases-uk-local-authority-district "2020-04-29"))

(defn coronavirus-cases-data
  "Extract and transform cases data for specific locations and date"
  [{:keys [csv-file locations date]}]
  (-> (extract-data-from-csv csv-file)
      (data-set-remove-locations locations)
      (data-set-specific-date  date)))


