(ns frontsorter.events
  (:require
   [frontsorter.db :refer [default-db]]
   [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx path after
                          reg-fx]]
   [cljs.spec.alpha :as s]
   [ajax.core :as ajax]))

;; fill db with default db
(reg-event-db
 :init-db
 ;; TODO add spec checking here
 (fn [db _] default-db))

(reg-event-fx :failed-http-req
              (fn [{:keys [db]} [_ result]]
                (js/console.log result)
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


(defn http-effect [m]
  {:http-xhrio (merge (when (or
                             (= :post (:method m))
                             (= :delete (:method m)))
                        {:format (ajax/json-request-format)})

                      (if (:dont-rehydrate m)
                        m
                        (-> m
                            (assoc-in [:params :rehydrate] true)
                            (assoc-in [:params :tagid] js/tagid)))

                      {:response-format (ajax/json-response-format {:keywords? true})
                       :on-failure [:failed-http-req]})})


(reg-event-fx
 :refresh-state
 (fn [{:keys [db]} [_ params]]
   (http-effect {:method :get
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

(reg-event-fx
 :vote
 (fn [{:keys [db]} _]
   (merge (http-effect {:method :post
                        :uri (str "/api/votes")
                        :params {:tagid (-> db :tag :id)
                                 :left (-> db :left :id)
                                 :right (-> db :right :id)
                                 :mag (-> db :percent)}
                        :on-success [:handle-refresh]})

          {:db (assoc db :percent 50)}))) 

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
   (http-effect {:method :delete
                 :uri (str "/api/votes/" voteid)
                 :on-success [:handle-refresh]})))



(reg-event-fx
 :add-item
 (fn [{:keys [db]} [_ item callback]]
   (http-effect {:method :post
                 :uri "/api/items"
                 :params (assoc item :tagid js/tagid)
                 :on-success [:handle-refresh-callback callback]})))

(defn dispatch [query-kw-str rest]
  (re-frame.core/dispatch
   (into [(keyword query-kw-str)]
         (js->clj rest :keywordize-keys true))))

(defn ^:export add_item [item callback]
  (re-frame.core/dispatch [:add-item (js->clj item :keywordize-keys true) callback]))
