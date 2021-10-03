(ns frontsorter.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require
     [cljs-http.client :as http]
     [cljs.core.async :refer [<!]]
     [reagent.core :as r]
     [reagent.dom :as d]
     [frontsorter.common :as c]
     [frontsorter.urls :as url]))


;; ------------------------ 
;; State

(def score (r/atom {:percent 50
                    :left nil :right nil
                    :name ""}))
(def rank (r/atom []))

(def badlist (r/atom []))

(def votes (r/atom []))

(def options (r/atom []))

(def show (r/atom {}))

(defn handleresponse [response]
  (js/console.log (-> response clj->js))
  
  (if (:success response)
    (let [body (:body response)]
      (do 
        (swap! score assoc :tag (:tag body))
        (swap! score assoc :left (:left body))
        (swap! score assoc :right (:right body))
        (swap! score assoc :percent 50)
        (reset! rank (:sorted body))
        (reset! badlist (:votelessitems body))
        (reset! votes (:votes body))
        (reset! show (:show body))))))


(defn initdata []
  (handleresponse {:body (js->clj js/init :keywordize-keys true)
                   :success true}))


(defn sendvote []
  (go
    (let [url (url/sendstr @score)
          response (<! (http/post url))]
      (handleresponse response))))

(defn delvotes []
  (go
    (let [url (url/delstr)
          response (<! (http/post url))]
      (handleresponse response))))

(defn delvote [vid]
  (go
    (let [url (url/delvotestr vid)
          response (<! (http/post url))]
      (handleresponse response))))

(defn add-item [name]
  (if (> (count @name) 0)
    (go
      (let [url (url/addstr)
            response (<! (http/post url {:json-params {:name @name :content {}}}))]
        (handleresponse response)
        ;; maybe open vote widget from here?
        (reset! name "")))))

(defn submit-edit [newinfo]
  (go
    (let [url (url/editstr)
          response (<! (http/patch url {:form-params newinfo}))]
      (if (:success response)
        (swap! score update :tag merge (:body response))))))

(defn delete-tag []
  (if (js/confirm "do you really want to delete this tag?")
    (set! js/window.location (url/deltag))))

;; -------------------------
;; Views
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
     [c/smallbutton "submit" submit]
     [c/smallbutton "cancel" #(reset! show false)]
     [c/smallbutton "delete" delete-tag {:color "red"}]]))

;; TODO check if my user id matches tag user id
(defn info []
  (let [edit (r/atom false)]
    (fn []
      
      (let [tag (:tag @score)]
        [:div.cageparent
         [:div.cagetitle "TAG"
          (if (:edit_tag @show)
            [:div.rightcorner {:on-click #(reset! edit true)} "edit"])
          ]
         (if @edit
           [info-edit edit]
           [:div {:style {:padding-left "10px"}}
            [:h1 (:title tag)]
            [:i (:description tag)]
            [:br]
            "created by user " [:a {:href (-> tag :creator :url)} (-> tag :creator :name)]
            [:br]
            [:b (+ (count @rank) (count @badlist))] " items "
            [:b (+ (count @votes))] " votes"])
         ;; TODO get real user here
         ]))))

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
            (if (:vote_edit @show)
              [:td [c/smallbutton "delete" #(delvote (:id i))]])]) @votes )]])

(defn item [item size]
  (let [url (str "/t/" js/tag "/" (:id item) )]
    (fn [item size] 
      [c/hoveritem {
                    :on-click (fn [] (set! js/window.location.href url))
                    :key (:id item)
                    }
       
       (if (:elo item)
         
         [:td (.toFixed (* 10 size (:elo item)) 2)])
       ;; customize by type (display url for links?)
       
       [:td ""]
       [:td (:name item)]
       ])))

(defn ranklist [rank & [ignoreitem votes]]
  ;; (js/console.log "rank")
  ;; (js/console.log (clj->js  @rank))
  
  (let [size (count @rank)]
    [:table
     [:thead
      [:tr [:th ""] [:th ""] [:th ""]]]
     [:tbody
      (doall
       (for [n @rank]
         [item (assoc n :key (:id n)) size]))]]))

(defn home-page []
  (initdata)
  
  (fn []
    [:div
     
     [info]
     
     (if (:add_items @show)
       [c/collapsible-cage
        true
        "ADD"
        [addpanel]])

     (if (:vote_panel @show)
       [c/pairvoter score sendvote])
     
     (if (not-empty @rank)
       [c/collapsible-cage
        true
        "RANKING"
        [ranklist rank]])
     
     (if (not (empty? @badlist)) [c/collapsible-cage
                                  true
                                  "UNRANKED ITEMS"
                                  [ranklist badlist]]
         nil)
     
     (if (:vote_edit @show)
       [c/collapsible-cage
        false
        "MY VOTES"
        [votelist votes]])]))


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
