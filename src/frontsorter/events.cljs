(ns frontsorter.events
  (:require
   [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx path after
                          reg-fx]]
   [cljs.spec.alpha :as s]
   [ajax.core :as ajax]))

;; fill db with default db
(reg-event-db
 :init-db
 ;; TODO add spec checking here
 (fn [db _] (assoc (js->clj js/init :keywordize-keys true)
                   :percent 50)))

(reg-event-fx :failed-http-req
              (fn [{:keys [db]} [_ result]]
                {:db (case (:status result)
                       500 (assoc db :errors ["internal server error"])
                       (assoc db :errors (:errors (:response result))))
                 :delayed [:clear-errors]}))


(reg-fx :delayed (fn [event]
                   (js/setTimeout
                    #(re-frame.core/dispatch event)
                    3000)))
;; TODO make this only clear the correct error
(reg-event-db :clear-errors #(assoc % :errors []))


(defn http-effect [db m]
  {:http-xhrio (cond-> m
                 (or
                  (= :post (:method m))
                  (= :delete (:method m))
                  (= :put (:method m)))
                 (assoc :format (ajax/json-request-format))
                 
                 (not (:dont-rehydrate m))
                 (cond->
                     true (assoc-in [:params :rehydrate] true)
                     true (assoc-in [:params :tagid] js/tagid)
                     js/itemid (assoc-in [:params :itemid] js/itemid))
                 
                 true
                 (assoc :response-format (ajax/json-response-format {:keywords? true})
                        :on-failure [:failed-http-req]))
   :db db})


(reg-event-fx
 :refresh-state
 (fn [{:keys [db]} [_ params]]
   (http-effect db {:method :get
                 :uri (str "/api/tags/" js/tagid "/sorted")
                 :params params
                 :on-success [:handle-refresh (select-keys db [:left :right])]
                 :dont-rehydrate true})))

(reg-event-db :handle-refresh (fn [db [_ keep result]] (merge db result keep {:errors []})))
(reg-event-db :handle-refresh-callback (fn [db [_ callback result]]
                                         (callback)
                                         (merge db result {:errors []})))


;; ui events

(reg-event-db
 :slide
 (fn [db [_ new-perc]]
   (assoc db :percent new-perc)))

(defn voting->item [db]
  (-> db
      (assoc :item (:left db))
      (dissoc :left :right :percent)))

(reg-event-fx
 :vote
 (fn [{:keys [db]} _]
   (http-effect (if js/itemid
                  (voting->item db)
                  (assoc db :percent 50))
                
                {:method :post
                 :uri (str "/api/votes")
                 :params {:tagid (-> db :tag :id)
                          :left (-> db :left :id)
                          :right (-> db :right :id)
                          :mag (-> db :percent)}
                 :on-success [:handle-refresh]}))) 

(reg-event-fx
 :delete-vote 
 (fn [{:keys [db]} [_ vote]]
   (http-effect db {:method :delete
                    :uri (str "/api/votes/" (:id vote))
                    :params {:itemid (-> db :item :id)}
                    :on-success [:handle-refresh]})))
(reg-event-fx
 :user-selected
 (fn [{:keys [db]}
      [_ new-user]]
   {:db (assoc-in db [:users :user] new-user)
    :dispatch [:refresh-state (case new-user
                                "all users" nil
                                {:username new-user})]}))

(reg-event-fx
 :delete
 (fn [{:keys [db]} [_ voteid]]
   (http-effect db {:method :delete
                    :uri (str "/api/votes/" voteid)
                    :on-success [:handle-refresh]})))



(reg-event-fx
 :add-item
 (fn [{:keys [db]} [_ item callback]]
   (http-effect db {:method :post
                    :uri "/api/items"
                    :params (assoc item :tagid js/tagid)
                    :on-success [:handle-refresh-callback callback]})))
(reg-event-fx
 :edit-item
 (fn [{:keys [db]} [_ item callback]]
   (http-effect db {:method :put
                    :uri (str "/api/items/" (:id (:item db)))
                    :params (assoc item :tagid js/tagid)
                    :on-success [:handle-refresh-callback callback]})))

#_(defn dispatch [query-kw-str rest]
  (re-frame.core/dispatch
   (into [(keyword query-kw-str)]
         (js->clj rest :keywordize-keys true))))

(defn ^:export add_item [item callback]
  (re-frame.core/dispatch [:add-item (js->clj item :keywordize-keys true) callback]))

(defn ^:export edit_item [item callback]
  (re-frame.core/dispatch [:edit-item (js->clj item :keywordize-keys true) callback]))



;; for item page
(reg-event-db
 :voteonpair
 (fn [db [_ vote leftitem rightitem]]
   (-> db
       (assoc :left leftitem
              :right rightitem
              :percent (second
                        (frontsorter.common/calcmag vote (:id leftitem))))
       (dissoc :item))))

(reg-event-db
 :cancelvote
 (fn [db _]
   (voting->item db)))


