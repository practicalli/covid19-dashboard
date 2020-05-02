(ns practicalli.vega-lite-components-depricated
  (:require
   [practicalli.data-mock :as data-mock]))


;; Line plot
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def line-plot
  "Transform data for visualization"
  {:mark     "line"
   :data     {:values (data-mock/covid19-data-set "England" "Scotland" "Wales" "Northern Ireland")}
   :encoding {:x     {:field "day" :type "quantitative"}
              :y     {:field "cases" :type "quantitative"}
              :color {:field "location" :type "nominal"}}})



;; Stacked bar
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def stacked-bar
  {:mark     "bar"
   :data     {:values (data-mock/covid19-data-set "England" "Scotland" "Wales" "Norther Ireland")}
   :encoding {:x     {:field "day"
                      :type  "ordinal"}
              :y     {:aggregate "location"
                      :field     "cases"
                      :type      "quantitative"}
              :color {:field "location"
                      :type  "nominal"}}})
