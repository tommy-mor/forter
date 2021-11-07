(ns frontsorter.tag
  (:require ["./../tagpage/page" :as foo]
            [reagent.dom :as d]
            [reagent.core :as r]))

(defn ^:export init! []
  (d/render [:> foo/App] (.getElementById js/document "app")))
