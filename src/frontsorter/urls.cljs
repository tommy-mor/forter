(ns frontsorter.urls
  (:require ["jorter" :as j]))

(defn sendstr [score]
  (let [left (:id (:left score))
        right (:id (:right score))
        mag (:percent score)]
    (apply str (interpose "/" ["/api/vote/send" js/tag left right mag]))))

(defn tagstate []
  (apply str (interpose "/" ["/api/tag/pair" js/tag])))

(defn delstr []
  (if (js/confirm "delete all votes?")
    (apply str (interpose "/" ["/api/tag/delvotes" js/tag]))))

(defn deltag []
  (str "/api/tag/del/" js/tag))

(defn delvotestr [vid]
  (apply str (interpose "/" ["/api/vote/del" js/tag vid])))

(defn addstr [] (str "/api/item/new/" js/tag))
(defn edititemstr [itemid] (str "/api/item/edit/" js/tag "/" itemid))
(defn deleteitemstr [itemid] (str "/api/item/delete/" js/tag "/" itemid))

(defn editstr [] (str "/priv/tags/edit/" js/tag))
(defn tagitem [itemid] (str "/t/" js/tag "/" itemid))

(defn tag [tagid] (str "/t/" tagid))

;; SUCCESS we imported. now to translate entire thing to re-frame, cause I want all the model code in a different file anyway...
(defn sss [uhh] j)
