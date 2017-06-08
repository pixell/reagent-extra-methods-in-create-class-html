(ns reagent-extra-methods-in-create-class-html.prod
  (:require [reagent-extra-methods-in-create-class-html.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
