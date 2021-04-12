(ns frontsorter.core
    (:require
      [reagent.core :as r]
      [reagent.dom :as d]))


;; ------------------------ 
;; State

(def score (r/atom {:percent 50}))
(def rank (r/atom [{:name "A" :url "google.com"}
                   {:name "B" :url "bing.com"}
                   {:name "C" :url "duckduckgo.com"}]))

;; -------------------------
;; Views

(defn button [text]
  [:div.button
   text ])

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

(defn ranklist [rank]
  [:table
   [:tr [:th "name"] [:th "url"]]
   (map (fn [i]
          [:tr
           [:td (:name i)]
           [:td (:url i)]]) @rank )])



(defn home-page []
  (let [{ :keys [left right] } (calc-heights (:percent @score))]
    [:div
     [:h2 "sorter"]
     [:code "category: web browsers"]
     

     [:div.container

      [itemview {:name "A" :url "google.com"} left]
      [itemview {:name "B" :url "bing.com"} right]
      [slider :percent (:percent @score) 0 100 nil ]
      [button "submit" ]
      [:h3 "current ranking"]
      
      [ranklist rank]]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))

                                        ; TODO
                                        ; then connect with backend (make json api)
                                        ; display the votes
					; links to rest of site real
					; make bottom panels collapsible?
