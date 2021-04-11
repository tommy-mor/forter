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
   {:style {:margin-top (str height "px")}}
   [:h1 {:style {:margin-bottom "4px"}}
    (:name item)]
   [:span {:style {:color "red"}} (:url item)]])

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
                                       (dissoc invalidates))))))}])

(defn calc-heights [perc]
  {:right (/ (min 0 (- 50 perc)) 2) 
   :left (/ (min 0 (- perc 50)) 2)})


(defn home-page []
  (let [{ :keys [left right] } (calc-heights (:percent @score))]
    [:div

     [:div.container [:h2 "warstarst"]
      [itemview {:name "A" :url "google.com"} left]
      [itemview {:name "B" :url "bing.com"} right]
      [slider :percent (:percent @score) 0 100 nil ]]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
