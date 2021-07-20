(ns frontsorter.common
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]))

(defn collapsible-cage [open title & children]
  (let [collapsed (r/atom (not open))]
    (fn [open title children]
      [:div.cageparent
       [:div.cagetitle
        {:on-click (fn [e] (swap! collapsed not))}
        (if @collapsed
          (str title " >>")
          (str title " <<"))]
       (if @collapsed
         nil
         children)])))

(defn spotify-player [id]
  [:iframe {:src (str "https://open.spotify.com/embed/track/" id)
    :width 300 :height 80
    :allowtransparency "true" :allow "encrypted-media"}])

(defn itemview [item height right]
  (let [url (:url (:content item))
        spotify-id (-> item :content :spotify_id)]
    [:div
     {:class (if right "rightitem" "leftitem")
      :style {:margin-top (str height "px")}}
     
     [:h1 {:style {:margin-bottom "4px"}}
      (if spotify-id
        (spotify-player spotify-id)
        (:name item))]
     [:span {:style {:color "red"}} url]]))

(defn item [item size]
  (let [hovered (r/atom false)
        url (str js/tag "/" (:id item) )
        row (fn [kw item]
              (if (kw item)
                
                [:td {:style {:background-color
                              (str "hsl(" (* 100 (kw item)) ", 100%, 50%)")}
                      }(.toFixed (kw item) 2)])
              
              )]
    (fn [item size] 
      [:tr
       {
        :on-mouse-over (fn [] (reset! hovered true))
        :on-mouse-out (fn [] (reset! hovered false))
        :on-click (fn [] (set! js/window.location.href url))
        :key (:id item)
        :class (if @hovered "item hovered" "item")
        }
       
       
       [:td (:name item)]
       ;; customize by type (display url for links?)
       
       (if (:elo item)
         
         [:td (.toFixed (* 10 size (:elo item)) 2)])
       
       (row :matchup item)
       (row :smoothmatchup item)
       
       
       ;; do we show the number of votes involving this tag?
       ])))

(defn ranklist [rank]
  ;; (js/console.log "rank")
  ;; (js/console.log (clj->js  @rank))
  
  (let [size (count @rank)]
    [:table
     [:thead
      [:tr [:th ""] [:th ""] [:th ""]]]
     [:tbody
      (for [n @rank]
        [item n size])]]))
