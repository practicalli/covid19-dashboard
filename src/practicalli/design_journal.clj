(ns practicalli.design-journal
  (:require [oz.core :as oz]))


;; Oz server listening on a websocket for views to display.
;; Enables fast feedback for visualization
(oz/start-server!)

