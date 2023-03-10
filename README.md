[![Java CI](https://github.com/jorjiiie/ONCE/workflows/Tests/badge.svg)](https://github.com/jorjiiie/ONCE/actions/workflows/ci.yml)
# ONCE

ONCE, the coolest cryptocurrency

ONCE is a Java-based cryptocurrency that utilizes SHA-256 for hashing and RSA for signatures and addresses. It's not serious or working half the time, but it's certainly an exciting project that includes the core workings of a cryptocurrency.

# Features

* P2P networking module
* 2048 bit RSA for wallets and transactions
* Uses SHA-256 for hashing
* Multicore mining for more coins


# Limitations

* Static mining difficulty, making the network vulnerable
* No scripting language (which most modern blockchains have), and relies on very rudimentary transactions
* Coins are stored in a wallet, rather than containing transactions, so switching branches may take longer
* Currently limited to a single network

# Installation

1. Clone repository
2. Build using `mvn package`
3. Run the generated jar on the command line (in target)


# Usage
Once the jar is running, use the CLI to interact with the network

