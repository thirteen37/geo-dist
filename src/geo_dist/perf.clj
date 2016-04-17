(ns geo-dist.perf
  (:require [criterium.core :refer :all]
            [geo-dist.vincenty :as v]
            [geo-dist.haversine :as h]))

(defn- bench-vincenty []
  (with-progress-reporting (bench (v/inverse 50 -5 54 -3) :verbose))
  (with-progress-reporting (bench (v/direct 37.5 144.4 306.9 54972.271) :verbose)))

(defn- bench-haversine []
  (with-progress-reporting (bench (h/distance 50 -5 54 -3) :verbose)))

(defn -main [& args]
  (bench-haversine)
  (bench-vincenty))

