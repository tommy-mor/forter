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


(reg-event-fx
 :handler-with-http
 (fn [{:keys [db]} _]
   {:db (assoc db :show-twirly true)
    :http-xhrio {:method :get
                 :uri "http://localhost:8080/api/tags/7ad13fcc-02a4-46cc-a45e-a5193c37e811"
                 :timeout 8000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [:good-http-result]
                 :on-failure [:bad-http-result]
                 }}))

;; ui events

(reg-event-db
 :good-http-result
 (fn [db [_ result]]
   (println "success result" result)
   (assoc db :success-http-result result)))

(reg-event-db
 :slide
 (fn [db [_ new-perc]]
   (assoc db :percent new-perc)))

(reg-event-db
 :vote
 (fn [db _]
   (println "todo")))



