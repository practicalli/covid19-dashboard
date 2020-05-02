(ns practicalli.design-journal
  (:require

   ;; Extract and Transform data
   [clojure.java.io   :as io]
   [clojure.data.csv  :as csv]
   [semantic-csv.core :as semantic-csv]
   [jsonista.core     :as json]

   ;; Visualization
   [oz.core :as oz]
   ))


;; Oz server listening on a websocket for views to display.
;; Enables fast feedback for visualization
(oz/start-server!)



;; Data Generation

(defn mock-data-set
  "Generates a set of mock data for each name

  Arguments: names as strings, names used in keys
  Returns: Sequence of maps, each representing confirmed cases"
  [& locations]
  (for [location locations
        day      (range 20)]
    {:day      day
     :location location
     :cases    (+ (Math/pow (* day (count location)) 0.8)
                  (rand-int (count location)))}))

(mock-data-set "England" "Scotland" "Wales" "Northern Ireland")
;; => ({:day 0, :location "England", :cases 1.0}
;;     {:day 1, :location "England", :cases 7.743276393803367}
;;     {:day 2, :location "England", :cases 13.258523872989459}
;;     {:day 3, :location "England", :cases 16.422875300666448}
;;     {:day 4, :location "England", :cases 18.378925219250924}
;;     {:day 5, :location "England", :cases 20.189151347155786}
;;     {:day 6, :location "England", :cases 19.888381054913122}
;;     {:day 7, :location "England", :cases 26.498670948012276}
;;     {:day 8, :location "England", :cases 27.03516289842348}
;;     {:day 9, :location "England", :cases 29.508850275948053}
;;     {:day 10, :location "England", :cases 32.9280507756976}
;;     {:day 11, :location "England", :cases 34.299260572778856}
;;     {:day 12, :location "England", :cases 34.62768266080517}
;;     {:day 13, :location "England", :cases 42.9175705807045}
;;     {:day 14, :location "England", :cases 45.17246133441246}
;;     {:day 15, :location "England", :cases 42.39533859324643}
;;     {:day 16, :location "England", :cases 43.58875032686557}
;;     {:day 17, :location "England", :cases 45.75489563854074}
;;     {:day 18, :location "England", :cases 47.89569020671064}
;;     {:day 19, :location "England", :cases 50.012816499808885}
;;     {:day 0, :location "Scotland", :cases 1.0}
;;     {:day 1, :location "Scotland", :cases 11.278031643091577}
;;     {:day 2, :location "Scotland", :cases 11.18958683997628}
;;     {:day 3, :location "Scotland", :cases 19.71068609258575}
;;     {:day 4, :location "Scotland", :cases 19.000000000000004}
;;     {:day 5, :location "Scotland", :cases 25.127049995800743}
;;     {:day 6, :location "Scotland", :cases 25.13058987556147}
;;     {:day 7, :location "Scotland", :cases 26.03516289842348}
;;     {:day 8, :location "Scotland", :cases 27.85761802547598}
;;     {:day 9, :location "Scotland", :cases 31.610188015018984}
;;     {:day 10, :location "Scotland", :cases 38.30212829607493}
;;     {:day 11, :location "Scotland", :cases 38.940667420160906}
;;     {:day 12, :location "Scotland", :cases 41.53159496449108}
;;     {:day 13, :location "Scotland", :cases 45.07964401264505}
;;     {:day 14, :location "Scotland", :cases 49.58875032686557}
;;     {:day 15, :location "Scotland", :cases 47.06223395648531}
;;     {:day 16, :location "Scotland", :cases 52.50293012833275}
;;     {:day 17, :location "Scotland", :cases 51.91328587177879}
;;     {:day 18, :location "Scotland", :cases 57.29543283815009}
;;     {:day 19, :location "Scotland", :cases 56.65124317675735}
;;     {:day 0, :location "Wales", :cases 0.0}
;;     {:day 1, :location "Wales", :cases 7.623898318388478}
;;     {:day 2, :location "Wales", :cases 8.309573444801934}
;;     {:day 3, :location "Wales", :cases 8.727161387290321}
;;     {:day 4, :location "Wales", :cases 11.98560543306118}
;;     {:day 5, :location "Wales", :cases 14.132639022018838}
;;     {:day 6, :location "Wales", :cases 19.194870523363548}
;;     {:day 7, :location "Wales", :cases 19.189151347155786}
;;     {:day 8, :location "Wales", :cases 22.127049995800743}
;;     {:day 9, :location "Wales", :cases 25.016965485301043}
;;     {:day 10, :location "Wales", :cases 22.86525259636632}
;;     {:day 11, :location "Wales", :cases 28.676874454922782}
;;     {:day 12, :location "Wales", :cases 28.45580618665162}
;;     {:day 13, :location "Wales", :cases 30.205297528345778}
;;     {:day 14, :location "Wales", :cases 33.9280507756976}
;;     {:day 15, :location "Wales", :cases 31.626345475706252}
;;     {:day 16, :location "Wales", :cases 36.30212829607493}
;;     {:day 17, :location "Wales", :cases 36.95707936800063}
;;     {:day 18, :location "Wales", :cases 39.59266228400805}
;;     {:day 19, :location "Wales", :cases 42.210162462449645}
;;     {:day 0, :location "Northern Ireland", :cases 14.0}
;;     {:day 1, :location "Northern Ireland", :cases 19.18958683997628}
;;     {:day 2, :location "Northern Ireland", :cases 29.000000000000004}
;;     {:day 3, :location "Northern Ireland", :cases 26.13058987556147}
;;     {:day 4, :location "Northern Ireland", :cases 35.85761802547598}
;;     {:day 5, :location "Northern Ireland", :cases 44.30212829607493}
;;     {:day 6, :location "Northern Ireland", :cases 52.53159496449108}
;;     {:day 7, :location "Northern Ireland", :cases 46.58875032686557}
;;     {:day 8, :location "Northern Ireland", :cases 58.50293012833275}
;;     {:day 9, :location "Northern Ireland", :cases 57.29543283815009}
;;     {:day 10, :location "Northern Ireland", :cases 60.98237309421565}
;;     {:day 11, :location "Northern Ireland", :cases 69.57633653571948}
;;     {:day 12, :location "Northern Ireland", :cases 70.08740340207163}
;;     {:day 13, :location "Northern Ireland", :cases 77.5238144704248}
;;     {:day 14, :location "Northern Ireland", :cases 77.89242230085388}
;;     {:day 15, :location "Northern Ireland", :cases 84.19900743499228}
;;     {:day 16, :location "Northern Ireland", :cases 92.44850628946526}
;;     {:day 17, :location "Northern Ireland", :cases 94.64517938986725}
;;     {:day 18, :location "Northern Ireland", :cases 95.79273815672464}
;;     {:day 19, :location "Northern Ireland", :cases 110.8944421913114})


;; Line plot
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def line-plot
  "Transform data for visualization"
  {:mark     "line"
   :data     {:values (mock-data-set "England" "Scotland" "Wales" "Northern Ireland")}
   :encoding {:x     {:field "day" :type "quantitative"}
              :y     {:field "cases" :type "quantitative"}
              :color {:field "location" :type "nominal"}}})

;; Notes:
;; The encoding field names match the keywords in the data values
;; TODO: review the available types

;; Send visualization to Oz server
(oz/view! line-plot)


;; Stacked bar
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def stacked-bar
  {:mark     "bar"
   :data     {:values (mock-data-set "England" "Scotland" "Wales" "Norther Ireland")}
   :encoding {:x     {:field "day"
                      :type  "ordinal"}
              :y     {:aggregate "location"
                      :field     "cases"
                      :type      "quantitative"}
              :color {:field "location"
                      :type  "nominal"}}})

(oz/view! stacked-bar)


;; Dashboard
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Define a dashboard using the hiccup syntax, including calls to the two views.


(def dashboard
  [:div
   [:h1 "COVID19 Tracker - Mock data"]
   [:p "Mock data to experiment with types of views"]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:vega-lite line-plot]
    [:vega-lite stacked-bar]]])

(oz/view! dashboard)


;; * :bar - Histogram
;; * Multi-series Line Chart
;; * Stripplot
;; * Slope Graph
;; * Binned Scatterplot
;; * Area Chart


;; Reading in CSV data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(require '[clojure.java.io :as io])
(require '[clojure.data.csv :as csv])


(def covid-uk-daily-indicators
  (csv/read-csv
    (slurp
      (io/resource "data/daily-indicators.csv"))))


covid-uk-daily-indicators
;; => (["DateVal"
;;      "TotalUKCases"
;;      "NewUKCases"
;;      "TotalUKDeaths"
;;      "DailyUKDeaths"
;;      "EnglandCases"
;;      "EnglandDeaths"
;;      "ScotlandCases"
;;      "ScotlandDeaths"
;;      "WalesCases"
;;      "WalesDeaths"
;;      "NICases"
;;      "NIDeaths"]
;;     ["3/27/2020"
;;      "14,543"
;;      "  2,885"
;;      "759"
;;      "181"
;;      " 12,288"
;;      "    679"
;;      "1,059"
;;      "33"
;;      "921"
;;      "34"
;;      "275"
;;      "13"])

(zipmap (first covid-uk-daily-indicators) (second covid-uk-daily-indicators))
;; => {"TotalUKDeaths" "759", "DailyUKDeaths" "181", "EnglandCases" " 12,288", "EnglandDeaths" "    679", "TotalUKCases" "14,543", "WalesCases" "921", "ScotlandDeaths" "33", "DateVal" "3/27/2020", "NewUKCases" "  2,885", "WalesDeaths" "34", "NICases" "275", "ScotlandCases" "1,059", "NIDeaths" "13"}

(def covid-uk-daily-indicators-map
  (zipmap (first covid-uk-daily-indicators) (second covid-uk-daily-indicators)))



;; Adding daily indicators
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def dashboard-headlines
  [:div
   [:h1 "COVID19 Tracker - Mock data"]
   [:p (str "Headline figures for: " (get covid-uk-daily-indicators-map "DateVal"))]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:h2 (str "Total UK Cases:"  (get covid-uk-daily-indicators-map "TotalUKCases"))]
    [:h2 (str "England Cases: "  (get covid-uk-daily-indicators-map "EnglandCases"))]
    [:h2 (str "Scotland Cases: "  (get covid-uk-daily-indicators-map "ScotlandCases"))]
    ]
   [:div {:style {:display "flex" :flex-direction "row"}}
    [:vega-lite line-plot]
    [:vega-lite stacked-bar]]])

(oz/view! dashboard-headlines)


;; Improving design with a CSS framework
;; Including CSS from a content delivery network (CDN)

(def dashboard-headlines-bootstrap
  [:div
   [:link {:rel         "stylesheet"
           :href        "https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
           :integrity   "sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh"
           :crossorigin "anonymous"}]
   [:div {:class "jumbotron"}
    [:h1 {:class "display-4"}
     "COVID19 Tracker - Mock data"]]

   [:h3 (str "Headline figures for: " (get covid-uk-daily-indicators-map "DateVal"))]

   [:div {:style {:display "flex" :flex-direction "row"}}
    [:div {:class "card"}
     [:div {:class "card-title"}
      [:h2 (str "Total UK Cases:"  (get covid-uk-daily-indicators-map "TotalUKCases"))]]]
    [:div {:class "card"}
     [:div {:class "card-title"}
      [:h2 (str "Scotland Cases: "  (get covid-uk-daily-indicators-map "ScotlandCases"))]   ]]
    [:div {:class "card"}
     [:div {:class "card-title"}
      [:h2 (str "England Cases: "  (get covid-uk-daily-indicators-map "EnglandCases"))]]]]

   [:div {:style {:display "flex" :flex-direction "row"}}
    [:vega-lite line-plot]
    [:vega-lite stacked-bar]]])


(oz/view! dashboard-headlines-bootstrap)


;; Using Bulma instead of Bootstrap, as its more developer friendly
;; https://bulma.io/

(def dashboard-headlines-bulma
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
     [:div {:class "column is-half"}
      [:div {:class "card"}
       [:div {:class "card-content"}
        [:div {:class "title "}
         [:h2 {:class "is-family-primary"}
          "Cumulative UK Totals "]
         [:h1 {:class "has-text-primary is-family-primary"
               :style {:font-size "5rem"}}
          (str (get covid-uk-daily-indicators-map "TotalUKCases"))]
         [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
          "5,373"]]]]]

     ;; UK daily totals
     [:div {:class "column is-half"}
      [:div {:class "card"}
       [:div {:class "card-content"}
        [:div {:class "title"}
         [:h2 {:class "is-family-primary"}
          "Daily Totals"]
         [:h1 {:class "has-text-primary is-family-primary"
               :style {:font-size "5rem"}}
          (str (str (get covid-uk-daily-indicators-map "DailyUKDeaths")))]
         [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
          "439"]]]]]]

    ;; Country Cases

    [:div {:class "columns"}

     ;; Total cases in England
     [:div {:class "column is-one-quarter"}
      [:div {:class "card"}
       [:div {:class "card-content"}
        [:div {:class "title"}
         [:h2 {:class "is-family-primary"}
          "England"]
         [:h1 {:class "has-text-primary is-family-primary"}
          (str (get covid-uk-daily-indicators-map "EnglandCases"))]
         [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
          (get covid-uk-daily-indicators-map "EnglandDeaths")]]]]]

     ;; Total Cases in Scotland
     [:div {:class "column is-one-quarter"}
      [:div {:class "card"}
       [:div {:class "card-content has-text-centered"}
        [:div {:class "title"}
         [:h2 {:class "is-family-primary"}
          "Scotland"]
         [:h1 {:class "has-text-primary is-family-primary"}
          (str (get covid-uk-daily-indicators-map "ScotlandCases"))]
         [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
          (get covid-uk-daily-indicators-map "ScotlandDeaths")]]]]]

     ;; Total Cases in Wales
     [:div {:class "column is-one-quarter"}
      [:div {:class "card"}
       [:div {:class "card-content has-text-centered"}
        [:div {:class "title"}
         [:h2 {:class "is-family-primary"}
          "Wales"]
         [:h1 {:class "has-text-primary is-family-primary"}
          (str (get covid-uk-daily-indicators-map "WalesCases"))]
         [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
          (str (get covid-uk-daily-indicators-map "WalesDeaths"))]]]]]

     ;; Total Cases in Northern Ireland
     [:div {:class "column is-one-quarter"}
      [:div {:class "card"}
       [:div {:class "card-content has-text-centered"}
        [:div {:class "title"}
         [:h2 {:class "is-family-primary"}
          "Northern Ireland"]
         [:h1 {:class "is-family-primary has-text-primary"}
          (str (get covid-uk-daily-indicators-map "NICases"))]
         [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
          (str (get covid-uk-daily-indicators-map "NIDeaths"))]]]]]    ]

    ]
   ;; End of Headline figures

   ;; Oz visualization
   [:section {:class "section"}
    [:vega-lite line-plot]
    [:vega-lite stacked-bar]]])


(oz/view! dashboard-headlines-bulma)



;; Refactor dashboard with section generators
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn headline-card-total
  "Generate a card for headline figures, to be used in an Oz dashboard"
  [totals]
  [:div {:class "column is-half"}
   [:div {:class "card"}
    [:div {:class "card-content"}
     [:div {:class "title "}
      [:h2 {:class "is-family-primary"}
       (:name totals)]
      [:h1 {:class "has-text-primary is-family-primary"
            :style {:font-size "5rem"}}
       (str (get covid-uk-daily-indicators-map (:type totals)))]
      [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
       "5,373"]]]]]
  )


(defn headline-card-country
  "Generate a card for headline figures, to be used in an Oz dashboard"
  [country]
  [:div {:class "column is-one-quarter"}
   [:div {:class "card"}
    [:div {:class "card-content has-text-centered"}
     [:div {:class "title"}
      [:h2 {:class "is-family-primary"}
       (:name country)]
      [:h1 {:class "is-family-primary has-text-primary"}
       (str (get covid-uk-daily-indicators-map (str (:alias country)) "Cases"))]
      [:h2 {:style {:color "hsl(300, 100%, 25%)"}}
       (str (get covid-uk-daily-indicators-map (str (:alias country)) "Deaths"))]]]]]
  )



(def dashboard-headlines-bulma-generators
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
     (headline-card-total {:name "Cumulative UK Totals " :type "TotalUKCases"})

     ;; UK daily totals
     (headline-card-total {:name "Daily Totals" :type "DailyUKDeaths"})]

    ;; Country Cases
    [:div {:class "columns"}

     ;; Total cases in England
     (headline-card-country {:name "England" :alias "England"})

     ;; Total Cases in Scotland
     (headline-card-country {:name "Scotland" :alias "Scotland"})

     ;; Total Cases in Wales
     (headline-card-country {:name "Wales" :alias "Wales"})

     ;; Total Cases in Northern Ireland
     (headline-card-country {:name "Northern Ireland" :alias "NI"})
     ]
    ]
   ;; End of Headline figures

   ;; Oz visualization
   [:section {:class "section"}
    [:vega-lite line-plot]
    [:vega-lite stacked-bar]]])


(oz/view! dashboard-headlines-bulma-generators)





;; Level not working as shown on the bulma website
;; (def dashboard-bulma-level
;;   [:nav {:class "level"}
;;    [:div {:class "level-item has-text-centered"}
;;     [:p {:class "heading"} "England"]
;;     [:p {:class "title has-text-primary is-size-1"} "12,288"]
;;     [:p {:class "title has-text-primary"} "12,288"]]
;;    [:div {:class "level-item has-text-centered"}
;;     [:p {:class "heading"} "England"]
;;     [:p {:class "title has-text-primary is-size-1"} "12,288"]
;;     [:p {:class "title has-text-primary"} "12,288"]]]
;;   )

;; (oz/view! dashboard-bulma-level)

;; TODO:
;; Set own colors
;; Add Geographic map with data regions
;; Investigate other graphs








;; Alternative: semantic-csv
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(require '[semantic-csv.core :as semantic])

(def data
  (with-open [in-file (io/reader "resources/data/daily-indicators.csv")]
    (->>
      (csv/parse-csv in-file)
      (semantic/remove-comments)
      (semantic/mappify)
      #_(semantic/cast-with {:this ->int})
      doall)))

(csv/read-csv )

(def covid-uk-daily-indicators-maps
  (semantic/mappify
    (csv/read-csv
      (slurp
        (io/resource "data/daily-indicators.csv")))))

covid-uk-daily-indicators-map

;; As a threading macro

(->> "data/daily-indicators.csv"
     io/resource
     slurp
     csv/read-csv
     semantic/mappify
     )
;; => ({:TotalUKCases "14,543", :EnglandDeaths "    679", :ScotlandCases "1,059", :DateVal "3/27/2020", :NIDeaths "13", :TotalUKDeaths "759", :DailyUKDeaths "181", :NewUKCases "  2,885", :WalesCases "921", :WalesDeaths "34", :NICases "275", :ScotlandDeaths "33", :EnglandCases " 12,288"})



(def covid-uk-daily-indicators-map-converted
  (->> "data/daily-indicators.csv"
       io/resource
       slurp
       csv/read-csv
       semantic/mappify
       semantic/->long )
  )





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; GOV.UK Data set change - 16 April 2020
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Data format has changed again

;; Cases combined country, region, local area districts
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Convert cases to a sequence of vectors

(defn csv->clj
  [data-file]
  (csv/read-csv
    (slurp
      (io/resource data-file))))

;; Returns a sequence of vectors, the first vector the headings,
;; all other vectors are data for country, region or local authority
(def covid19-cases-england-combined
  (csv->clj "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"))

;; Return just the country data sets
(def covid19-cases-uk-countries
  (filter #(some #{"Country"} %)
          covid19-cases-england-combined))

;; Return region data
(def covid19-cases-uk-regions
  (filter #(some #{"Region"} %)
          covid19-cases-england-combined))

;; Return just the local authority data
;; remove the country and region data and that should be what is left (including the heading)
;; (def covid19-cases-uk-local-authorities
;;   (remove #(some #{"Country" "Region"} %)
;;           covid19-cases-england-combined))

(def covid19-cases-uk-local-authorities
  (filter #(some #{"Upper tier local authority"} %)
          covid19-cases-england-combined))

(def gov-uk-data-headings
  (first covid19-cases-england-combined))


;; A helper function would be useful right now

(defn uk-data-view
  "Specific view of a given data set based on location"
  [data-set location]
  (filter #(some #{location} %) data-set))


(def covid19-cases-uk-countries
  (uk-data-view covid19-cases-england-combined "Country"))

(def covid19-cases-uk-regions
  (uk-data-view covid19-cases-england-combined "Region"))

(def covid19-cases-uk-local-authorities
  (uk-data-view covid19-cases-england-combined "Upper tier local authority"))




;; New data as Clojure hash-maps
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; convert data to maps with headings as keys
;; semantic-csv

(semantic-csv/mappify
  (csv/read-csv
    (slurp
      (io/resource "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"))))

;; Example data

;; :Area name = "England"
;; :Area code = "E92000001"
;; :Area type = "Country"
;; :Specimen date = "2020-04-14"
;; :Daily lab-confirmed cases = "134"
;; :Cumulative lab-confirmed cases = "76371"


(->> "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"
     io/resource
     slurp
     csv/read-csv
     semantic-csv/mappify)


;; Define a function (using threading macro to show the sequence of actions)

(defn csv->clj-hash-map
  "Convert CSV file to sequence of hash maps.
  Each hash-map uses the heading text as a key
  for each element in the row of data.

  Return: a sequence of hash-maps"
  [data-source]
  (->> data-source
       io/resource
       slurp
       csv/read-csv
       semantic-csv/mappify))

(def covid19-cases-uk-combined
  (csv->clj-hash-map "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"))


(defn uk-data-view-hash-map
  "Specific view of a given data set based on location"
  [data-set location]
  (filter #(= location (:Area type  %) data-set)))

(uk-data-view-hash-map covid19-cases-uk-combined "Country")

;; This doesn't work as keywords do not have spaces in them.
;; Reviewing the semantic-csv library, we can ask it not to convert headings into keywords
;; using {:keyify false} as the first argument to mapify


{"key with space" "value"}

;; Refactor the cvs->clj-hash-map

(defn csv->clj-hash-map
  "Convert CSV file to sequence of hash maps.
  Each hash-map uses the heading text as a key
  for each element in the row of data.

  Return: a sequence of hash-maps"
  [data-source]
  (->> data-source
       io/resource
       slurp
       csv/read-csv
       (semantic-csv/mappify {:keyify false} )))

(def covid19-cases-uk-combined
  (csv->clj-hash-map "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"))

(keys (first covid19-cases-uk-combined))
;; => ("Area name" "Area code" "Area type" "Specimen date" "Daily lab-confirmed cases" "Cumulative lab-confirmed cases")


(filter #(= "Country" (get % "Area type")) covid19-cases-uk-combined)


(defn uk-data-view-hash-map
  "Specific view of a given data set based on location"
  [data-set location]
  (filter #(= location (get % "Area type")) data-set))

(def covid19-cases-uk-contries
  (uk-data-view-hash-map covid19-cases-uk-combined "Country"))

(def covid19-cases-uk-regions
  (uk-data-view-hash-map covid19-cases-uk-combined "Regions"))

(def covid19-cases-uk-englad-lad
  (uk-data-view-hash-map covid19-cases-uk-combined "Upper tier local authority"))





;; GEOJSON data sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; GeoJSON data not working
;; (oz/view! {:data {:url    "/public/data/uk-england.geo.json"
;;                   :format {:type     "json"
;;                            :property "features"}}
;;            :mark "geoshape"})


;; Dowload from UK office for National Statistics

;; Does not work correctly with Oz
;; Opens a blue square.

;; Opening in the vega editor https://vega.github.io/editor/#/
;; Invalid specification {"$schema":"https://vega.github.io/schema/vega-lite/v4.json"}. Make sure the specification includes at least one of the following properties: "mark", "layer", "facet", "hconcat", "vconcat", "concat", or "repeat".
;; http://geoportal.statistics.gov.uk/datasets/473aefdcee19418da7e5dbfdeacf7b90_3?geometry=-39.745%2C46.015%2C34.874%2C63.432
;; GeoJSON file
;; https://opendata.arcgis.com/datasets/473aefdcee19418da7e5dbfdeacf7b90_3.geojson

;; (oz/view! {:data {:url    "/geo-data/NUTS_Level_3_January_2018_Super_Generalised_Clipped_Boundaries_in_the_United_Kingdom.geojson"
;;                   :format {:type     "json"
;;                            :property "features"}}
;;            :mark "geoshape"})


;; Minimum viable geographic visualization
;; Googling to find a workable GEO.json file for the UK
;; https://raw.githubusercontent.com/martinjc/UK-GeoJSON/master/json/administrative/eng/lad.json

;; Gov.uk - https://data.gov.uk/
;; http://geoportal1-ons.opendata.arcgis.com/datasets/8d3a9e6e7bd445e2bdcc26cdf007eac7_2.geojson

;; Regions (December 2015) Full Clipped Boundaries in England
;; http://geoportal1-ons.opendata.arcgis.com/datasets/8d3a9e6e7bd445e2bdcc26cdf007eac7_0.geojson

;; (def gb-uk-geo-json-map "/public/data/uk-england-lad.geo.json")

(def geo-map-great-britain
  {:height 400
   :width  360
   :data   {:url "http://geoportal1-ons.opendata.arcgis.com/datasets/8d3a9e6e7bd445e2bdcc26cdf007eac7_2.geojson"

            :format {:type     "json"
                     :property "features"}}
   :mark "geoshape"})

(def geo-map-england-full-clipped-boundaries
  {:height 400
   :width  360
   :data   {:url "http://geoportal1-ons.opendata.arcgis.com/datasets/8d3a9e6e7bd445e2bdcc26cdf007eac7_0.geojson"

            :format {:type     "json"
                     :property "features"}}
   :mark "geoshape"})



;; Minimum viable geographic visualization
;; Googling to find a workable GEO.json file for the UK
;; https://raw.githubusercontent.com/martinjc/UK-GeoJSON/master/json/administrative/eng/lad.json

;; (def gb-uk-geo-json-map "/public/data/uk-england-lad.geo.json")

;; basic structure with no data applied

(def uk-england-local-authorities
  {:data {:url    "/geo-data/uk-england-lad.geo.json"
          :format {:type     "json"
                   :property "features"}}

   :mark   {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :height 1000
   :width  920})

(oz/view! uk-england-local-authorities)


;; Transform the GeoJSON data to Clojure
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Read in the json data from the GEOJSON file
;; The GEOJSON is converted to a CLojure data structure

(def england-lad-geojson
  (json/read-value
    (io/resource "public/geo-data/uk-england-lad.geo.json")
    (json/object-mapper {:decode-key-fn true})))


;; data structure of the transformed GeoJSON file
;; Essentially it is a map with a :featurs key
;; which is associated the value of a vector of maps

{:features
 [{:properties {:LAD13CD  "Local Authority Disctrict December 2012 - eg. E060000001"
                :LAD13NM  "Local Authority District Name - eg. Hartlepool"
                :LAD13CDO ""}
   :type       "Feature"
   :geometry   {:coordinates [[[[0 0] [1 1]]]]}}]}


;; We can still use the transformed GEOJSON data,
;; using a :values key in the :data section

(oz/view!
  {:title  {:text "COVID19 cases in England Hospitals"}
   :height 1000
   :width  920
   :data   {:name   "England"
            :values england-lad-geojson
            :format {:property "features"}},
   :mark   {:type "geoshape" :stroke "white" :strokeWidth 0.5}})


;; For each feature we can add keys that contain values from the Gov.uk data



;; Combine GeoJSON and Gov.uk data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; So now we just need to merge the GeoJSON data with our country data
;; and hope everything lines up

;; Map over each feature in the GeoJSON file
;; associate the keys from the uk data set

;; HINT: Use the cider-inspect tool to navigate the UK data structure
;; to grab the keys it uses

;; Each property has the following structure

{:properties {:LAD13CD  "Local Authority Disctrict December 2012 - eg. E060000001"
              :LAD13NM  "Local Authority District Name - eg. Hartlepool"
              :LAD13CDO ""}
 :type       "Feature"
 :geometry   {:coordinates [[[[0 0] [1 1]]]]}
 :cases      27}



;; return the local area district name
(get-in (first (:features england-lad-geojson)) [:properties :LAD13NM])


;; UK data set elements

;; "Area name" = "Barking and Dagenham"
;; "Area code" = "E09000002"
;; "Area type" = "Upper tier local authority"
;; "Specimen date" = "2020-04-14"
;; "Daily lab-confirmed cases" = "4"
;; "Cumulative lab-confirmed cases" = "347"


;; transform the GeoJSON data to include cases information for each feature
;; - map over each feature in turn
;; -- associate a :Cases key that has the district name and case total for that district
(mapv
  (fn [feature]
    (assoc
      feature
      :Cases (get
               (get covid19-cases-uk-englad-lad
                    (get-in feature [:properties :LAD13NM]))
               "Cumulative lab-confirmed cases")))

  england-lad-geojson)


;; If this is done as an update function, then we can update each feature
;; with the cases data from uk data set.

;; Simply put it will be of the form
;; (update geojson-data :features update-uk-data)

;; the update-uk-data is a little more complex though :)

(defn update-cases-data
  [geo-json-data-set cases-data-set]

  (update
    geo-json-data-set
    :features
    (fn [features]  ;; as we are using update, features represents the whole geo-json data set
      (mapv
        (fn [feature]
          (assoc
            feature
            :Cases (get (filter #(= (:LAD13NM (:properties feature)) ;; name of the local authority
                                    (get % "Area name"))
                                cases-data-set)
                        "Cumulative lab-confirmed cases" (rand-int 12000)))

          )
        features
        ))))


(def england-lad-geojson-with-cases
  (update-cases-data england-lad-geojson covid19-cases-uk-englad-lad))


;; now use england-lad-geojson-with-cases as the data for our view.


;; Create an Oz view with the combind GeoJSON and Gov.uk data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(oz/view!
  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values england-lad-geojson-with-cases
              :format {:property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color {:field "Cases",
                      :type  "quantitative"
                      :scale {:domain [0 120000]}}
              }})

;; The scale is hard coded, so it would need to be adjusted
;; each time the data changes to make sure its an appropriate size
;; Use the data to determine the maximum value

(map
  #(get % "Cumulative lab-confirmed cases")
  covid19-cases-uk-englad-lad)


(apply max (map
             #(get % "Cumulative lab-confirmed cases")
             covid19-cases-uk-englad-lad))

;; Need to convert strings to numbers first

(map
  #(Integer/parseInt (get % "Cumulative lab-confirmed cases"))
  covid19-cases-uk-englad-lad)

(apply max (map
             #(Integer/parseInt (get % "Cumulative lab-confirmed cases"))
             covid19-cases-uk-englad-lad))


(defn maximum-cases
  "Calculates the maximum value of cases"
  [data-set]
  (apply max
         (map
           #(Integer/parseInt
              (get % "Cumulative lab-confirmed cases"))
           data-set)))

;; Update the view to calculate the maximum scale

(oz/view!
  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values england-lad-geojson-with-cases
              :format {:property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color
              {:field "Cases",
               :type  "quantitative"
               :scale {:domain [0 (maximum-cases covid19-cases-uk-englad-lad)]}}
              }})


;; Using a smaller scale highlights issues with the data.
;; It could be showing a difference between the geo-data
;; and gov.uk data sets in the names used for local area districts.
;; Or perhaps the values for the districts are not being used correctly.
;; Having the a scale that is too large can be misleading


;; Adding a tool tip to see values of each district
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; To make the tool tip simpler, add the name the district
;; at the top level of each feature, as was done with cases.

;; Add to the existing update code.
;; The relevant code was already create for cases
;; (:LAD13NM (:properties feature))

(defn update-cases-data
  [geo-json-data-set cases-data-set]

  (update
    geo-json-data-set
    :features
    (fn [features]  ;; as we are using update, features represents the whole geo-json data set
      (mapv
        (fn [feature]
          (assoc
            feature
            :Cases (get (filter #(= (:LAD13NM (:properties feature))
                                    (get % "Area name"))
                                cases-data-set)
                        "Daily lab-confirmed cases" (rand-int 12000))

            :Location (:LAD13NM (:properties feature))))

        features
        ))))

(def england-lad-geojson-with-cases
  (update-cases-data england-lad-geojson covid19-cases-uk-englad-lad))

;; Add a tool tip to the view using :Location data

(oz/view!
  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values england-lad-geojson-with-cases
              :format {:property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color
              {:field "Cases",
               :type  "quantitative"
               :scale {:domain [0  40000 #_(maximum-cases covid19-cases-uk-englad-lad)]}}
              :tooltip [{:field "Location" :type "nominal"}
                        {:field "Cases" :type "quantitative"}]
              }})


;; To resolve this issue, a reducing function could be used
;; to create a new data set with just the current maximum value
;; for each district.


;; Using aggregate with Vega-lite
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; First lets see if there is anything that we can adjust in the view
;; vega-lite has an aggregate
;; https://vega.github.io/vega-lite/docs/aggregate.html
;; e.g. "aggregate": "max",
;; It is assumed that this will sum up all the values for each district


(oz/view!
  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values england-lad-geojson-with-cases
              :format {:property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color
              {:field     "Cases",
               :aggregate "max",
               :type      "quantitative"
               :scale     {:domain [0 (maximum-cases covid19-cases-uk-englad-lad)]}}
              :tooltip [{:field "Location" :type "nominal"}
                        {:field "Cases" :type "quantitative"}]
              }})


;; Unfortunately the aggregate option breaks the display of the map
;; so it seems it does not do what I was hoping for

;; vega-lite has a transform

;; "transform": [
;;               {
;;                "aggregate" : [{
;;                                "op"    : "mean",
;;                                "field" : "Cases",
;;                                "as"    : "mean_acc"
;;                                }],
;;                }
;;               ]



;; BAD DATA alert
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Oh, I realized that my update function is not getting the real data
;; If I set the default to a lower random number, all the values are lower

(defn update-cases-data
  [geo-json-data-set cases-data-set]

  (update
    geo-json-data-set
    :features
    (fn [features]  ;; as we are using update, features represents the whole geo-json data set
      (mapv
        (fn [feature]
          (assoc
            feature
            :Cases (get (filter #(= (:LAD13NM (:properties feature))
                                    (get % "Area name"))
                                cases-data-set)
                        "Cumulative lab-confirmed cases" (rand-int 10))

            :Location (:LAD13NM (:properties feature)))

          )
        features
        ))))

(def england-lad-geojson-with-cases
  (update-cases-data england-lad-geojson covid19-cases-uk-englad-lad))

;; Add a tool tip to the view

(oz/view!
  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values england-lad-geojson-with-cases
              :format {:property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color
              {:field "Cases",
               :type  "quantitative"
               :scale {:domain [0 (maximum-cases covid19-cases-uk-englad-lad)]}}
              :tooltip [{:field "Location" :type "nominal"}
                        {:field "Cases" :type "quantitative"}]
              }})


;; Looking at the values of the tooltip over individual regions
;; the values were much higher than the highest value,
;; so obviously have multiple occurrences of each location
;; and it seems I am summing daily cumulative totals as cumulative values


;; Fixing the update function
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; returns a collection of daily recordings
(filter (fn [district]
          (= (get district "Area name") "Hartlepool") ) covid19-cases-uk-englad-lad)

;; really we just want the largest total value
;; look at the keys to see what is needed
(keys (first (filter (fn [district]
                       (= (get district "Area name") "Hartlepool") ) covid19-cases-uk-englad-lad)))
;; => ("Area name" "Area code" "Area type" "Specimen date" "Daily lab-confirmed cases" "Cumulative lab-confirmed cases")

(map #(get % "Cumulative lab-confirmed cases")
     (filter (fn [district]
               (= (get district "Area name") "Hartlepool") ) covid19-cases-uk-englad-lad))

;; then just get the maximum value
(apply max
       (map #(Integer/parseInt
               (get % "Cumulative lab-confirmed cases"))
            (filter (fn [district]
                      (= (get district "Area name") "Hartlepool") ) covid19-cases-uk-englad-lad)))


(defn update-cases-data
  [geo-json-data-set cases-data-set]

  (update
    geo-json-data-set
    :features
    (fn [features]  ;; as we are using update, features represents the whole geo-json data set
      (mapv
        (fn [feature]
          (assoc
            feature
            :Cases
            (apply max
                   (mapv #(Integer/parseInt
                            (get % "Cumulative lab-confirmed cases"))
                         (filter (fn [district]
                                   (= (get district "Area name")
                                      (:LAD13NM (:properties feature))))
                                 cases-data-set)))

            :Location (:LAD13NM (:properties feature)))

          )
        features
        ))))

(def england-lad-geojson-with-cases
  (update-cases-data england-lad-geojson covid19-cases-uk-englad-lad))


;; (val covid19-cases-uk-englad-lad )


;; Oz view with combined GeoJSON and Gov.uk data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; (oz/view!
;;   {:title    {:text "COVID19 cases in England Hospitals"}
;;    :height   1000
;;    :width    920
;;    :data     {:name   "England"
;;               :values england-lad-geojson-with-cases
;;               :format {:property "features"}},
;;    :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
;;    :encoding {:color {:field "Cases-per-100k",
;;                       :type  "quantitative"
;;                       :scale {:domain [0 120000
;;                                        ;; Calculate the max value from the :cases keyword in the data set
;;                                        #_(apply max (map :cases-per-100k (vals deutschland/bundeslaender-data)))]}}

;;               ;; is the bundesland added to the geo-json data because its otherwise burried in the properties data?
;;               :tooltip [{:field "Bundesland" :type "nominal"}
;;                         {:field "Cases" :type "quantitative"}]}
;;    :selection {:highlight {:on "mouseover" :type "single"}}}
;;   )




;; Transform Cases data first
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; Get the maximum value for each local area district


;; (group-by "Area name"  covid19-cases-uk-englad-lad)

(defn uk-data-view
  "Specific view of a given data set based on location"
  [data-set location]
  (filter #(some #{location} %) data-set))


;; data structure
;; "Area name" = "Barking and Dagenham"
;; "Area code" = "E09000002"
;; "Area type" = "Upper tier local authority"
;; "Specimen date" = "2020-04-14"
;; "Daily lab-confirmed cases" = "4"
;; "Cumulative lab-confirmed cases" = "347"

(def test-data
  [{"Area name" "Hartlepool" "Cumulative lab-confirmed cases" "347"}
   {"Area name" "Bromley" "Cumulative lab-confirmed cases" "47"}
   {"Area name" "Hartlepool" "Cumulative lab-confirmed cases" "7"}
   {"Area name" "Hartlepool" "Cumulative lab-confirmed cases" "17"}])

(map (fn [occurance]
       (filter #(some #{"Hartlepool"} %)) occurance)
     test-data)

(filter (fn [[_ v]]
          (= "Hartlepool" v))
        test-data)

(map #(filter (fn [[k v]]
                (= "Hartlepool" v)) %)
     test-data)
;; => ((["Area name" "Hartlepool"]) () (["Area name" "Hartlepool"]) (["Area name" "Hartlepool"]))


#_(filter #(some #{"Country"} %)
          covid19-cases-england-combined)


(mapv #(vals %) test-data)
;; => [("Hartlepool" "347") ("Bromley" "47") ("Hartlepool" "7") ("Hartlepool" "17")]


(mapv (fn [occurance]
        #(some #{"Hartlepool"} %) (vals occurance))
      test-data)
;; => [("Hartlepool" "347") ("Bromley" "47") ("Hartlepool" "7") ("Hartlepool" "17")]



#_(comp #(= "Hartlepool" %) val)

(fn [occurance]
  (filter (comp #(= "Hartlepool" %) val) occurance))


(into {}
      (map (fn [occurance]
             (filter (comp #(= "Hartlepool" %) val) occurance))
           test-data))


(uk-data-view covid19-cases-uk-englad-lad "Hartlepool")



;; Go right back to the starting data
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Its actually easier to go back to the starting data and add some filters there


(defn csv->clj-hash-map
  "Convert CSV file to sequence of hash maps.
  Each hash-map uses the heading text as a key
  for each element in the row of data.

  Return: a sequence of hash-maps"
  [data-source]
  (->> data-source
       io/resource
       slurp
       csv/read-csv
       semantic-csv/mappify))

(def covid19-cases-uk-combined
  (csv->clj-hash-map "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"))


(def covid19-cases-uk-local-authorities-hartlepool
  (filter #(some #{"Hartlepool"} %)
          covid19-cases-england-combined))


;; 0. "Hartlepool"
;; 1. "E06000001"
;; 2. "Upper tier local authority"
;; 3. "2020-04-14"
;; 4. "0"
;; 5. "75"

;; We could just get the map with the latest date
;; Dates are strings, so a simple comparison seems fine
;; no need to convert them into date objects

(= "2020-04-14" "2020-04-14")
;; => true

(= "2020-04-14" "2020-04-13")
;; => false


;; We can either get the latest date or the maximum cummulative total
;; using the latest date requires a lot less code than comparing each
;; cumulative total from every occurrence of every district


(def covid19-cases-uk-local-authorities-latest
  (filter #(some #{"2020-04-14"} %)
          covid19-cases-england-combined))




(defn extract-data-from-csv
  "Convert CSV file to sequence of vectors
  Each hash-map uses the heading text as a key
  for each element in the row of data.

  Return: a sequence of vectors"
  [data-source]
  (->> data-source
       io/resource
       slurp
       csv/read-csv))

(def extracted-data-gov-uk
  (extract-data-from-csv "data-sets/coronavirus-cases-UK-contry-region-local-authority-gov-uk.csv"))


(defn data-set-specific-date
  "Transform to map for visualization,
  including only the specific date.

  Use csv headings as keys in each map.

  Return: a sequence of maps"
  [extracted-data-set date]

  (let [heading (first extracted-data-set)]

    (conj
      (filter #(some #{date} %) extracted-data-set)
      heading)))


;; conj will join the headings which is a vector
;; to the specific days occurrences which is a sequence of vectors
(conj '([1 2 3] [4 5 6]) ["a" "b" "c"] )
;; => (["a" "b" "c"] [1 2 3] [4 5 6])


;; Test the combination works
;; (data-set-specific-date extracted-data-gov-uk "2020-04-14")




;; Now turn it into a map

(defn data-set-specific-date
  "Transform to map for visualization,
  including only the specific date.

  Use csv headings as keys in each map.

  Return: a sequence of maps"
  [extracted-data-set date]

  (let [heading (first extracted-data-set)]

    (semantic-csv/mappify
      {:keyify false}
      (conj
        (filter #(some #{date} %) extracted-data-set)
        heading))))


(def gov-uk-date-specific
  (data-set-specific-date extracted-data-gov-uk "2020-04-14"))


;; TODO: extract the local area district occurrences before turning into a map.


(defn update-cases-data-simplified
  [geo-json-data-set cases-data-set]

  (update
    geo-json-data-set
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

        features
        ))))

(def england-lad-geojson-with-cases-date-specific
  (update-cases-data-simplified england-lad-geojson gov-uk-date-specific))


(filter
  #(some #{"England"} (vals %))
  (take 5 gov-uk-date-specific))
;; => ({"Area name" "England", "Area code" "E92000001", "Area type" "Country", "Specimen date" "2020-04-14", "Daily lab-confirmed cases" "134", "Cumulative lab-confirmed cases" "76371"})


(first
  (filter
    #(some #{"England"} (vals %))
    (take 5 gov-uk-date-specific)))
;; => {"Area name" "England", "Area code" "E92000001", "Area type" "Country", "Specimen date" "2020-04-14", "Daily lab-confirmed cases" "134", "Cumulative lab-confirmed cases" "76371"}

(get
  (first
    (filter
      #(some #{"England"} (vals %))
      (take 5 gov-uk-date-specific)))
  "Cumulative lab-confirmed cases" -1)
;; => "76371"





;; Oz view with combined GeoJSON and Gov.uk data latest
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



(oz/view!
  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values england-lad-geojson-with-cases-date-specific
              :format {:property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color
              {:field "Cases",
               :type  "quantitative"
               :scale {:domain [0 2000
                                #_(maximum-cases gov-uk-date-specific)]}}
              :tooltip [{:field "Location" :type "nominal"}
                        {:field "Cases" :type "quantitative"}]
              }})



;; TODO: filter out non district settings to make maximum-cases work correctly.
;; Its currently including data from England

(def covid19-cases-uk-local-authorities
  (filter #(some #{"Upper tier local authority"} %)
          extracted-data-gov-uk))

;;  This removes the heading though, which is still needed to convert
;; the data set into a map for Oz.

(def covid19-cases-uk-local-authorities
  (remove #(some #{"Country" "Region"} %)
          extracted-data-gov-uk))


(def gov-uk-date-specific-lad
  (data-set-specific-date covid19-cases-uk-local-authorities "2020-04-14"))

(def england-lad-geojson-with-cases-date-specific-lad
  (update-cases-data-simplified england-lad-geojson gov-uk-date-specific-lad))


;; TODO: Create a single def with references to all the data transformations
;; - Put this in its own namespace ??

(oz/view!
  {:title    {:text "COVID19 cases in England Hospitals"}
   :height   1000
   :width    920
   :data     {:name   "England"
              :values england-lad-geojson-with-cases-date-specific-lad
              :format {:property "features"}},
   :mark     {:type "geoshape" :stroke "white" :strokeWidth 0.5}
   :encoding {:color
              {:field "Cases",
               :type  "quantitative"
               :scale {:domain [0 (maximum-cases gov-uk-date-specific-lad)]}}
              :tooltip [{:field "Location" :type "nominal"}
                        {:field "Cases" :type "quantitative"}]
              }})







;; References
;; Local Authority District (December 2013) http://hub.arcgis.com/datasets/d266cbe2179a4766b4de7c6e73b4a285_0/data


;; The clojure data structure is the map argument to the update function
;; :features is the key to be updated in the clojure hash-map
;; the update is managed with an anonymous function that takes the original features data structure
;; and changes the values of specific keys using an assoc function
;; update clojure-hash-map :feature new-data




;; Comparing GeoJSON features and Gov.uk districts
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Create a set of the features from the GeoJSON containing
;; :LAD13NM - name of the district

(require 'practicalli.data-geo-json)

;; Get the district names from the GeoJSON file
(map #(get-in % [:properties :LAD13NM] )
     (:features practicalli.data-geo-json/england-lad-geojson-with-cases-date-specific-lad))

;; Total number of districts
(count
  (map #(get-in % [:properties :LAD13NM] )
       (:features practicalli.data-geo-json/england-lad-geojson-with-cases-date-specific-lad)))
;; => 326

;; Are they all unique?
(into #{}
      (map #(get-in % [:properties :LAD13NM] )
           (:features practicalli.data-geo-json/england-lad-geojson-with-cases-date-specific-lad)))

;; Yes, all unique
(count
  (into #{}
        (map #(get-in % [:properties :LAD13NM] )
             (:features practicalli.data-geo-json/england-lad-geojson-with-cases-date-specific-lad))))
;; => 326

(def geojson-data-names
  (into #{}
        (map #(get-in % [:properties :LAD13NM] )
             (:features practicalli.data-geo-json/england-lad-geojson-with-cases-date-specific-lad))))


;; Get district names for the latest day of the cases data
(require 'practicalli.data-gov-uk)

(map
  #(get % "Area name")
  practicalli.data-gov-uk/covid19-cases-uk-local-authority-district-date-specific)

(count
  (map
    #(get % "Area name")
    practicalli.data-gov-uk/covid19-cases-uk-local-authority-district-date-specific))
;; => 150

(def data-gov-uk-england-names
  (into #{}
        (map
          #(get % "Area name")
          practicalli.data-gov-uk/covid19-cases-uk-local-authority-district-date-specific)))



;; Compare the names in the two data sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Find the names that appear in both data sets

;; clojure.set contains functions for working with Clojure set data types
(require 'clojure.set)


;; Take the intersection of both data sets (names in both sets)
;; and count the total
(count
  (clojure.set/intersection
    data-gov-uk-england-names
    geojson-data-names))
;; => 121

;; 29 cases not found in the GeoJSON
(count
  (clojure.set/difference
    data-gov-uk-england-names
    geojson-data-names))
;; => 29


;; 205 GeoJSON districts without cases data
(count
  (clojure.set/difference
    geojson-data-names
    data-gov-uk-england-names))
;; => 205





;; Search for a more appropriate GeoJSON file
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Open Geography Portal
;; https://geoportal.statistics.gov.uk/datasets/6638c31a8e9842f98a037748f72258ed_0
;; This file contains the digital vector boundaries for Counties and Unitary Authorities (also known as Upper Tier Local Authorities) in the UK, as at 31 December 2017. The boundaries available are:

;; Full resolution - extent of the realm (usually this is the Mean Low Water mark but in some cases boundaries extend beyond this to include off shore islands);

;; Download GeoJSON file
;; https://opendata.arcgis.com/datasets/6638c31a8e9842f98a037748f72258ed_0.geojson


;; Only shows a solid blue box rather than a map
;; irrespective of if it is a geojson or topojson
(oz/view! {:data {:url    "/public/geo-data/Counties_and_Unitary_Authorities_December_2017_Boundaries_UK.geojson"
                  :format {:type     "json"
                           :property "features"}}
           :mark "geoshape"})

(oz/view!
  {:data {:url "https://opendata.arcgis.com/datasets/6638c31a8e9842f98a037748f72258ed_0.geojson"

          :format {:type    "topojson"
                   :feature "lad"}}
   :mark "geoshape"})




(require '[practicalli.data-geo-json :as data-geo-json])

;; Convert to Clojure
(def open-geograhy-portal-counties-uk
  (data-geo-json/geojson->clj "public/geo-data/Counties_and_Unitary_Authorities_December_2017_Boundaries_UK.geojson"))


;; Get the names of each district
(map #(get-in % [:properties :ctyua17nm] )
     (:features open-geograhy-portal-counties-uk))


;; Count the number of district
(count
  (map #(get-in % [:properties :ctyua17nm] )
       (:features open-geograhy-portal-counties-uk)))
;; => 217

;; Remove duplicate names by placing values into a Clojure set data type
;; If count is still the same as above, no duplicates
(count
  (into #{}
        (map #(get-in % [:properties :ctyua17nm] )
             (:features open-geograhy-portal-counties-uk))   ))
;; => 217

;; Bind the set with a name to use when comparing with Gov.uk data
(def open-geograhy-portal-counties-uk-names
  (into #{}
        (map #(get-in % [:properties :ctyua17nm] )
             (:features open-geograhy-portal-counties-uk))   ))


;; Compare the names in the two data sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Find the names that appear in both data sets

;; clojure.set contains functions for working with Clojure set data types
(require 'clojure.set)


;; Take the intersection of both data sets (names in both sets)
;; and count the total
(count
  (clojure.set/intersection
    data-gov-uk-england-names
    open-geograhy-portal-counties-uk-names))
;; => 148

;; Ah, so only 2 items different, that looks promising
;; unfortunately the GeoJSON file does not work with vega,
;; it only displays a blue square.



;; GeoJSON - Great Britain Local Authorities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; https://martinjc.github.io/UK-GeoJSON/json/eng/topo_lad.json


(oz/view!
  {:height 400
   :width  360
   :data   {:url "public/geo-data/topo_lad.json"

            :format {:type    "topojson"
                     :feature "lad"}}
   :mark "geoshape"})




;; United Kingdom Local Area Districts (Administrative)
;; - martinjc GitHub repository
;;

(oz/view!
  {:height 400
   :width  360
   :data   {:url "https://raw.githubusercontent.com/martinjc/UK-GeoJSON/master/json/administrative/gb/lad.json"

            :format {:type     "json"
                     :property "features"}}
   :mark "geoshape"})

;; Find the number of districts

(def uk-local-area-districts-administrative-martinjc
  (data-geo-json/geojson->clj "public/geo-data/uk-local-area-districts-administrative-martinjc-lad.json"))


(count
  (map #(get-in % [:properties :LAD13NM] )
       (:features uk-local-area-districts-administrative-martinjc)))
;; => 380



;; GeoJSON of Counties and Unitary Authorities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; - Ultra Generalised Clipped Boundaries in UK (WG84)
;; England, Scotland, Wales and Northern Ireland
;; https://opendata.arcgis.com/datasets/658297aefddf49e49dcd5fbfc647769e_4.geojson
;;
;; Office for National Statistics
;; https://geoportal.statistics.gov.uk/datasets/counties-and-unitary-authorities-december-2017-ultra-generalised-clipped-boundaries-in-uk-wgs84






;; Refactor -main and dashboard-corvid19-uk
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Although the various def expressions were useful for building up the code,
;; multiple expressions need to be re-evaluated when changing data.
;; It also requires navigating through several namespaces

;; Modify the -main function in the dashboard namespaces
;; to call the relevant functions to extract and transform the data sets

;; Some of the def expressions should be turned into functions to support this.





;; The transformation is required for the cases data and the geojson data set.
;; As there are several steps, this could be written using a let statement
;; With a function call to add the data to the view.

(let [cases-data
      (data-gov-uk/coronavirus-cases-data {:csv-file  "data-sets/uk-coronavirus-cases.csv"
                                           :locations #{"Nation" "Country" "Region"}
                                           :date      "2020-04-29"})

      geojson-cases-data
      (data-geo-json/geojson-cases-data "public/geo-data/uk-local-area-districts-administrative-martinjc-lad.json"
                                        cases-data)]

  (oz/view!
    (dashboard-corvid19-uk (views/geo-json-view geojson-cases-data 1000))))


;; Or it could be written as one expression

(dashboard-corvid19-uk
  (views/geo-json-view
    (data-geo-json/geojson-cases-data
      "public/geo-data/uk-local-area-districts-administrative-martinjc-lad.json"
      (data-gov-uk/coronavirus-cases-data
        {:csv-file  "data-sets/uk-coronavirus-cases.csv"
         :locations #{"Nation" "Country" "Region"}
         :date      "2020-04-29"}))
    1000))

;; Or using the threading macro

(-> "public/geo-data/uk-local-area-districts-administrative-martinjc-lad.json"
    (data-geo-json/geojson-cases-data
      (data-gov-uk/coronavirus-cases-data
        {:csv-file  "data-sets/uk-coronavirus-cases.csv"
         :locations #{"Nation" "Country" "Region"}
         :date      "2020-04-29"}))
    (views/geo-json-view
      1000)
    dashboard-corvid19-uk)


;; it still looks a little dense, but with main argument variables it can be clearer

(defn -main
  [cases-data-file]
  (-> cases-data-file
      (data-geo-json/geojson-cases-data
        (data-gov-uk/coronavirus-cases-data
          {:csv-file  "data-sets/uk-coronavirus-cases.csv"
           :locations #{"Nation" "Country" "Region"}
           :date      "2020-04-29"}))
      (views/geo-json-view
        1000)
      dashboard-corvid19-uk))


;; Thread last is still quite nested
(->> 1000
     (views/geo-json-view
       (data-geo-json/geojson-cases-data
         "public/geo-data/uk-local-area-districts-administrative-martinjc-lad.json"
         (data-gov-uk/coronavirus-cases-data
           {:csv-file  "data-sets/uk-coronavirus-cases.csv"
            :locations #{"Nation" "Country" "Region"}
            :date      "2020-04-29"})))
     dashboard-corvid19-uk
     )

;; Using the let approach seems to be the most readable approach in this case.



;; Update the dashboard view
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Update the view data from a def to a defn, which takes the combined geojson and cases data


(defn dashboard-corvid19-uk
  [geojson-view]
  [:div

   ;; refactor this part to simplify the view

   ;; Oz visualization
   [:section {:class "section"}
    [:vega-lite views/line-plot-uk-countries-cumulative-cases]
    [:vega-lite views/stacked-bar-uk-countries-cumulative-cases]

    ;; The argument is a vega-lite data structure, so can be visualized
    [:vega-lite geojson-view]]])



;; Refactor the dashboard into functions

;; Web page meta data
(def include-bulma-css
  [:link {:rel  "stylesheet"
          :href "https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css"}])

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
    (view-helpers/headline-card-total
      data-gov-uk-depricated/covid19-uk-data-latest-fixed
      {:name "Cumulative UK Totals " :type "TotalUK"})

    (view-helpers/headline-card-total
      data-gov-uk-depricated/covid19-uk-data-latest-fixed
      {:name "Daily Totals" :type "DailyUK"})]

   ;; Country Cases
   [:div {:class "columns"}
    (map #(view-helpers/headline-card-country
            data-gov-uk-depricated/covid19-uk-data-latest %)
         [{:name "England" :alias "England"}
          {:name "Scotland" :alias "Scotland"}
          {:name "Wales" :alias "Wales"}
          {:name "Northern Ireland" :alias "NI"}])]])


(defn dashboard-corvid19-uk
  [geojson-view]
  [:div
   ;; Web page structure and content
   include-bulma-css

   ;; Heading
   webpage-heading

   ;; Daily Headline figures
   (headline-figures)

   ;; Oz visualization
   [:section {:class "section"}
    [:vega-lite views/line-plot-uk-countries-cumulative-cases]
    [:vega-lite views/stacked-bar-uk-countries-cumulative-cases]
    [:vega-lite geojson-view]]])
