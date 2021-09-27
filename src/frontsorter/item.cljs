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
        (handleresponse response)
        (reset! score nil))))

(defn delvote [vid]
  (go (let [url (url/delvotestr vid)
            response (<! (http/post url {:form-params {:voteritem (:id @item)}}))]
        (handleresponse response))))

(defn back [tag]
  [:a {:href (str "/t/" (:id tag))} " << " (:title tag)])

(defn tagbody [item]
  [:div
   [c/itemview item 10 false]])

(defn matchupchart [tag]
  [:a {:href (str "/t/" (:id tag))} " << " (:title tag)])

(defn calcmag [vote leftid]
  (if vote
    (let [mag (if (= (:item_a vote) leftid)
                (:magnitude vote)
                (- 100 (:magnitude vote)))
          mag2 (- 100 mag)]
      [mag mag2])
    [50 50]))

(defn voteonpair [vote leftitem rightitem]
  ; TODO fix magnitude going wrong direction
  (reset! score {:percent (first (calcmag vote (:id leftitem)))  :left leftitem :right rightitem}))

(defn votepanel [rowitem ignoreitem]
  (let [vote (get @votes (keyword (:id rowitem)))
        [mag mag2] (calcmag vote (:id rowitem))
        editfn (fn [e]
                 (.stopPropagation e)
                 (voteonpair vote ignoreitem rowitem))
        delfn (fn [e]
                (.stopPropagation e)
                (delvote (:id vote)))
        ]
    (if vote
      [:<>
       [:td [:<> "" [:b mag] " vs " [:b mag2] "  " (:name item)]]
       [:td 
        [c/smallbutton "edit " editfn]]
       [:td]
       [:td 
        [c/smallbutton " delete" delfn]]]
      (if (= (:id rowitem) (:id ignoreitem))
        [:td "--"]
        [:td [c/smallbutton "vote" editfn]]))))


(defn fixelo [elo size]
  (let [elo 
        (.toFixed (* 10 size elo)  2)]
    elo))

(defn rowitem [rowitem size]
  (let [ignoreitem @item
        url (url/tagitem (:id rowitem))
        row (fn [kw item]
              (if (kw item)
                (if (or
                     (= 0.00 (kw item))
                     (= (:id item) (:id ignoreitem)))
                  [:td "--"]
                  [:td {:style {:background-color
                                (str "hsl(" (* 100 (kw item)) ", 100%, 50%)")}}
                   (.toFixed (kw item) 2)])))
        ]
    (fn [rowitem size] 
      ;; [c/hoveritem {:on-click (fn [] (set! js/window.location.href url))
      ;;               :key (:id item)}]
      
      
      [:tr 
       [:td (fixelo (:elo rowitem) size)]
       ;; customize by type (display url for links?)
       
       [:td ""]
       [:td (if (= (:id rowitem) (:id (:right @score)))
              [:b (:name rowitem)]
              (:name rowitem))]
       
       [votepanel rowitem ignoreitem]
       ;; (row :matchup item) ;; TODO maybe make this hover text?

       ]
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
      (doall (for [n @sorted]
         [rowitem (assoc n :key (:id n)) size]))]]))

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
