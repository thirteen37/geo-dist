(ns geo-dist.haversine-test
  (:require [clojure.test :refer :all]
            [geo-dist.haversine :refer :all]
            [geo-dist.math :refer [float=]]))

(deftest distance-test
  (testing "Different coordinates"
    (is (float= (distance 50 -5 54 -3) 465473 0.5e-3)))
  (testing "Same coordinates"
    (is (= (distance 50 -5 50 -5) 0.))))
