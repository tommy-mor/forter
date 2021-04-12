(ns frontsorter.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require
     [cljs-http.client :as http]
     [cljs.core.async :refer [<!]]
     [reagent.core :as r]
     [reagent.dom :as d]))


;; ------------------------ 
;; State

(def score (r/atom {:percent 50 :left {:name "A" :url "google.com"} :right {:name "B" :url "google.com"}}))
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
   [:thead
    [:tr [:th "name"] [:th "url"]]]
   [:tbody
    (map (fn [i]
           [:tr
            {:key (:url i)}
            [:td (:name i)]
            [:td (:url i)]]) @rank )]])



(def apistr "localhost:8080/priv/api/vote/0/58830eb5-4d8e-4f4c-8294-91e7a0c02c68")
(def apistr0 "google.com")
(defn home-page []
  (go
    (let [response (<! (http/get apistr))]   
      (js/console.log "epic")
      (js/console.log response)))
  
  (fn []
    (let [{ :keys [left right] } (calc-heights (:percent @score))]
      [:div
       [:h2 "sorter"]
       [:code "category: web browsers"]
       

       [:div.container

        [itemview (:left @score) left]
        [itemview (:right @score) right]
        [slider :percent (:percent @score) 0 100 nil ]
        [button "submit" ]
        [:h3 "current ranking"]
        
        [ranklist rank]]])))

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
                    ; make button only go pink (clickable) once you've changed the ranking at all

                                        ; make it load the things straight from the html or the dom, to avoid road trip.
; right now, just road trip.
