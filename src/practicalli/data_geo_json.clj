(ns practicalli.data-geo-json
  (:require [clojure.java.io   :as io]
            [jsonista.core     :as json]))


;; Convert GeoJSON files to Clojure for transformation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; GeoJSON of England Local Area Districts source:
;; https://raw.githubusercontent.com/martinjc/UK-GeoJSON/master/json/administrative/eng/lad.json
;; Saved locally to resources/public/geo-data/uk-england-lad.geo.json



(defn geojson->clj
  "Convert GeoJSON files to Clojure data structure

  Arguments: GeoJSON file
  Return: Clojure sequence"

  [geo-json-resource]

  (json/read-value
    (io/resource geo-json-resource)
    (json/object-mapper {:decode-key-fn true})))





(defn geojson-cases-data
  "Combine data sets by adding top level keys
  to each location (feature) in the GeoJSON file

  Attributes:
  - GeoJSON file
  - A sequence of hash-map values for each location

  Returns: GeoJSON in Clojure with additional top level keys"

  [geojson-file cases-data-set]

  (let [geojson-data (geojson->clj geojson-file)]

    (update
      geojson-data
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

          features)))))


;; Transformed data sets - to be deprecated
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(require '[practicalli.data-gov-uk :as data-gov-uk]
         '[practicalli.data-geo-json :as data-geo-json])

(def england-lad-geojson-with-cases-date-specific-lad
  (geojson-cases-data "public/geo-data/uk-england-lad.geo.json"
                      data-gov-uk/covid19-cases-uk-local-authority-district-date-specific))









;; Converted Data Sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


#_(def geojson-england-local-area-district
    (geojson->clj "public/geo-data/uk-england-lad.geo.json"))

#_(def geojson-england-local-area-district
    (geojson->clj "public/geo-data/Counties_and_Unitary_Authorities_December_2017_Boundaries_UK.geojson"))

(def geojson-england-local-area-district
  (geojson->clj "public/geo-data/uk-local-area-districts-administrative-martinjc-lad.json"))


#_(first
    (:features geojson-england-local-area-district))
