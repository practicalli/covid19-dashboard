(ns practicalli.data-transformation
  (:require [practicalli.data-gov-uk :as data-gov-uk]
            [practicalli.data-geo-json :as data-geo-json]))


;; Data Transformation helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


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


;; Transformed data sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def england-lad-geojson-with-cases-date-specific-lad
  (combine-data-sets data-geo-json/geojson-england-local-area-district
                     data-gov-uk/covid19-cases-uk-local-authority-district-date-specific))
