(ns frontsorter.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require
     [cljs-http.client :as http]
     [cljs.core.async :refer [<!]]
     [reagent.core :as r]
     [reagent.dom :as d]
     [frontsorter.common :as c]
     [frontsorter.urls :as url]
     ["./../tagpage/CreateTagPage" :as foo]))


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

(def users (r/atom {}))

(defn handleresponse [response]
  (js/console.log (-> response clj->js))
  
  (if (:success response)
    (let [body (:body response)]
      (do 
        (swap! score assoc :tag (:tag body))
        (swap! score assoc :allvotes (:allvotes body))
        (swap! score assoc :left (:left body))
        (swap! score assoc :right (:right body))
        (swap! score assoc :percent 50)
        (reset! rank (:sorted body))
        (reset! badlist (:votelessitems body))
        (reset! votes (:votes body))
        (reset! show (:show body))
        (reset! users (:users body))))))


(defn initdata []
  (handleresponse {:body (js->clj js/init :keywordize-keys true)
                   :success true}))

(defn common-params []
  "params that are in every request"
  (if-let [username (:user @users)]
    {:user username}
    {}))


(defn sendvote []
  (go
    (let [url (url/sendstr @score)
          response (<! (http/post url {:form-params (common-params)}))]
      (handleresponse response))))

;; PROBLEM: the dropdown box won't stay in sync with the rest of the commands, because only the update command knows about it.
;; if any other action happens, it will reset. maybe the answer is go to static page of specific user ranking?
(defn only-users [username]
  (swap! users assoc :user username)
  (go (let 
          [url (url/tagstate)
           response (<! (http/get url (if (not= "all users" username) {:query-params (common-params)}
                                          nil)))]
        (handleresponse response))))

(defn delvotes []
  (go
    (let [url (url/delstr)
          response (<! (http/post url (:query-params (common-params))))]
      (handleresponse response))))

(defn delvote [vid]
  (go
    (let [url (url/delvotestr vid)
          response (<! (http/post url {:query-params (common-params)}))]
      (handleresponse response))))


;; only called from js file, not cljs
(defn add-item [info callback]
  (let [info (js->clj info :keywordize-keys true)]
    (if (> (count (:name info)) 0)
      (go
        (let [url (url/addstr)
              response (<! (http/post url {:json-params (merge (common-params)
                                                               info)}))]
          (if (:success response)
            (do
              (handleresponse response)
              (callback))))))))

(defn submit-edit [newinfo]
  (go
    (let [url (url/editstr)
          response (<! (http/patch url {:form-params (merge (common-params) newinfo)}))]
      (if (:success response)
        (swap! score update :tag merge (:body response))))))

(defn delete-tag []
  (if (js/confirm "do you really want to delete this tag?")
    (set! js/window.location (url/deltag))))

;; -------------------------
;; Views
(comment (defn addpanel []
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
        [:button {:on-click #(add-item title)} "add item"]]))))
(defn addpanel []
  (js/console.log "tag")
  (js/console.log @score)
  (let [field2bool (-> @score
                       :tag
                       :settings
                       :format)
        fields (vec (filter identity
                            (for [k ["name" "url" "paragraph"]]
                              (if ((keyword k) field2bool)
                                k))))]
    (js/console.log "ttt" )
    [:> foo/ItemCreator {:inputList fields}]))

(comment
  "TODO replace with jsx version..."
  (defn info-edit [show]
   (let [tag (:tag @score)
         form
         (r/atom {:title (:title tag)
                  :description (:description tag)})]
     [c/editpage
      form
      show
      (fn []
        (submit-edit @form)
        (reset! show false))
      delete-tag])))

;; TODO check if my user id matches tag user id
(defn info [tag]
  [c/editable-link
   "TAG"
   (:edit_tag @show)
   (url/editstr)
   [:div {:style {:padding-left "10px"}}
    
    [:h1 (:title tag)]
    [:i (:description tag)]
    [:br]
    "created by user " [:a {:href (-> tag :creator :url)} (-> tag :creator :name)]
    [:br]
    [:b (+ (count @rank) (count @badlist))] " items "
    [:b (:allvotes @score)] " votes by " [:b (count (:users @users))]
    " users"
    ;; TODO make this use correct plurality/inflection
    ]])

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
    
    (doall (map (fn [i]
            [:tr
             {:key (:id i)}
             [:td (idtoname (:item_a i))]
             [:td (- 100 (:magnitude i))]
             [:td (idtoname (:item_b i))]
             [:td (:magnitude i)]
             (if (:vote_edit @show)
               [:td [c/smallbutton "delete" #(delvote (:id i))]])]) @votes))]])

(defn item [item size]
  (let [url (str "/t/" js/tag "/" (:id item) )]
    (fn [item size] 
      [c/hoveritem ^{:key (:id item)} {
                    :on-click (fn [] (set! js/window.location.href url))
                    :key (:id item)
                    }
       
       (if (:elo item)
         
         [:td {:key 1} (.toFixed (* 10 size (:elo item)) 2)])
       ;; customize by type (display url for links?)
       
       [:td {:key 2} (:votecount item)]
       [:td {:key 3} (:name item)]
       ])))

(defn ranklist [rank & [ignoreitem votes]]
  ;; (js/console.log "rank")
  ;; (js/console.log (clj->js  @rank))
  
  (let [size (count @rank)]
    [:div "by user "
     [:form {:autoComplete "off"}
      [:select {:on-change #(only-users (.. % -target -value))
                :value (or (:user @users) "all users")
                :autoComplete "nope"}  
       [:option {:value "all users"} "all users combined"]
       (for [user (:users @users)]
         [:option {:key user :value user} user])]]

     
     [:table
      [:thead
       [:tr [:th ""] [:th ""] [:th ""]]]
      [:tbody
       (doall
        (for [n @rank]
          [item (assoc n :key (:id n)) size]))]]]))

(defn home-page []
  (initdata)
  
  (fn []
    [:div
     
     [info (:tag @score)]
     
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
        (str "MY VOTES (" (count @votes) ")")
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
