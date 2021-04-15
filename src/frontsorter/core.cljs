(ns frontsorter.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require
     [cljs-http.client :as http]
     [cljs.core.async :refer [<!]]
     [reagent.core :as r]
     [reagent.dom :as d]))

(defn apistr [c t] (str "/priv/api/vote/" js/col "/" js/tagid))
(defn sendstr [tag col left right mag]
  (apply str (interpose "/" ["/priv/api/vote/send" tag col left right mag])))

(defn delstr [tag]
  (apply str (interpose "/" ["/priv/api/tag/delvotes" tag])))

;; ------------------------ 
;; State

(def score (r/atom {:percent 50
                    :left nil :right nil}))
(def rank (r/atom []))

(defn handleresponse [response]
  (js/console.log (-> response :body :sorted))
  (swap! score assoc :left (-> response :body :left))
  (swap! score assoc :right (-> response :body :right))
  (swap! score assoc :percent 50)
  (reset! rank (-> response :body :sorted)))

(defn initdata []
  (js/console.log (js->clj js/sorted))
  (reset! rank (js->clj js/sorted :keywordize-keys true))
  (swap! score assoc :left {:name js/left.name :content {:url  js/left.content.url} :id js/left.id})
  (swap! score assoc :right {:name js/right.name :content {:url js/right.content.url} :id js/right.id}))


(defn sendvote []
  (go
    (let [url (sendstr js/tag js/col
                       (-> @score :left :id)
                       (-> @score :right :id)
                       (:percent @score))
          response (<! (http/post url))]
      (handleresponse response))))

(defn delvotes []
  (go
    (let [url (delstr js/tag)
          response (<! (http/post url))]
      (handleresponse response))))

;; -------------------------
;; Views

(defn button [text fn]
  [:div.button {:on-click fn} text])

(defn itemview [item height]
  [:div.child
   {:style {:margin-top (str height "px")}}
   [:h1 {:style {:margin-bottom "4px"}}
    (:name item)]
   [:span {:style {:color "red"}} (:url (:content item))]])

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
  (js/console.log "rank")
  (js/console.log (clj->js  @rank))
  [:table
   [:thead
    [:tr [:th "name"] [:th "url"]]]
   [:tbody
    (map (fn [i]
           (let [i (get i 1)]
             (js/console.log i)
             
             [:tr
              {:key (:id i)}
              [:td (:name i)]
              [:td (:url (:content i))]])) @rank )]])





(defn home-page []
  (initdata)
  
  (fn []
    (let [{ :keys [left right] } (calc-heights (:percent @score))]
      [:div
       [:h2 " sorter "]
       [:code "category: web browsers"]
       

       [:div.container

        [itemview (:left @score) left]
        [itemview (:right @score) right]
        [slider :percent (:percent @score) 0 100 nil ]
        [button "submit" sendvote]
        [:h3 "current ranking"]
        [ranklist rank]
        
        [button "delete" delvotes]]])))


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
                                        ; TODO add kanban
                                        ; TODO set up hosted version, maybe way to deploy through gh actions
                                        ; make slider easier to press (wider surface area to click)
                                        ; make votes editable (many mini sliders, in collapsible panel (all panels are collapsible))
                                        ; in 'current ranking', select items to 'pin' on left or right
