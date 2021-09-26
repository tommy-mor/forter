(ns frontsorter.item
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [reagent.core :as r]
   [reagent.dom :as d]
   [frontsorter.common :as c]
   [frontsorter.urls :as url]))

(def tag (r/atom {}))

(def item (r/atom {}))

(def sorted (r/atom []))

(def votes (r/atom {}))

(def score (r/atom nil))

(defn handleresponse [response]
  (js/console.log response)
  (if (:success response)
    (let [body (:body response)]
      (do
        ;; question, does this rerender the body four times?
        (reset! tag (:tag body))
        (reset! item (:item body))
        (reset! sorted (:sorted body))
        (reset! votes (:votes body))))))

(defn initdata []
  (handleresponse {:body (js->clj js/init :keywordize-keys true)
                   :success
                   true}))

(defn sendvote []
  (go (let [url (url/sendstr @score)
            response (<! (http/post url {:form-params {:voteritem (:id @item)}}))]
        (if (:success response)
          (do
            (js/console.log response)
            
            (reset! sorted (:sorted (:body response)))
            (reset! votes (:votes (:body response)))))
        (reset! score nil))))

(defn back [tag]
  [:a {:href (str "/t/" (:id tag))} " << " (:title tag)])

(defn tagbody [item]
  [:div
   [c/itemview item 10 false]])

(defn matchupchart [tag]
  [:a {:href (str "/t/" (:id tag))} " << " (:title tag)])

(defn votepanel [vote item editfn]
  (let [mag (if (= (:item_a vote) (:id item))
              (:magnitude vote)
              (- 100 (:magnitude vote)))
        mag2 (- 100 mag)]
    [:<>
     [:td [:<> "" [:b mag] " vs " [:b mag2] "  " (:name item)]]
     [c/smallbutton "edit " editfn]]))

(defn voteonpair [leftitem rightitem]
  (reset! score {:percent 50 :left leftitem :right rightitem}))

(defn fixelo [elo size]
  (js/console.log "fixelo")
  (let [elo 
        (.toFixed (* 10 size elo)  2)]
    (js/console.log elo)
    elo))

(defn rowitem [rowitem size vote]
  (let [ignoreitem @item
        item rowitem
        url (url/tagitem (:id item))
        row (fn [kw item]
              (if (kw item)
                (if (or
                     (= 0.00 (kw item))
                     (= (:id item) (:id ignoreitem)))
                  [:td "--"]
                  [:td {:style {:background-color
                                (str "hsl(" (* 100 (kw item)) ", 100%, 50%)")}}
                   (.toFixed (kw item) 2)])))
        editfn (fn [edit] (fn [e]
                            (.stopPropagation e)
                            (voteonpair ignoreitem item)))]
    (fn [rowitem size vote] 
      ;; [c/hoveritem {:on-click (fn [] (set! js/window.location.href url))
      ;;               :key (:id item)}]
      
      
      [:tr 
       [:td (fixelo (:elo rowitem) size)]
       ;; customize by type (display url for links?)
       
       [:td ""]
       [:td (:name item)]
       (js/console.log "rendering row")
       (js/console.log item)
       
       
       ;; (row :matchup item) ;; TODO maybe make this hover text?

       (if vote
         [votepanel vote ignoreitem (editfn true)]
         (if (= (:id item) (:id ignoreitem))
           nil
           [:td [c/smallbutton "vote" (editfn false)]]))]
      ;; (row :smoothmatchup item)
      
      
      ;; do we show the number of votes involving this tag?
      )))

(defn ranklist []
  ;; (js/console.log "rank")
  ;; (js/console.log (clj->js  @rank))
  
  (let [size (count @sorted)]
    [:table
     [:thead
      [:tr [:th ""] [:th ""] [:th ""]]]
     [:tbody
      (for [n @sorted]
        (let [vote (get @votes (keyword (:id n)))]
          [rowitem (assoc n :key (:id n)) size vote]))]]))

(defn home-page []
  (initdata)
  (fn []
    [:div
     [back @tag]
     (if @score
       [c/pairvoter score true sendvote]
       [:div.cageparent [tagbody @item]])
     [c/collapsible-cage true
      "MATCHUPS"
      [ranklist]]]))


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
