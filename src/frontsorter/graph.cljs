(ns frontsorter.graph
  (:require
   [rid3.core :as rid3 :refer [rid3->]]
   [reagent.core :refer [atom]]))

(def height 850)
(def width 850)

(defn scale [ratom]
  (let [data @ratom
        vals (concat (mapv :x data)
                     (mapv :y data))
        lowest-value (apply min vals)
        highest-value (apply max vals)]
    (-> js/d3
        .scaleLinear
        (.rangeRound #js [height 0])
        (.domain #js [lowest-value highest-value])
        )))

(defn graph []
  (let [data (atom [{:x 3 :y 0 :link "uhh"}
                    {:x 4 :y 4 :link "uhh"}
                    {:x 5 :y 12 :link "uhh"}
                    {:x 6 :y 9 :link "uhh"}
                    {:x 7 :y 4 :link "uhh"}
                    {:x 8 :y 13 :link "uhh"}
                    {:x 9 :y 15 :link "uhh"}])]
    [rid3/viz
     {:id "graph-id"
      :ratom data
      :svg {:did-mount (fn [node ratom]
                         (rid3-> node
                                 {:width  width
                                  :height height}))}
      :pieces
      [{:kind :elem-with-data
        :class "dots"
        :tag "circle"
        :prepare-dataset (fn [atm] (clj->js @atm))
        :did-mount
        (fn [node ratom]
          (let [xy-scale (scale ratom)]
            (rid3-> node {:cx (fn [d] (xy-scale (. d -x)))
                          :cy (fn [d] (xy-scale (. d -y)))
                          :r 4
                          :fill "F3366CC"})))}]}]))
