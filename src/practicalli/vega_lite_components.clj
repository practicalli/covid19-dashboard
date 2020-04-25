;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vega-lite components
;; Named components generating the vega-lite specification
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.vega-lite-components
  (:require [practicalli.data-mock :as data-mock]
            [practicalli.data-gov-uk :as data-gov-uk]))



;; Line plot
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def line-plot
  "Transform data for visualization"
  {:mark     "line"
   :data     {:values (data-mock/covid19-data-set "England" "Scotland" "Wales" "Northern Ireland")}
   :encoding {:x     {:field "day" :type "quantitative"}
              :y     {:field "cases" :type "quantitative"}
              :color {:field "location" :type "nominal"}}})


(def line-plot-uk-countries-cumulative-cases
  "Transform data for visualization"
  {:height   400
   :width    400
   :mark     "line"
   :data     {:values data-gov-uk/covid19-uk-countries-all-cumulative-cases}
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


;; View Vega-lite components
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment

  (oz/view! line-plot)
  (oz/view! stacked-bar)

)
