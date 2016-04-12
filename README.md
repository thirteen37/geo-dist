# geo-dist

Geodesic distance functions for Clojure

## Usage

[Vincenty approximations](https://en.wikipedia.org/wiki/Vincenty%27s_formulae)
using the WGS84 ellipsoid are in `geo-dist.vincenty`. There is
currently an implementation of
[Vincenty's inverse problem](https://en.wikipedia.org/wiki/Vincenty%27s_formulae#Inverse_problem)
as `inverse` and
[the direct problem](https://en.wikipedia.org/wiki/Vincenty%27s_formulae#Direct_Problem)
as `direct`.

The Vincenty algorithm was adapted from
[this Javascript implementation](http://www.movable-type.co.uk/scripts/latlong-vincenty.html)
by Chris Veness.

## License

Copyright © 2016 Yu-Xi Lim

Distributed under the Apache License either version 2.0 or (at your
option) any later version.
