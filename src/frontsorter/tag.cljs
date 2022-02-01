(ns frontsorter.tag
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.dom :as d]
   [re-frame.core :as rf :refer [dispatch dispatch-sync]]
   [day8.re-frame.http-fx]
   [frontsorter.views :as views]
   [frontsorter.events]
   [frontsorter.subs]))

;; println now does console.log
(enable-console-print!)

(dispatch-sync [:init-db])

;; initialize app
(defn mount-root []
  (d/render [views/tag-page] (.getElementById js/document "app")))


(defn ^:export init! []
  (mount-root))



;; ------------------------ 
;; State

;; (defn handleresponse [response]
;;   (js/console.log (-> response clj->js))
  
;;   (if (:success response)
;;     (let [body (:body response)]
;;       (do 
;;         (swap! score assoc :tag (:tag body))
;;         (swap! score assoc :allvotes (:allvotes body))
;;         (swap! score assoc :left (:left body))
;;         (swap! score assoc :right (:right body))
;;         (swap! score assoc :percent 50)
;;         (reset! rank (:sorted body))
;;         (reset! badlist (:votelessitems body))
;;         (reset! votes (:votes body))
;;         (reset! show (:show body))
;;         (reset! users (:users body))))))


;; (defn common-params []
;;   "params that are in every request"
;;   (if-let [username (:user @users)]
;;     {:user username}
;;     {}))


;; (defn sendvote []
;;   (go
;;     (let [url (url/sendstr @score)
;;           response (<! (http/post url {:form-params (common-params)}))]
;;       (handleresponse response))))

;; PROBLEM: the dropdown box won't stay in sync with the rest of the commands, because only the update command knows about it.
;; if any other action happens, it will reset. maybe the answer is go to static page of specific user ranking?
;; (defn only-users [username]
;;   (swap! users assoc :user username)
;;   (go (let 
;;           [url (url/tagstate)
;;            response (<! (http/get url (if (not= "all users" username) {:query-params (common-params)}
;;                                           nil)))]
;;         (handleresponse response))))

;; (defn delvotes []
;;   (go
;;     (let [url (url/delstr)
;;           response (<! (http/post url (:query-params (common-params))))]
;;       (handleresponse response))))

;; (defn delvote [vid]
;;   (go
;;     (let [url (url/delvotestr vid)
;;           response (<! (http/post url {:query-params (common-params)}))]
;;       (handleresponse response))))


;; only called from js file, not cljs
;; (defn add-item [info callback]
;;   (let [info (js->clj info :keywordize-keys true)]
;;     (if (> (count (:name info)) 0)
;;       (go
;;         (let [url (url/addstr)
;;               response (<! (http/post url {:json-params (merge (common-params)
;;                                                                info)}))]
;;           (if (:success response)
;;             (do
;;               (handleresponse response)
;;               (callback))))))))

;; (defn submit-edit [newinfo]
;;   "submit an edit to a tag"
;;   (go
;;     (let [url (url/editstr)
;;           response (<! (http/patch url {:form-params (merge (common-params) newinfo)}))]
;;       (if (:success response)
;;         (swap! score update :tag merge (:body response))))))

;; (defn delete-tag []
;;   (if (js/confirm "do you really want to delete this tag?")
;;     (set! js/window.location (url/deltag))))

;; -------------------------
;; Views


;; TODO check if my user id matches tag user id



;; -------------------------
;; Initialize app


                                        ; TODO
                                        ; then connect with backend (make json api)
                                        ; display the votes
                                        ; links to rest of site real
                                        ; make bottom panels collapsible?
                                        ; make button only go pink (clickable) once you've changed the ranking at all

                                        ; make it load the things straight from the html or the dom, to avoid road trip.
                                        ; right now, just road trip.
                                        ; TODO add kanban
                                        ; TODO set up hosted version, maybe way to deploy through gh actions
                                        ; make slider easier to press (wider surface area to click)
                                        ; make votes editable (many mini sliders, in collapsible panel (all panels are collapsible))
                                        ; in 'current ranking', select items to 'pin' on left or right
