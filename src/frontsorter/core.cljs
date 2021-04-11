(ns frontsorter.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]))


;; ------------------------ 
;; State

(def score (r/atom {:percent 50}))

;; -------------------------
;; Views

(defn itemview [item height]
  [:div.child
   {:style {:margin-top (str (- height) "px")}}
   [:h1 {:style {:margin-bottom "4px"}}
    (:name item)]
   [:span {:style {:color "red"}} (:url item)]])

(defn home-page []
  [:div

   [:div.container [:h2 "warstarst"]
    [itemview {:name "A" :url "google.com"} 0]
    [itemview {:name "B" :url "bing.com"} 0]
    [slider :percent 50 0 100 nil ]]])


;; copied from reagent-project.github.io
(defn slider [param value min max invalidates]
  [:input {:type "range" :value value :min min :max max
           :style {:width "100%"}
           :on-change (fn [e]
                        (let [new-value (js/parseInt (.. e -target -value))]
                          (swap! score
                                 (fn [data]
                                   (-> data
                                       (assoc param new-value)
                                       (dissoc invalidates)
                                       score)))))}])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
