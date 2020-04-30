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



;; Extract data from Excel Spreadsheets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

#_(->> (spreadsheet/load-workbook-from-resource "data/historic-covid-19-dashboard-data.xlsx")
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

#_(add-dates covid19-uk-coutry-data-cumulative-cases)


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

