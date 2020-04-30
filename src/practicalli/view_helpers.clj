;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; practicalli.view-helpers
;;
;; Helper functions to minimize duplicate code
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(ns practicalli.view-helpers)


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
