(ns practicalli.design-journal
  (:require [oz.core :as oz]))


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

