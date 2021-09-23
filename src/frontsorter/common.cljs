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

(defn smallbutton [text fn]
  [:a {:on-click fn :class "sideeffect" :href "#"} text])

(defn hoveritem [itemkw keys & children]
  (let [hovered (r/atom false)]
    (fn [itemkw keys & children]
      (into 
       [itemkw
        (merge keys 
               {
                :on-mouse-over (fn [] (reset! hovered true))
                :on-mouse-out (fn [] (reset! hovered false))
                ;; :key TODO
                :class (if @hovered "item hovered" "item")
                })] children))))


;; slider stuff
(defn slider [param value min max score]
  [:input.slider {:type "range" :value value :min min :max max
                  :on-change (fn [e]
                               (let [new-value (js/parseInt (.. e -target -value))]
                                 (swap! score
                                        (fn [data]
                                          (assoc data param new-value)))))}])


(defn button [text fn]
  [:div.button {:on-click fn} text])


(defn calc-heights [perc]
  {:right (/ (min 0 (- 50 perc)) 2) 
   :left (/ (min 0 (- perc 50)) 2)})


(defn pairvoter [score startopen sendvote]
  (let [{:keys [left right]} (calc-heights (:percent @score))]
    
    [collapsible-cage startopen "VOTE"
     [:div.votearena
      [itemview (:left @score) left false]
      [itemview (:right @score) right true]
      [slider :percent (:percent @score) 0 100 score]
      [button "submit" sendvote]]]))

