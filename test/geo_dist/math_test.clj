(ns geo-dist.math-test
  (:require [geo-dist.math :refer :all]
            [clojure.test :refer :all]))

(deftest float=-test
  (testing "float equality"
    (is (float= 1. 1.000001))
    (is (not (float= 1. 1.00001)))))
