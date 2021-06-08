(ns frontsorter.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require
     [cljs-http.client :as http]
     [cljs.core.async :refer [<!]]
     [reagent.core :as r]
     [reagent.dom :as d]
     [goog.string :as gstring]))

(defn tagpage [tagid] (str "/priv/tag/disp/" tagid))

(defn sendstr [left right mag]
  (apply str (interpose "/" ["/priv/api/vote/send" js/tag js/col left right mag])))

(defn delstr []
  (if (js/confirm "delete all votes?")
    (apply str (interpose "/" ["/priv/api/tag/delvotes" js/col js/tag]))))

(defn delvotestr [vid]
  (apply str (interpose "/" ["/priv/api/vote/del" js/col js/tag vid])))

;; ------------------------ 
;; State

(def score (r/atom {:percent 50
                    :left nil :right nil
                    :name ""}))
(def rank (r/atom []))

(def votes (r/atom []))

(def options (r/atom []))

(defn handleresponse [response]
  ;; (js/console.log (-> response clj->js))
  (swap! score assoc :tag (-> response :body :tag))
  (swap! score assoc :left (-> response :body :left))
  (swap! score assoc :right (-> response :body :right))
  (swap! score assoc :percent 50)
  (reset! rank (-> response :body :sorted))
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

(defn button [text fn]
  [:div.button {:on-click fn} text])
(defn smallbutton [text fn]
  [:a {:on-click fn :class "sideeffect" :href "#"} text])

(defn itemview [item height right]
  (let [url (:url (:content item))]
    [:div.child
     {:style {:margin-top (str height "px") :text-align (if right "right" "inherit")}}
     [:h1 {:style {:margin-bottom "4px"}}
      (:name item)]
     [:span {:style {:color "red"}} (if (= "" url) "no url" url)]]))

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
  
  (let [size (count @rank)]
    [:table
     [:thead
      [:tr [:th "name"] [:th "url"] [:th "score"]]]
     [:tbody
      (for [i (range size)] (let [n (get @rank (keyword (str i)))]
                                       
                                       (js/console.log "i")
                                       (js/console.log (clj->js n))
                                       [:tr
                                        {:key (:id n)}
                                        [:td (:name n)]
                                        [:td (:url (:content n))]
                                        [:td (gstring/format "%.2f" (* 10 size (:elo n)))]]))]]))
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



(defn tagline []
  (let [tag (:tag @score)]
    [:code
     "category:" [:a {:href (tagpage (:tag_id tag))} (:title tag)]
     ";  " "public name: " [:b (:public_name tag)]
     ";  " "description: " [:i (:description tag)]]))

(defn home-page []
  (initdata)
  
  (fn []
    (let [{ :keys [left right] } (calc-heights (:percent @score))]
      [:div
       [:div.container

        [itemview (:left @score) left false]
        [itemview (:right @score) right true]
        [slider :percent (:percent @score) 0 100 nil ]
        [button "submit" sendvote]
        [:h3 "current ranking"]
        [ranklist rank]
        [:br]
        [votelist votes]
        [smallbutton "clearvotes" delvotes]]])))


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
