(ns practicalli.data-gov-uk-depricated
  (:require
   [clojure.java.io   :as io]
   [clojure.data.csv  :as csv]
   [semantic-csv.core :as semantic-csv]
   [dk.ative.docjure.spreadsheet :as spreadsheet]))



;; Extract data from CSV files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Original individual data sets from Gov.uk

(defn covid19-uk-data
  [data-file]
  (let [data-extract
        (csv/read-csv
          (slurp
            (io/resource data-file)))]
    (zipmap (first data-extract) (second data-extract)) )
  )


;; Data set bindings

(def covid19-uk-data-latest
  (covid19-uk-data "data/daily-indicators-2020-04-10.csv") )

(def covid19-uk-data-latest-fixed
  "Name of DailyUKCases has been changed to NewUKCases.
  A simple fix by copying data to the old key."
  (let [daily-uk-cases (get covid19-uk-data-latest "NewUKCases")]
    (assoc covid19-uk-data-latest "DailyUKCases" daily-uk-cases)))

