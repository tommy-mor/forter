(ns frontsorter.views
  (:require [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [clojure.string :as str]
            [frontsorter.common :as c]
            ["./../tagpage/CreateTagPage" :as foo]
            [reagent.core :refer [atom]]))


(defn addpanel []
  (let [fields (c/fields-from-format
                @(subscribe [:format]))]
    [:> foo/ItemCreator {:inputList fields}]))

(defn attributes []
  (let [editing (atom false)
        new-attr-name (atom "")]
    (fn []
      (let [current-attribute @(subscribe [:current-attribute])
            attributes @(subscribe [:attributes])]
        (js/console.log attributes)
        [:div {:style {:display "flex"}} "you are voting on"
         (if @editing
           [:<> [:input {:type "text"

                         :value @new-attr-name
                         :on-change #(reset! new-attr-name (.. % -target -value))
                         :placeholder "default"}]
            [:button
             {:on-click #(do
                           (dispatch-sync [:attribute-selected @new-attr-name])
                           (reset! editing false)
                           (reset! new-attr-name ""))}
             "chose"]]
           
           [:select
            {:on-change #(let [new-attr (.. % -target -value)]
                           (case new-attr
                             "[add new attribute]" (reset! editing true)
                             (dispatch-sync [:attribute-selected new-attr])))
             :value current-attribute}
            (for [[attribute number] attributes]
              [:option {:value attribute
                        :key attribute} (str number "-" (name attribute))])
            [:option {:key "add new"} "[add new attribute]"]])
         "attribute"]))))


(defn tag-info []
  (let [{:keys [title description
                numusers numitems numvotes
                creator]} @(subscribe [:tag])
        {:keys [edit_tag]} @(subscribe [:show])]
    [c/editable-link
     "TAG"
     edit_tag
     (str "/t/" js/tagid "/edit")
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

(defn item [item]
  [c/hoveritem ^{:key (:id item)} {:on-click #(let [url (str "/t/" js/tagid "/" (:id item))]
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
                       [:td [c/smallbutton "delete" #(dispatch [:delete-vote i])]])])
                  votes)))]])

(defn errors []
  (let [errors @(subscribe [:errors])]
    [:div {:style {:color "red"}}
     (doall
      (for [error errors]
        [:pre error]))]))


(defn tag-page []
  (let [show @(subscribe [:show])]
    [:div
     

     [errors]
     [tag-info]
     
     (when (:add_items show) ;; TODO convert everything reading show dict to be a sub
       [c/collapsible-cage
        true
        "ADD"
        [addpanel]])

     (when true
       [c/collapsible-cage
        true
        "ATTRIBUTE"
        [attributes]])

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

