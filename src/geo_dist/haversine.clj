(ns geo-dist.haversine
  (:require [geo-dist.math :refer :all]))

(def ^:private R 6372800.)

(defn distance [lat1 lon1 lat2 lon2]
  (let [φ1 (deg->rad lat1)
        φ2 (deg->rad lat2)
        λ1 (deg->rad lon1)
        λ2 (deg->rad lon2)]
    (* 2 R (asin (sqrt (+ (sq (sin (/ (- φ1 φ2) 2)))
                          (* (cos φ1) (cos φ2) (sq (sin (/ (- λ1 λ2) 2))))))))))
