(ns frontsorter.item
  (:require
   [reagent.dom :as d]
   [re-frame.core :as rf :refer [dispatch dispatch-sync subscribe]]
   [day8.re-frame.http-fx]
   [frontsorter.views :as views]
   [frontsorter.events]
   [frontsorter.common :as c]
   [frontsorter.subs]))

(dispatch-sync [:init-db])


;; only called from js
;; TODO move these
(defn edit-item [newstate callback]
  (let [newstate (js->clj newstate :keywordize-keys true)]
    (go (let [url (url/edititemstr (:id @item))
              response (<! (http/post url {:json-params {:itemid (:id @item)
                                                         :content newstate}}))]
          (if (handleresponse response)
            (callback))))))

(defn delete-item []
  (if (js/confirm "are you sure you want to delete this item")
    (set! js/window.location (url/deleteitemstr (:id @item)))))

;; views --

(defn back []
  (let [tag @(subscribe [:tag])]
    [:a {:href (str "/t/" (:id tag))} " << " (:title tag)]))

(defn item-edit [show]
  #_(let [callback (fn []
                   (reset! show false))]
    [:> foo/ItemCreator {:inputList (c/fields-from-format (-> @tag :settings :format))
                         :editItem @item
                         :editCallback callback}])
  [:h1 "hi"])

(defn itemv []
  #_[c/editable
   nil
   (:edit_item @show) ;; TODO
   item-edit
     [c/itemview (:format (:settings @tag)) @item 10 false (:type (:settings @tag))]]
  [c/editable
   nil
   (:edit_item @(subscribe [:show]))
   item-edit
   [c/itemview :item]])

(defn votepanel [rowitem]
  (let [vote @(subscribe [:vote-on rowitem])
        [mag mag2] (c/calcmag vote (:id rowitem))
        ignoreitem @(subscribe [:item :item])
        editfn (fn [e]
                 (.stopPropagation e)
                 (dispatch [:voteonpair vote ignoreitem rowitem]))
        delfn (fn [e]
                (.stopPropagation e)
                (dispatch [:delete-vote vote]))]
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

(defn rowitem [rowitem]
  (let [size @(subscribe [:sorted-count])
        show @(subscribe [:show])]
    [:tr 
     [:td (fixelo (:elo rowitem) size)]
     ;; customize by type (display url for links?)
     
     [:td (:votecount rowitem)]
     [:td (let [url (str "/t/" js/tagid "/" (:id rowitem))
                name [:div
                      {:onClick (fn [] (set! js/window.location.href url))}
                      (:name rowitem)]
                id (:id rowitem)
                item-id @(subscribe [:item-id])
                right @(subscribe [:item :left])]
            (cond
              (and right
                   (= id (:id right))) [:b name]
              (= id item-id) [:b name]
              true name))]
     
     (if (:vote_edit show)
       [votepanel rowitem])]))

(defn ranklist []
  ;; (js/console.log "rank")
  ;; (js/console.log (clj->js  @rank))
  
  (let [sorted @(subscribe [:sorted])
        count @(subscribe [:sorted-count])]
    [:table
     [:thead
      [:tr [:th "elo"] [:th "#votes"] [:th ""]]]
     [:tbody
      (doall (for [n sorted]
               [rowitem (-> n
                            (assoc :key (:id n)))]))]]))

(defn home-page []
  #_ (fn []
       [:div
        [back @tag]
        (if @score
          [c/pairvoter score (:format (:settings @tag)) sendvote :startopen true :cancelfn #(reset! score nil)]
          [itemv])
        [c/collapsible-cage true
         "MATCHUPS"
         [ranklist]]])
  [:div
   [back]
   (case @(subscribe [:item-stage])
     :itemview [itemv]
     :voting [c/pairvoter :cancelevent [:cancelvote]])
   [c/collapsible-cage true
    "MATCHUPS"
    [ranklist]]
   ])


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
