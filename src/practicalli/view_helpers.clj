;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; practicalli.view-helpers
;;
;; Helper functions to minimize duplicate code
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.view-helpers
  (:require [practicalli.data-gov-uk-depricated :as data-gov-uk-deprecated]))


;; Bulma styled components
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn headline-card-total
  "Generate a card for headline figures, to be used in an Oz dashboard"
  [data-set totals]
  [:div {:class "column is-half"}
   [:div {:class "card"}
    [:div {:class "card-content"}
     [:div {:class "title "}
      [:h2 {:class "is-family-primary"}
       (:name totals)]
      [:h1 {:class "has-text-primary is-family-primary"
            :style {:font-size "5rem"}}
       (str (get data-set (str (:type totals) "Cases")))]
      [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
       (str (get data-set (str (:type totals) "Deaths")))]]]]]
  )


(defn headline-card-country
  "Generate a card for headline figures, to be used in an Oz dashboard"
  [data-set country]
  [:div {:class "column is-one-quarter"}
   [:div {:class "card"}
    [:div {:class "card-content has-text-centered"}
     [:div {:class "title"}
      [:h2 {:class "is-family-primary"}
       (:name country)]
      [:h1 {:class "is-family-primary has-text-primary"}
       (str (get data-set (str (:alias country) "Cases")))]
      [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
       (str (get data-set (str (:alias country) "Deaths")))]]]]]
  )


;; Web page meta data
(def include-bulma-css
  [:link {:rel  "stylesheet"
          :href "https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css"}])


;; Webpage heading
(def webpage-heading
  [:section {:class "hero is-dark is-bold"}
   [:div {:class "hero-body"}
    [:div {:class "container"}
     [:h1 {:class "title is-family-primary"}
      "COVID19 Tracker - Mock data"]
     [:h2 {:class "subtitle"}
      "Data will be extracted from "
      [:a {:href "https://www.gov.uk/government/publications/covid-19-track-coronavirus-cases"} "Gov.UK"]]]]])


(defn headline-figures
  []
  [:section {:class "section has-text-centered"}
   #_[:h1 (str "Headline figures for: " (get covid-uk-daily-indicators-map "DateVal"))]

   ;; UK Totals
   [:div {:class "columns"}
    ;; UK cumulative totals
    (headline-card-total
      data-gov-uk-deprecated/covid19-uk-data-latest-fixed
      {:name "Cumulative UK Totals " :type "TotalUK"})

    (headline-card-total
      data-gov-uk-deprecated/covid19-uk-data-latest-fixed
      {:name "Daily Totals" :type "DailyUK"})]

   ;; Country Cases
   [:div {:class "columns"}
    (map #(headline-card-country
            data-gov-uk-deprecated/covid19-uk-data-latest %)
         [{:name "England" :alias "England"}
          {:name "Scotland" :alias "Scotland"}
          {:name "Wales" :alias "Wales"}
          {:name "Northern Ireland" :alias "NI"}])]])





;; Oz View helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn maximum-cases
  "Calculates the maximum value of cases.
  Used to calculate top end of scale in GeoJSON view"
  [data-set]
  (apply max
         (map
           #(Integer/parseInt
              (get % "Cumulative lab-confirmed cases"))
           data-set)))
