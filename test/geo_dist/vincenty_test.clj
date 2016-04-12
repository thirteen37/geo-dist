(ns geo-dist.vincenty-test
  (:require [clojure.test :refer :all]
            [geo-dist.vincenty :refer :all]
            [geo-dist.math :refer [float=]]))

(deftest inverse-test
  (testing "Different coordinates"
    (is (float= (inverse 50 -5 54 -3) 465732.196)))
  (testing "Same coordinates"
    (is (= (inverse 50 -5 50 -5) 0))))
