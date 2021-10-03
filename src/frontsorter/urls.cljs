(ns frontsorter.urls)

(defn sendstr [score]
  (let [left (:id (:left score))
        right (:id (:right score))
        mag (:percent score)]
    (apply str (interpose "/" ["/api/vote/send" js/tag left right mag]))))

(defn delstr []
  (if (js/confirm "delete all votes?")
    (apply str (interpose "/" ["/api/tag/delvotes" js/tag]))))

(defn deltag []
  (str "/api/tag/del/" js/tag))

(defn delvotestr [vid]
  (apply str (interpose "/" ["/api/vote/del" js/tag vid])))

(defn addstr [] (str "/api/item/new/" js/tag))

(defn editstr [] (str "/api/tag/edit/" js/tag))

(defn tagitem [itemid] (str "/t/" js/tag "/" itemid))
