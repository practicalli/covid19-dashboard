;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vega-lite components
;; Named components generating the vega-lite specification
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.vega-lite-views
  (:require
   [practicalli.data-gov-uk-depricated :as data-gov-uk-depricated]))



;; Line plot chart
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TODO: change to function

(def line-plot-uk-countries-cumulative-cases
  "Transform data for visualization"
  {:height   400
   :width    400
   :mark     "line"
   :data     {:values data-gov-uk-depricated/covid19-uk-countries-all-cumulative-cases}
   :encoding {:x     {:field "day" :type "quantitative"}
              :y     {:field "cases" :type "quantitative"}
              :color {:field "location" :type "nominal"}}})



;; Stacked bar chart
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TODO: change to function

(def stacked-bar-uk-countries-cumulative-cases
  {:mark     "bar"
   :data     {:values data-gov-uk-depricated/covid19-uk-countries-all-cumulative-cases}
   :encoding {:x     {:field "day"
                      :type  "ordinal"}
              :y     {:aggregate "location"
                      :field     "cases"
                      :type      "quantitative"}
              :color {:field "location"
                      :type  "nominal"}}})


;; GeoJSON view
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn geo-json-view
  "Geographic visualization generator

  Arguments:
  - combined data set of GeoJSON and Cases
  - maximum integer value for scale
  Returns: Oz view hash-map"

  [data-set max-scale]

  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values data-set
              :format {:type     "json"
                       :property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color
              {:field "Cases",
               :type  "quantitative"
               :scale {:domain [0 max-scale]}}
              :tooltip [{:field "Location" :type "nominal"}
                        {:field "Cases" :type "quantitative"}]}})
