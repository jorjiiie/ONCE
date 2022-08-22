[![Java CI](https://github.com/jorjiiie/ONCE/workflows/Tests/badge.svg)](https://github.com/jorjiiie/ONCE/actions/workflows/ci.yml)
# ONCE

ONCE, the coolest cryptocurrency

# Features (its not a bug its a feature 😎)

* Implemented in Java
* Uses SHA-256 for hashing
* Static hash targets so there is no fixed time-target (vulnerable to attacks probably)
* Does not use input/output tx on each tx, so there is literally zero ability to do any 'smart contracting' (as of yet uwu) or track transactions and so every single address with be databased with their own balance instead of having tx associated with each address
* Requires a port forward but I'm pretty sure that's standard
* Uses RSA for signatures and addresses, so the keys are only like 500 bytes each (dub)


# Actual features

* It's got some pretty sick networking (I implemented it but its likely garbage)
* I implemented the RSA by hand (a security vulnerability tbh)


# TODO list (I will get around to these eventually!)

* custom serialization protocol for disk read/writes and network communications (which will use the same serialization format)
* better logging with option to silence logging for once (L)
* delete this project

# FAQ (what is this project actualy for)

* this project is obviously useless but its an excuse for me to do whatever I want that seems interesting, so far I've implemented a p2p networking "module", a cache system that holds n objects with O(n) memory and O(logn) queries/adds (avl tree lol) and has priorities that get updated (priority = # of queries while active), a generic data manager that is a essentially a disk based map with said cache, and in the future i will roll this out to a generalized serialization so its a better disk-based map (the issue is that each entry gets its own file and no collisions are allowed), hand implemented RSA (tbh not that hard but still cool), a very very bad cryptocurrency that probably doesnt even qualify as a cryptocurrency (literally just a public ledger with SHA-256)