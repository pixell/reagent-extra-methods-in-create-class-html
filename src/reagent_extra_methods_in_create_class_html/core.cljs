(ns reagent-extra-methods-in-create-class-html.core
    (:require [reagent.core :as reagent]))

;; -------------------------
;; Views

(defn view [_]
  (let [c (reagent/create-class
            {:display-name "View"

             :component-will-appear
             (fn []
               (.log js/console "Component will appear"))

             :reagent-render
             (fn [children]
              [:div children])})]
    (.log js/console "Callback exists" (.. c -prototype -componentWillAppear))
    c))

(defn- get-child-map [children]
  (apply merge {} (map (fn [c] (when-let [k (:key (meta c))] {k c})) children)))

(defn transition-group [_ _]
  (let [state (atom {:children {} :refs {}})]
    (reagent/create-class
      {:display-name "TransitionGroup"

       :component-will-mount
       (fn [this]
         (swap! state merge {:children (get-child-map (reagent/children this))}))

       :component-will-receive-props
       (fn [this new-argv]
         (let [next-child (get-child-map (reagent.impl.component/extract-children new-argv))
               prev-child (@state :children)]
           (swap! state merge {:children (merge prev-child next-child)})))

       :component-did-update
       (fn [this old-argv]
         (when-let [[k c] (first (take 2 (@state :children)))]
           (when-let [r (get-in @state [:refs k])]
             (if (.. r -componentWillAppear)
               (.log js/console "Callback found for" k)
               (.log js/console "No callback found for" k r)))
           (swap! state update-in [:children] dissoc k)))

       :reagent-render
       (fn [props _]
         (let [children-to-render (map (fn [[k c]]
                                         (when c
                                           (.cloneElement js/React (reagent/as-element c) (clj->js {:ref #(swap! state update-in [:refs] assoc k %) k nil}))))
                                       (@state :children))]
           [view children-to-render]))
       })))

(defn home-page []
  (let [state (reagent/atom {:view :first})]
    (fn []
      [:div
        [:div
          [:h2 "View:" (name (@state :view))]
          [transition-group
            (case (@state :view)
              :first  ^{:key "t1"} [view [:p "Test 1"]]
              :second ^{:key "t2"} [view [:p "Test 2"]])
            ]]

        [:input {:type  "button"
                 :value "Change view"
                 :on-click #(swap! state update-in [:view] (fn [v] (first (remove #{v} #{:first :second}))))}]
      ])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
