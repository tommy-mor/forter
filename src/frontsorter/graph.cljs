(ns frontsorter.graph
  (:require
   ["chart.js"]
   [reagent.core :refer [atom create-class]]
   [reagent.dom :as d]
   [frontsorter.common :as c]
   [ajax.core :as ajax :refer [GET]]))

(def height 850)
(def width 850)

(defn show-revenue-chart
  [data]
  (let [context (.getContext (.getElementById js/document "rev-chartjs") "2d")
        chart-data {:type "bar"
                    :data {:labels ["2012" "2013" "2014" "2015" "2016"]
                           :datasets [{:data [5 10 15 20 25]
                                       :label "Rev in MM"
                                       :backgroundColor "#90EE90"}
                                      {:data [3 6 9 12 15]
                                       :label "Cost in MM"
                                       :backgroundColor "#F08080"}]}}]
    (js/Chart. context (clj->js chart-data))))

(defn graph-data
  [data]
  (create-class
   {:component-did-mount #(show-revenue-chart data)
    :display-name        "chartjs-component"
    :reagent-render      (fn []
                           [:canvas {:id "rev-chartjs" :width "700" :height "380"}])}))

(defn attribute-selector [selected-atom attributes]
  [:select
   {:on-change #(let [new-attr (.. % -target -value)]
                  (reset! selected-atom new-attr))
    :value @selected-atom}

   [:option {:disabled true :value false} "select an option"]
   (for [[attribute number] attributes]
     [:option {:value attribute
               :key attribute}
      (str (name attribute) " (" number " votes)")])])

(defn render-graph [x-attr, y-attr]
  (let [data (atom nil)]
    (fn [x-attr y-attr]
      (GET (str "/t/" js/tagid "/graph/" x-attr "/" y-attr)
           {:handler #(js/console.log %)})
      [:h1 "render"])))


(defn graph []
  (let [x-attr (atom false)
        y-attr (atom false)
        attrs (c/attributes-not-db (js->clj js/attributes :keywordize-keys true))]
    (fn []
      [:div
       "x attribute"
       [attribute-selector x-attr attrs]
       "y attribute"
       [attribute-selector y-attr attrs]

       (when (and @x-attr
                  @y-attr
                  (not (= @x-attr @y-attr)))
         [render-graph @x-attr, @y-attr])])))

(defn mount-root []
  (d/render [graph] (.getElementById js/document "app")))


(defn ^:export init! []
  (mount-root))
