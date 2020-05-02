(ns practicalli.data-transform-ons
  (:require
   [practicalli.data-geo-json :as data-geo-json]
   [practicalli.data-gov-uk   :as data-gov-uk]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Experimental - namespace not currently used
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Transformation for GeoJSON files specifically from
;; the UK Office of National Statistics
;;
;; The ONS GeoJSON files use different naming conventions for fields
;; and do not work with Vega-lite directly
;;
;; ONS GeoJSON export file with different outfields
;; https://geoportal.statistics.gov.uk/datasets/counties-and-unitary-authorities-december-2017-full-extent-boundaries-in-uk-wgs84/geoservice
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn combine-data-sets-office-national-statistics
  "Combine data sets by adding top level keys
  to each location (feature) in the GeoJSON file
  The office of national statistics GeoJSON files use different
  key names so required updating.

  Attributes:
  - GeoJSON file converted into a Clojure sequence
  - A sequence of hash-map values for each location
  Returns: GeoJSON in Clojure with additional top level keys"

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
                  #(some #{(:ctyua17nm (:properties feature))} (vals %))
                  cases-data-set))
              "Cumulative lab-confirmed cases" -1)

            :Location (:ctyua17nm (:properties feature))))

        features))))


;; Transformed data sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



(def england-lad-ons-geojson-with-cases-date-specific-lad
  (combine-data-sets-office-national-statistics data-geo-json/geojson-england-local-area-district
                                                data-gov-uk/covid19-cases-uk-local-authority-district-date-specific))
