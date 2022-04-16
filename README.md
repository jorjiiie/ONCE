[![Java CI](https://github.com/jorjiiie/ONCE/actions/workflows/ci.yml/badge.svg)](https://github.com/jorjiiie/ONCE/actions/workflows/ci.yml)
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
