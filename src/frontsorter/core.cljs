(ns frontsorter.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require
     [cljs-http.client :as http]
     [cljs.core.async :refer [<!]]
     [reagent.core :as r]
     [reagent.dom :as d]))

(defn tagpage [tagid] (str "/priv/tag/disp/" tagid))

(defn sendstr [left right mag]
  (apply str (interpose "/" ["/priv/api/vote/send" js/tag left right mag])))

(defn delstr []
  (if (js/confirm "delete all votes?")
    (apply str (interpose "/" ["/priv/api/tag/delvotes" js/tag]))))

(defn delvotestr [vid]
  (apply str (interpose "/" ["/priv/api/vote/del" js/tag vid])))

;; ------------------------ 
;; State

(def score (r/atom {:percent 50
                    :left nil :right nil
                    :name ""}))
(def rank (r/atom []))

(def badlist (r/atom []))

(def votes (r/atom []))

(def options (r/atom []))

(defn handleresponse [response]
  (js/console.log (-> response clj->js))
  (swap! score assoc :tag (-> response :body :tag))
  (swap! score assoc :left (-> response :body :left))
  (swap! score assoc :right (-> response :body :right))
  (swap! score assoc :percent 50)
  (reset! rank (-> response :body :sorted))
  (reset! badlist (-> response :body :baditems))
  (reset! votes (-> response :body :votes)))

(defn initdata []
  (handleresponse {:body (js->clj js/init :keywordize-keys true)}))


(defn sendvote []
  (go
    (let [url (sendstr (-> @score :left :id)
                       (-> @score :right :id)
                       (:percent @score))
          response (<! (http/post url))]
      (handleresponse response))))

(defn move []
  (go
    (let [url "" response (<! (http/post url))]
      (handleresponse response))))

(defn delvotes []
  (go
    (let [url (delstr)
          response (<! (http/post url))]
      (handleresponse response))))

(defn delvote [vid]
  (go
    (let [url (delvotestr vid)
          response (<! (http/post url))]
      (handleresponse response))))

;; -------------------------
;; Views
(defn collapsible-cage [open title & children]
  (let [collapsed (r/atom (not open))]
    (fn [open title & children]
      [:div.cageparent
       [:div.cagetitle
        {:on-click (fn [e] (swap! collapsed not))}
        (if @collapsed
          (str title ">>")
          (str title "<<"))]
       (if @collapsed
         nil
         children)])))

(defn info []
  
  (let [tag (:tag @score)]
    [:div.cageparent [:div.cagetitle "TAG"]
     [:div {:style {:padding-left "10px"}} 
      [:h1 (:title tag)]
      [:i (:description tag)]
      [:br]
      "created by user " ;;TODO
      [:br]
      [:b (+ (count @rank) (count @badlist))] " items "
      [:b (+ (count @votes))] " votes"]
     ;; TODO get real user here
     ]))

(defn button [text fn]
  [:div.button {:on-click fn} text])

(defn smallbutton [text fn]
  [:a {:on-click fn :class "sideeffect" :href "#"} text])

(defn spotify-player [id]
  [:iframe {:src (str "https://open.spotify.com/embed/track/" id)
    :width 300 :height 80
    :allowtransparency "true" :allow "encrypted-media"}])

;; copied from reagent-project.github.io
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
(defn slider [param value min max invalidates]
  [:input.slider {:type "range" :value value :min min :max max
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
  ;; (js/console.log "rank")
  ;; (js/console.log (clj->js  @rank))
  
  (let [size (count @rank)]
    [:table
     [:thead
      [:tr [:th ""] [:th ""] [:th ""]]]
     [:tbody
      (for [n @rank]
        [:tr
         {:key (:id n)}
         [:td (:name n)]
         [:td (:url (:content n))]
         (if (:elo n)
           
           [:td (.toFixed (* 10 size (:elo n)) 2)])])]]))
(defn idtoname [itemid]
  ;; (js/console.log "itemid")
  ;; (js/console.log itemid)
  (let [a (filter (fn [i]
                    (let [i (get i 1)]
                      
                      (= (:id i) itemid))) @rank)]
    (:name (get (first a) 1))))



(defn votelist [votes]
  
   ;;(js/console.log "votes")
   ;;(js/console.log (clj->js  @votes))
  [:table
   [:thead
    [:tr [:th "left"] [:th "pts"] [:th "right"] [:th "pts"]]]
   [:tbody
    
    (map (fn [i]
           [:tr
            {:key (:id i)}
            [:td (idtoname (:item_a i))]
            [:td (- 100 (:magnitude i))]
            [:td (idtoname (:item_b i))]
            [:td (:magnitude i)]
            [:td (smallbutton "delete" #(delvote (:id i)))]]) @votes )]])

(defn home-page []
  (initdata)
  
  (fn []
    (let [{ :keys [left right] } (calc-heights (:percent @score))]
      [:div
       
       [info]
       
       [collapsible-cage
        false
        "VOTE"
        [:div.votearena
         [itemview (:left @score) left false]
         [itemview (:right @score) right true]
         [slider :percent (:percent @score) 0 100 nil ]
         [button "submit" sendvote]]]
       
       [collapsible-cage
        true
        "RANKING"
        [ranklist rank]]
       
       (if (not (empty? @badlist)) [collapsible-cage
                                    true
                                    "UNRANKED ITEMS"
                                    [ranklist badlist]]
           nil)
       
       [collapsible-cage
        false
        "MY VOTES"
        [votelist votes]]])))


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
