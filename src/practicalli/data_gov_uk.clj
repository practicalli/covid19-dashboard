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
