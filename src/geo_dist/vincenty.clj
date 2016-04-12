(ns geo-dist.vincenty
  "Vincenty approximations using WGS84

  All angles in degrees and distances in meters."
  (:require [geo-dist.math :refer :all]))

(def ^:private a 6378137.)
(def ^:private b 6356752.314245)
(def ^:private f (/ (- a b) a))

(defn inverse
  "Returns the distance between (lat1, lon1) (lat2, lon2)."
  [lat1 lon1 lat2 lon2]
  (let [ϕ1 (deg->rad lat1)
        ϕ2 (deg->rad lat2)
        λ1 (deg->rad lon1)
        λ2 (deg->rad lon2)
        L (- λ2 λ1)
        tanU1 (* (- 1 f) (tan ϕ1))
        cosU1 (/ 1 (sqrt (+ 1 (sq tanU1))))
        sinU1 (* tanU1 cosU1)
        tanU2 (* (- 1 f) (tan ϕ2))
        cosU2 (/ 1 (sqrt (+ 1 (sq tanU2))))
        sinU2 (* tanU2 cosU2)]
    (loop [λ L
           λ' 0
           iterations 0]
      (let [sinλ (sin λ)
            cosλ (cos λ)
            sinSqσ (+ (sq (* cosU2 sinλ))
                      (sq (- (* cosU1 sinU2) (* sinU1 cosU2 cosλ))))
            sinσ (sqrt sinSqσ)]
        (if (zero? sinσ)
          0
          (let [cosσ (+ (* sinU1 sinU2) (* cosU1 cosU2 cosλ))
                σ (atan2 sinσ cosσ)
                sinα (/ (* cosU1 cosU2 sinλ) sinσ)
                cosSqα (- 1 (sq sinα))
                cos2σM (zero-if-NaN (- cosσ (/ (* 2 sinU1 sinU2) cosSqα)))
                C (* (/ f 16) cosSqα (+ 4 (* f (- 4 (* 3 cosSqα)))))
                λ'' (+ L (* (- 1 C) f sinα (+ σ (* C sinσ (+ cos2σM (* C cosσ (+ -1 (* 2 cos2σM cos2σM))))))))]
            (if (and (> (abs (- λ'' λ)) 1e-12) (< (inc iterations) 200))
              (recur λ'' λ (inc iterations))
              (let [uSq (/ (* cosSqα (- (sq a) (sq b))) (sq b))
                    A (+ 1 (* (/ uSq 16384) (+ 4096 (* uSq (+ -768 (* uSq (- 320 (* 175 uSq))))))))
                    B (* (/ uSq 1024) (+ 256 (* uSq (+ -128 (* uSq (- 74 (* 47 uSq)))))))
                    Δσ (* B sinσ (+ cos2σM (* (/ B 4) (- (* cosσ (+ -1 (* 2 (sq cos2σM))))
                                                         (* (/ B 6) cos2σM (+ -3 (* 4 (sq sinσ))) (+ -3 (* 4 (sq cos2σM))))))))]
                (* b A (- σ Δσ))))))))))
