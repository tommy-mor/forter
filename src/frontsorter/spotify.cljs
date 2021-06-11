(ns frontsorter.spotify
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [reagent.core :as r]
   [reagent.dom :as d]
   [goog.string :as gstring]))

(defn extract-key []
  (let [str (.-location.hash js/window)]
    (nth (re-find  #"#access_token=([A-z0-9-]*)" str) 1)))

(defn authreq [] {
                  :with-credentials? false
                  :oauth-token (extract-key)})

(def playlists (r/atom []))

(defn handleresponse [resp]
  (js/console.log "api response")
  (reset! playlists (-> resp :body :items))
  (js/console.log (clj->js resp)))

(defn maketag [url name]
  (go
    (let [url url
          response (<! (http/get url (authreq)))
          finalresponse (<! (http/post "http://localhost:8080/priv/spotify/data" {:json-params (assoc (:body response)
                                                                                                      :name name)}))]
      (js/window.location.replace (str "/priv/tag/disp/" (-> finalresponse :body :newtagid))))))


(defn auth []
  (go
    (let [url "https://api.spotify.com/v1/me/playlists"
          response (<! (http/get url (authreq)))]
      (handleresponse response))))

(defn home-page []
  [:div
   (for [list @playlists]
     [:div {:key (:id list)
            :on-click #(maketag (-> list :tracks :href) (:name list))} (:name list)])])

(defn mount-root []
  (auth)
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
