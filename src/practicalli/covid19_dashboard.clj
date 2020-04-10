(ns practicalli.covid19-dashboard
  (:gen-class)
  (:require [oz.core :as oz]
            [practicalli.vega-lite-components :as views]
            [practicalli.view-helpers :as view-helpers]
            [practicalli.data-gov-uk :as data-gov-uk]))


(def dashboard-corvid19-uk
  [:div
   ;; Web page meta data
   [:link {:rel  "stylesheet"
           :href "https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css"}]

   ;; Web page structure and content

   ;; Heading
   [:section {:class "hero is-dark is-bold"}
    [:div {:class "hero-body"}
     [:div {:class "container"}
      [:h1 {:class "title is-family-primary"}
       "COVID19 Tracker - Mock data"]
      [:h2 {:class "subtitle"}
       "Data will be extracted from "
       [:a {:href "https://www.gov.uk/government/publications/covid-19-track-coronavirus-cases"} "Gov.UK"]]]]]

   ;; Daily Headline figures
   [:section {:class "section has-text-centered"}
    #_[:h1 (str "Headline figures for: " (get covid-uk-daily-indicators-map "DateVal"))]

    ;; UK Totals
    [:div {:class "columns"}
     ;; UK cumulative totals
     (view-helpers/headline-card-total
       data-gov-uk/covid19-uk-data-latest-fixed
       {:name "Cumulative UK Totals " :type "TotalUK"})

     (view-helpers/headline-card-total
       data-gov-uk/covid19-uk-data-latest-fixed
       {:name "Daily Totals" :type "DailyUK"})]

    ;; Country Cases
    [:div {:class "columns"}
     (map #(view-helpers/headline-card-country
             data-gov-uk/covid19-uk-data-latest %)
          [{:name "England" :alias "England"}
           {:name "Scotland" :alias "Scotland"}
           {:name "Wales" :alias "Wales"}
           {:name "Northern Ireland" :alias "NI"}])]]

   ;; Oz visualization
   [:section {:class "section"}
    [:vega-lite views/line-plot]
    [:vega-lite views/stacked-bar]]])



(defn -main
  "I don't do a whole lot ... yet."
  [view]
  (oz/view! view))

(-main dashboard-corvid19-uk)
