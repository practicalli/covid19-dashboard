(ns practicalli.data-geo-json
  (:require [clojure.java.io   :as io]
            [jsonista.core     :as json]))



;; Extract data from GeoJSON files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; GeoJSON of England Local Area Districts source:
;; https://raw.githubusercontent.com/martinjc/UK-GeoJSON/master/json/administrative/eng/lad.json


(def geojson-england-local-area-district
  (json/read-value
    (io/resource "public/geo-data/uk-england-lad.geo.json")
    (json/object-mapper {:decode-key-fn true})))

