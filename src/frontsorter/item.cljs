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

(defn handleresponse [response]
  (js/console.log response)
  (if (:success response)
    (do
      (reset! tag (-> response :body :tag))
      (reset! item (-> response :body :item))
      (reset! sorted (-> response :body :sorted)))))

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

(defn home-page []
  (initdata)
  (fn []
    [:div
     [back @tag]
     [c/collapsible-cage true
      "ITEM"
      [tagbody @item]]
     [c/collapsible-cage true
      "MATCHUPS"
      [c/ranklist sorted]]]))


(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
