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
            response (<! (http/post url {:form-params {:itemid (:id @item)}}))]
        (handleresponse response)
        (reset! score nil))))

(defn delvote [vid]
  (go (let [url (url/delvotestr vid)
            response (<! (http/post url {:form-params {:itemid (:id @item)}}))]
        (handleresponse response))))

;; views --

(defn back [tag]
  [:a {:href (str "/t/" (:id tag))} " << " (:title tag)])

(defn tagbody []
  [:div
   [c/itemview @item 10 false]])

(defn calcmag [vote leftid]
  (if vote
    (let [mag (if (= (:item_a vote) leftid)
                (:magnitude vote)
                (- 100 (:magnitude vote)))
          mag2 (- 100 mag)]
      [mag mag2])
    [50 50]))

(defn voteonpair [vote leftitem rightitem]
  (reset! score
          {:percent (first (calcmag vote (:id leftitem)))
           :left leftitem :right rightitem} ))

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
       [:td [:<> "" [:b mag] " vs " [:b mag2] "  " (:name ignoreitem)]]
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
        ;; url
        ;; (url/tagitem (:id rowitem))
        ]
    (fn [rowitem size] 
      [:tr 
       [:td (fixelo (:elo rowitem) size)]
       ;; customize by type (display url for links?)
       
       [:td ""]
       [:td (let [name (:name rowitem)]
              (if (:right @score) 
                (if (= (:id rowitem) (:id (:right @score)))
                  [:b name]
                  name)
                (if (= (:id rowitem) (:id ignoreitem))
                  [:b name]
                  name)))]
       
       [votepanel rowitem ignoreitem]])))

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
       [c/pairvoter score sendvote :startopen true :cancelfn #(reset! score nil)]
       [:div.cageparent [tagbody]])
     [c/collapsible-cage true
      "MATCHUPS"
      [ranklist]]]))


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
