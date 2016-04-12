(ns geo-dist.vincenty-test
  (:require [clojure.test :refer :all]
            [geo-dist.vincenty :refer :all]
            [geo-dist.math :refer [float=]]))

(deftest inverse-test
  (testing "Different coordinates"
    (is (float= (inverse 50 -5 54 -3) 465732.196)))
  (testing "Same coordinates"
    (is (= (inverse 50 -5 50 -5) 0.))))

(deftest direct-test
  (testing "Somewhere far"
    (is (and (float= (first (direct 37.5 144.4 306.9 54972.271)) 37.79633)
             (float= (second (direct 37.5 144.4 306.9 54972.271)) 143.9009)))))
