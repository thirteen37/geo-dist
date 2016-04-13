(ns geo-dist.vincenty
  "Vincenty approximations using WGS84

  All angles in degrees and distances in meters."
  (:require [geo-dist.math :refer :all]))

(def ^:private a 6378137.)
(def ^:private b 6356752.314245)
(def ^:private f (/ (- a b) a))

(defn inverse
  "Returns the distance between (lat1, lon1) (lat2, lon2)"
  [lat1 lon1 lat2 lon2]
  (let [ϕ1 (deg->rad lat1)
        ϕ2 (deg->rad lat2)
        λ1 (deg->rad lon1)
        λ2 (deg->rad lon2)
        L (- λ2 λ1)
        tanU1 (* (- (double 1.) f) (tan ϕ1))
        cosU1 (/ (double 1.) (sqrt (+ (double 1.) (sq tanU1))))
        sinU1 (* tanU1 cosU1)
        tanU2 (* (- (double 1.) f) (tan ϕ2))
        cosU2 (/ (double 1.) (sqrt (+ (double 1.) (sq tanU2))))
        sinU2 (* tanU2 cosU2)]
    (loop [λ L
           λ' (double 0.)
           iterations (int 0)]
      (let [sinλ (sin λ)
            cosλ (cos λ)
            sinSqσ (+ (sq (* cosU2 sinλ))
                      (sq (- (* cosU1 sinU2) (* sinU1 cosU2 cosλ))))
            sinσ (sqrt sinSqσ)]
        (if (zero? sinσ)
          (double 0.)
          (let [cosσ (+ (* sinU1 sinU2) (* cosU1 cosU2 cosλ))
                σ (atan2 sinσ cosσ)
                sinα (/ (* cosU1 cosU2 sinλ) sinσ)
                cosSqα (- (double 1.) (sq sinα))
                cos2σM (zero-if-NaN (- cosσ (/ (* (double 2.) sinU1 sinU2) cosSqα)))
                C (* (/ f (double 16.)) cosSqα (+ (double 4.) (* f (- (double 4.) (* (double 3.) cosSqα)))))
                λ'' (+ L (* (- (double 1.) C) f sinα (+ σ (* C sinσ (+ cos2σM (* C cosσ (+ (double -1.) (* (double 2.) cos2σM cos2σM))))))))]
            (if (and (> (abs (- λ'' λ)) 1e-12) (< (inc iterations) (int 200)))
              (recur λ'' λ (inc iterations))
              (let [uSq (/ (* cosSqα (- (sq a) (sq b))) (sq b))
                    A (+ (double 1.) (* (/ uSq (double 16384.)) (+ (double 4096.) (* uSq (+ (double -768.) (* uSq (- (double 320.) (* (double 175.) uSq))))))))
                    B (* (/ uSq (double 1024.)) (+ (double 256.) (* uSq (+ (double -128.) (* uSq (- (double 74.) (* (double 47.) uSq)))))))
                    Δσ (* B sinσ
                          (+ cos2σM (* (/ B (double 4.))
                                       (- (* cosσ (+ (double -1.) (* (double 2.) (sq cos2σM))))
                                          (* (/ B (double 6.)) cos2σM (+ (double -3.) (* (double 4.) (sq sinσ))) (+ (double -3.) (* (double 4.) (sq cos2σM))))))))]
                (* b A (- σ Δσ))))))))))

(defn direct
  "Returns the (lat, lon) that is distance away from (lat, lon) in the
  direction of bearing"
  [lat lon bearing distance]
  (let [ϕ1 (deg->rad lat)
        λ1 (deg->rad lon)
        α1 (deg->rad bearing)
        s (double distance)
        sinα1 (sin α1)
        cosα1 (cos α1)
        tanU1 (* (- (double 1.) f) (tan ϕ1))
        cosU1 (/ (double 1.) (sqrt (+ (double 1.) (sq tanU1))))
        sinU1 (* tanU1 cosU1)
        σ1 (atan2 tanU1 cosα1)
        sinα (* cosU1 sinα1)
        cosSqα (- (double 1.) (sq sinα))
        uSq (/ (* cosSqα (- (sq a) (sq b))) (sq b))
        A (+ (double 1.) (* (/ uSq 16384) (+ (double 4096.) (* uSq (+ (double -768.) (* uSq (- (double 320.) (* (double 175.) uSq))))))))
        B (* (/ uSq 1024) (+ (double 256.) (* uSq (+ (double -128.) (* uSq (- (double 74.) (* (double 47.) uSq)))))))]
    (loop [σ (/ s (* b A))
           σ' (double 0.)
           iterations (int 0)]
      (let [cos2σM (cos (+ (* (double 2.) σ1) σ))
            sinσ (sin σ)
            cosσ (cos σ)
            Δσ (* B sinσ
                  (+ cos2σM (/ B (* (double 4.) (- (* cosσ (+ (double -1.) (* (double 2.) (sq cos2σM))))
                                         (* (/ B (double 6.)) cos2σM (+ (double -3.) (* (double 4.) (sq sinσ))) (+ -3 (* (double 4.) (sq cos2σM)))))))))
            σ'' (+ (/ s (* b A)) Δσ)]
        (if (and (> (abs (- σ σ'')) 1e-12) (< (inc iterations) (int 200)))
          (recur σ'' σ (inc iterations))
          (let [x (- (* sinU1 sinσ) (* cosU1 cosσ cosα1))
                φ2 (atan2 (+ (* sinU1 cosσ) (* cosU1 sinσ cosα1)) (* (- (double 1.) f) (sqrt (+ (sq sinα) (sq x)))))
                λ (atan2 (* sinσ sinα1) (- (* cosU1 cosσ) (* sinU1 sinσ cosα1)))
                C (* (/ f (double 16.)) cosSqα (+ (double 4.) (* f (- (double 4.) (* (double 3.) cosSqα)))))
                L (- λ (* (- (double 1.) C) f sinα (+ σ (* C sinσ (+ cos2σM (* C cosσ (+ (double -1.) (* (double 2.) (sq cos2σM)))))))))
                λ2 (- (mod (+ λ1 L (* (double 3.) pi)) (* (double 2.) pi)) pi)]
            [(rad->deg φ2) (rad->deg λ2)]))))))
