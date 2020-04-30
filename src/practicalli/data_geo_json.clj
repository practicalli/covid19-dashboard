(ns practicalli.data-geo-json
  (:require [clojure.java.io   :as io]
            [jsonista.core     :as json]))


;; Convert GeoJSON files to Clojure for transformation
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn geojson->clj
  "Convert GeoJSON files to Clojure data structure

  Arguments: GeoJSON file
  Return: Clojure sequence"

  [geo-json-resource]

  (json/read-value
    (io/resource geo-json-resource)
    (json/object-mapper {:decode-key-fn true})))



;; GeoJSON of England Local Area Districts source:
;; https://raw.githubusercontent.com/martinjc/UK-GeoJSON/master/json/administrative/eng/lad.json
;; Saved locally to resources/public/geo-data/uk-england-lad.geo.json


;; Converted Data Sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def geojson-england-local-area-district
  (geojson->clj "public/geo-data/uk-england-lad.geo.json"))
