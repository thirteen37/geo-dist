(ns geo-dist.perf
  (:require [criterium.core :refer :all]
            [geo-dist.vincenty :as v]))

(defn- bench-vincenty []
  (with-progress-reporting (bench (v/inverse 50 -5 54 -3) :verbose))
  (with-progress-reporting (bench (v/direct 37.5 144.4 306.9 54972.271) :verbose)))

(defn -main [& args]
  (bench-vincenty))

