;; practicalli.data-gov-uk
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.data-gov-uk
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]))


(defn covid19-uk-data
  [data-file]
  (let [data-extract
        (csv/read-csv
          (slurp
            (io/resource data-file)))]
    (zipmap (first data-extract) (second data-extract)) )
  )

(def covid19-uk-data-latest
  (covid19-uk-data "data/daily-indicators.csv") )
