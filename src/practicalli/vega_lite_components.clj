;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vega-lite components
;; Named components generating the vega-lite specification
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.vega-lite-components
  (:require [practicalli.data-mock :as data-mock]
            [practicalli.data-gov-uk-depricated :as data-gov-uk-depricated]






;; Line plot chart
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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


(def geo-map-uk-england-local-area-districts-date-specific
  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values data-gov-uk/england-lad-geojson-with-cases-date-specific-lad
              :format {:property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color
              {:field "Cases",
               :type  "quantitative"
               :scale {:domain [0 (data-gov-uk/maximum-cases
                                    data-gov-uk/covid19-cases-uk-local-authority-district-date-specific)
                                #_(data-gov-uk/maximum-cases data-gov-uk/gov-uk-date-specific-lad)]}}
              :tooltip [{:field "Location" :type "nominal"}
                        {:field "Cases" :type "quantitative"}]
              }})
