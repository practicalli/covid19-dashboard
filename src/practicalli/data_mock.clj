;; Data - mock
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(ns practicalli.data-mock)


;; Data Generation

(defn covid19-data-set
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


;; Examples of mock data sets
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment
  (covid19-data-set "England" "Scotland" "Wales" "Northern Ireland")
  )

(first (covid19-data-set "England" "Scotland" "Wales" "Northern Ireland"))

;; => {:day 0, :location "England", :cases 6.0}
