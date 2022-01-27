(ns frontsorter.events
  (:require
   [frontsorter.db :refer [default-db]]
   [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx path after]]
   [cljs.spec.alpha :as s]
   [ajax.core :as ajax]))

;; fill db with default db
(reg-event-db
 :init-db
 ;; TODO add spec checking here
 (fn [db _] default-db))


(defn http-effect [m]
  {:http-xhrio (merge (when (= :post (:method m))
                        {:format (ajax/json-request-format)})
                      {:response-format (ajax/json-response-format {:keywords? true})
                       :on-failure [:failed-http-req]}
                      (if (:dont-rehydrate m)
                        m
                        (assoc-in m [:params :rehydrate] true)))})


(reg-event-fx
 :refresh-state
 (fn [{:keys [db]} [_ params]]
   (http-effect {:method :get
                 :uri (str "/api/tags/" js/tag "/sorted")
                 :params params
                 :on-success [:handle-refresh (select-keys db [:left :right])]
                 :dont-rehydrate true})))

(reg-event-db :handle-refresh (fn [db [_ keep result]] (merge db result keep)))


;; ui events

(reg-event-db
 :slide
 (fn [db [_ new-perc]]
   (assoc db :percent new-perc)))

(reg-event-fx
 :vote
 (fn [{:keys [db]} _]
   (http-effect {:method :post
                 :uri (str "/api/votes")
                 :params {:tagid (-> db :tag :id)
                          :left (-> db :left :id)
                          :right (-> db :right :id)
                          :mag (-> db :percent)}
                 :on-success [:handle-refresh]}))) 

(reg-event-fx
 :user-selected
 (fn [{:keys [db]}
      [_ new-user]]
   {:db (assoc-in db [:users :user] new-user)
    :dispatch [:refresh-state (case new-user
                                "all users" nil
                                {:username new-user})]}))



