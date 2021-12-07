(ns frontsorter.common
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [reagent.core :as r]))

(defn collapsible-cage [open title & children]
  (let [collapsed (r/atom (not open))]
    (fn [open title children]
      [:div.cageparent
       [:div.cagetitle
        {:on-click (fn [e] (swap! collapsed not))}
        (if @collapsed
          (str title " >>")
          (str title " <<"))]
       (if @collapsed
         nil
         children)])))

(defn spotify-player [url]
  [:iframe {:src url
            :width 300 :height 80
            :allowtransparency "true" :allow "encrypted-media"}])

(defn fields-from-format [format]
  (vec (filter identity
               (for [k ["name" "url" "paragraph"]]
                 (if ((keyword k) format)
                   k)))))

(defn url-displayer [[url embedurl] format]
  (cond
    ((keyword "any website") format) [:a {:href url
                                          :target "_blank"} url]
    ((keyword "image link") format)  [:img {:src url
                                            :style {:max-width "100%"}}]
    (or ((keyword "youtube") format)
        ((keyword "youtube with timestamp") format))
    [:div {:style {:padding-bottom "56.25%"
                   :position "relative"
                   :width "100%"
                   :height 0}}[:iframe {:src embedurl :style {:height "100%"
                                                              :width "100%"
                                                              :position "absolute"
                                                              :top 0
                                                              :left 0
                                                              }
                                        :allow-full-screen true}]]
    ((keyword "spotify") format) [spotify-player embedurl]
    
    true [:span "unknown format"]))

(defn itemview [format item height right]
  (let [url ((juxt :url :embedurl) (:content item)) 
        spotify-id  (-> item :content :spotify_id) ;; TODO GET RID OF THIS, MATCH ON URL
        paragraph (-> item :content :paragraph)]
    
    [:div
     {:class (if right "rightitem" "leftitem")
      :style {:margin-top (str height "px")}}
     
     (if (:name format)
       [:h1 {:style {:margin-bottom "4px"}} (:name item)])
     (if (:url format)
       [url-displayer url (:url format)])
     (if (:paragraph format)
       [:fragment 
        [:br]
        [:pre {:style {:color "red"
                       :white-space "pre-line"}} paragraph]])]))

(defn smallbutton [text fn & [style]]
  [:a {:on-click fn :style style :class "sideeffect" :href "#"} text])

(defn hoveritem [keys & children]
  (let [hovered (r/atom false)]
    (fn [keys & children]
      [:tr
       (merge keys 
              {
               :on-mouse-over (fn [] (reset! hovered true))
               :on-mouse-out (fn [] (reset! hovered false))
               ;; :key TODO
               :class (if @hovered "item hovered" "item")
               })
       children])))

(defn editable [title is-editable edit-body body]
  (let [edit (r/atom false)]
    (fn [title is-editable edit-body body]
      [:div.cageparent
       [:div.cagetitle title
        (if is-editable
          [:div.rightcorner {:on-click #(reset! edit true)} "edit"])]
       (if @edit
         [edit-body edit]
         body)
       ;; TODO get real user here
       ])))

(defn editable-link [title is-editable url body]
  [:div.cageparent
   [:div.cagetitle title
    (if is-editable
      [:div.rightcorner {:on-click #(set! js/window.location.href url)} "edit"])]
   body])

(defn editpage [stateatom showatom submitfn deletefn]
  
  [:div.votearena
   (into [:<>]
         (for [[attr v] @stateatom]
           ^{:key attr}
           [:input.editinput {:type "text" :value v
                              :on-change #(let [v (-> % .-target .-value)]
                                            (swap! stateatom assoc attr v))
                              :on-key-down #(condp = (.-which %)
                                              13 (submitfn)
                                              nil)}]))
   [smallbutton "submit" submitfn]
   [smallbutton "cancel" #(reset! showatom false)]
   [smallbutton "delete" deletefn {:color "red"}]])


;; slider stuff
(defn slider [param value min max score]
  [:input.slider {:type "range" :value value :min min :max max
                  :on-change (fn [e]
                               (let [new-value (js/parseInt (.. e -target -value))]
                                 (swap! score
                                        (fn [data]
                                          (assoc data param new-value)))))}])


(defn button [text fn & {:keys [class] :or {class "button"}}]
  [:div {:class (str "button " class) :on-click fn} text])


(defn calc-heights [perc]
  {:right (/ (min 0 (- 50 perc)) 2) 
   :left (/ (min 0 (- perc 50)) 2)})


(defn styles-format [format]
  (js/console.log "format" format)
  (if (or (-> format :url :youtube)
          (-> format :url ((keyword "youtube with timestamp"))))
    {:width "150%"
     :transform "translate(-16.6%, 0)"}
    {}))

(defn pairvoter [score format sendvote &
                 {:keys [cancelfn startopen]
                  :or {cancelfn nil startopen false}}]
  (let [{:keys [left right]} (calc-heights (:percent @score))]
    
    (js/console.log format)
    [collapsible-cage true "VOTE"
     [:div.votearena
      {:style (styles-format format)}
      [itemview format (:left @score) left false type]
      [itemview format (:right @score) right true type]
      [slider :percent (:percent @score) 0 100 score]
      [button "submit" sendvote]
      (when cancelfn
        [button "cancel" cancelfn :class "cancelbutton"])]]))

