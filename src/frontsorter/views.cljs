(ns frontsorter.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [clojure.string :as str]
            [frontsorter.common :as c]
            ["./../tagpage/CreateTagPage" :as foo]))


(defn addpanel []
  (let [fields (c/fields-from-format
                @(subscribe [:format]))]
    [:> foo/ItemCreator {:inputList fields}]))


(defn tag-info []
  (let [{:keys [title description
                numusers numitems numvotes
                creator]}
        @(subscribe [:tag])]
    [c/editable-link
     "TAG"
     true
     "uhhhhh???"
     [:div {:style {:padding-left "10px"}}
      
      [:h1 title]
      [:i description]
      [:br]
      "created by user " [:a {:href (creator :url)} (creator :name)]
      [:br]
      [:b numitems] " items "
      [:b numvotes] " votes by " [:b numusers] " users"
      ;; TODO make this use correct plurality/inflection
      ]]))

;; TODO replace with subscription/query
#_(defn idtoname [itemid rank]
  (let [a (filter (fn [i]
                    (= (:id i) itemid)) rank)]
    (:name (first a))))



(defn item [item]
  [c/hoveritem ^{:key (:id item)} {:on-click #(let [url "https://google.com"]
                                                (set! js/window.location.href url))
                                   :key (:id item)}
   
   (when (:elo item)
     
     [:td {:key 1} (-> (* 10
                          @(subscribe [:sorted-count])
                          (:elo item))
                       (.toFixed 2))])
   ;; customize by type (display url for links?)
   
   [:td {:key 2} (:votecount item)]
   [:td {:key 3} (:name item)]])

(defn sortedlist [sorted sorted-count]
  (let [size @(subscribe [sorted-count])
        sorted @(subscribe [sorted])
        all-users @(subscribe [:all-users])
        selected-user @(subscribe [:selected-user])]
    
    [:div "by user "
     [:form {:autoComplete "off"}
      [:select {:on-change #(dispatch [:user-selected (.. % -target -value)])
                :value selected-user
                :autoComplete "nope"}  
       [:option {:value "all users"} "all users combined"]
       (for [user all-users]
         [:option {:key user :value user} user])]]

     
     [:table
      [:thead
       [:tr [:th ""] [:th ""] [:th ""]]]
      [:tbody
       (doall
        (for [item-i sorted]
          (let [item-i (assoc item-i :key (:id item-i))]
            [item item-i])))]]]))

(defn votelist []

  ;;(js/console.log "votes")
  ;;(js/console.log (clj->js  @votes))
  [:table
   [:thead
    [:tr [:th "left"] [:th "pts"] [:th "right"] [:th "pts"]]]
   [:tbody
    (let [idtoname @(subscribe [:idtoname])
          votes @(subscribe [:votes])]
      
      (doall (map (fn [i]
                    [:tr
                     {:key (:id i)}
                     [:td (idtoname (:item_a i))]
                     [:td (- 100 (:magnitude i))]
                     [:td (idtoname (:item_b i))]
                     [:td (:magnitude i)]
                     (if (:vote_edit @(subscribe [:show]))
                       [:td [c/smallbutton "delete" #(println (:id i))]])])
                  votes)))]])


(defn tag-page []
  (let [show @(subscribe [:show])]
    [:div
     

     [tag-info]
     
     (when (:add_items show) ;; TODO convert everything reading show dict to be a sub
       [c/collapsible-cage
        true
        "ADD"
        [addpanel]])

     (when (:vote_panel show)
       [c/pairvoter])
     
     (when @(subscribe [:sorted-not-empty])
       [c/collapsible-cage
        true
        "RANKING"
        [sortedlist :sorted :sorted-count]])
     
     (when @(subscribe [:unsorted-not-empty])
          [c/collapsible-cage
           true
           "UNRANKED ITEMS"
           [sortedlist :unsorted :unsorted-count]])
     
     (when (:vote_edit show)
       [c/collapsible-cage
        false
        (str "MY VOTES (" @(subscribe [:votes-count]) ")")
        [votelist]])]))




