(ns frontsorter.item
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [reagent.core :as r]
   [reagent.dom :as d]
   [frontsorter.common :as c]))

(def tag (r/atom {}))

(def item (r/atom {}))

(def sorted (r/atom {}))

(def votes (r/atom {}))

(def score (r/atom {}))

(defn handleresponse [response]
  (js/console.log response)
  (if (:success response)
    (do
      ;; question, does this rerender the body four times?
      (reset! tag (-> response :body :tag))
      (reset! item (-> response :body :item))
      (reset! sorted (-> response :body :sorted))
      (reset! votes (-> response :body :votes)))))

(defn initdata []
  (handleresponse {:body (js->clj js/init :keywordize-keys true)
                   :success true}))

(defn back [tag]
  [:a {:href (str "/t/" (:id tag))} " << " (:title tag)])

(defn tagbody [item]
  [:div
   [c/itemview item 0 false]])

(defn matchupchart [tag]
  [:a {:href (str "/t/" (:id tag))} " << " (:title tag)])

(defn votepanel [vote item]
  (let [mag (if (= (:item_a vote) (:id item))
              (:magnitude vote)
              (- 100 (:magnitude vote)))
        mag2 (- 100 mag)
        editfn (fn [e]
                 (.stopPropagation e)
                 (js/console.log "uhh"))]
    [:td [:p "" [:b mag] " vs " [:b mag2] "  " (:name item)]
     [c/smallbutton "edit " editfn]]))

(defn rowitem [rowitem size vote]
  (let [ignoreitem @item
        item rowitem
        url (str "/t/" js/tag "/" (:id item) )
        row (fn [kw item]
              (if (kw item)
                (if (or
                     (= 0.00 (kw item))
                     (= (:id item) (:id ignoreitem)))
                  [:td "--"]
                  [:td {:style {:background-color
                                (str "hsl(" (* 100 (kw item)) ", 100%, 50%)")}}
                   (.toFixed (kw item) 2)])))]
    (fn [rowitem size vote] 
      [c/hoveritem :tr
       {
        :on-click (fn [] (set! js/window.location.href url))
        :key (:id item)
        }
       
       
       (if (:elo item)
         
         [:td (.toFixed (* 10 size (:elo item)) 2)])
       ;; customize by type (display url for links?)
       
       [:td ""]
       [:td (:name item)]
       
       
       ;; (row :matchup item) ;; TODO maybe make this hover text?

       (if vote
         [votepanel vote ignoreitem]
         (if (= (:id item) (:id ignoreitem))
           nil
           [:td [c/smallbutton "vote"]]))
       ;; (row :smoothmatchup item)
       
       
       ;; do we show the number of votes involving this tag?
       ])))

(defn ranklist []
  ;; (js/console.log "rank")
  ;; (js/console.log (clj->js  @rank))
  
  (let [size (count @sorted)]
    [:table
     [:thead
      [:tr [:th ""] [:th ""] [:th ""]]]
     [:tbody
      (doall
       (for [n @sorted]
         (let [vote (get @votes (keyword (:id n)))]
           [rowitem (assoc n :key (:id n)) size vote])))]]))

(defn home-page []
  (initdata)
  (fn []
    [:div
     [back @tag]
     [:div.cageparent [tagbody @item]]
     [c/collapsible-cage true
      "MATCHUPS"
      [ranklist]]]))


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
