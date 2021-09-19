(ns frontsorter.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require
     [cljs-http.client :as http]
     [cljs.core.async :refer [<!]]
     [reagent.core :as r]
     [reagent.dom :as d]
     [frontsorter.common :as c]))

(defn tagpage [tagid] (str "/tag/disp/" tagid))

(defn sendstr [left right mag]
  (apply str (interpose "/" ["/api/vote/send" js/tag left right mag])))

(defn delstr []
  (if (js/confirm "delete all votes?")
    (apply str (interpose "/" ["/api/tag/delvotes" js/tag]))))

(defn delvotestr [vid]
  (apply str (interpose "/" ["/api/vote/del" js/tag vid])))

(defn addstr [] (str "/api/item/new/" js/tag))

(defn editstr [] (str "/api/tag/edit/" js/tag))

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
  
  (if (:success response)
    (let [body (:body response)]
      (do 
        (swap! score assoc :tag (:tag body))
        (swap! score assoc :left (:left body))
        (swap! score assoc :right (:right body))
        (swap! score assoc :editable (:editable body))
        (swap! score assoc :percent 50)
        (reset! rank (:sorted body))
        (reset! badlist (:baditems body))
        (reset! votes (:votes body))))))


(defn initdata []
  (handleresponse {:body (js->clj js/init :keywordize-keys true)
                   :success true}))


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

(defn add-item [name]
  (if (> (count @name) 0)
    (go
      (let [url (addstr)
            response (<! (http/post url {:form-params {:name @name :content "{}"}}))]
        (handleresponse response)
        ;; maybe open vote widget from here?
        (reset! name "")))))

(defn submit-edit [newinfo]
  (go
    (let [url (editstr)
          response (<! (http/patch url {:form-params newinfo}))]
      (js/console.log @score)
      (if (:success response)
        (swap! score assoc :tag (:body response))))))

;; -------------------------
;; Views
(defn smallbutton [text fn]
  [:a {:on-click fn :class "sideeffect" :href "#"} text])

(defn addpanel []
  (let [title (r/atom "")
        on-key-down (fn [k title]
                      (condp = (.-which k)
                        13 (add-item title)
                        nil))]
    (fn [] 
      [:div.addpanel
       [:input.addinput {:type "text"
                         :value @title
                         :placeholder "new item name"
                         :on-change #(reset! title (-> % .-target .-value))
                         :on-key-down #(on-key-down % title)}]
       [:button {:on-click #(add-item title)} "add item"]])))

(defn info-edit [show]
  (let [tag (:tag @score)
        newinfo (r/atom {:title (:title tag) :description (:description tag)})
        submit (fn []
                 (submit-edit @newinfo)
                 (reset! show false))
        inp (fn [attr] [:input.editinput {:type "text" :value (attr @newinfo)
                                          :on-change #(swap! newinfo assoc attr (-> % .-target .-value))
                                          :on-key-down #(condp = (.-which %)
                                                          13 (submit)
                                                          nil)}])]
    ;; TODO check that its valid, then submit to server
    [:div.votearena 
     [inp :title]
     [inp :description]
     [smallbutton "submit" submit]
     [smallbutton "cancel" #(reset! show false)]]))

;; TODO check if my user id matches tag user id
(defn info []
  (let [edit (r/atom false)]
    (fn []
      
      (let [tag (:tag @score)]
        [:div.cageparent
         [:div.cagetitle "TAG"
          (if (:editable @score)
            [:div.rightcorner {:on-click #(reset! edit true)} "edit"])
          ]
         (if @edit
           [info-edit edit]
           [:div {:style {:padding-left "10px"}}
            [:h1 (:title tag)]
            [:i (:description tag)]
            [:br]
            "created by user " ;;TODO
            [:br]
            [:b (+ (count @rank) (count @badlist))] " items "
            [:b (+ (count @votes))] " votes"])
         ;; TODO get real user here
         ]))))

(defn button [text fn]
  [:div.button {:on-click fn} text])

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

(defn idtoname [itemid]
  ;; (js/console.log "itemid")
  ;; (js/console.log itemid)
  (let [a (filter (fn [i]
                    (= (:id i) itemid)) @rank)]
    (:name (first a))))



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
       
       [c/collapsible-cage
        true
        "ADD"
        [addpanel]]

       (if (:left @score)
         [c/collapsible-cage
          false
          "VOTE"
          [:div.votearena
           [c/itemview (:left @score) left false]
           [c/itemview (:right @score) right true]
           [slider :percent (:percent @score) 0 100 nil ]
           [button "submit" sendvote]]])
       
       (if (not-empty @rank)
         [c/collapsible-cage
          true
          "RANKING"
          [c/ranklist rank]])
       
       (if (not (empty? @badlist)) [c/collapsible-cage
                                    true
                                    "UNRANKED ITEMS"
                                    [c/ranklist badlist]]
           nil)
       
       [c/collapsible-cage
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
