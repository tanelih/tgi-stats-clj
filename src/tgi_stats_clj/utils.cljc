(ns tgi-stats-clj.utils)

(defn parse-int
  [s]
  #?(:clj
      (try
        (Long/parseLong s)
        (catch NumberFormatException e nil))
     :cljs
       (if (js/isNaN (js/parseInt s)) nil (js/parseInt s))))
