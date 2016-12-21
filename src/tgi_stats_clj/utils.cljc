(ns tgi-stats-clj.utils)

(defn parse-int
  "Parse an integer from the given string."
  [s]
  #?(:clj
      (try
        (Integer/parseInt s)
        (catch NumberFormatException e nil))
     :cljs
       (if (js/isNaN (js/parseInt s)) nil (js/parseInt s))))
