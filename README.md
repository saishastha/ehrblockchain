# HLF Network Package
## Hyperledger Fabric Blockchain Network for EHR Systems

Related project: <https://github.com/karthik/ehrblockchainapplication>

This software package is related to the following article and thesis:
- (1) Yang et al., "A blockchain-based architecture for securing electronic health record systems", 2019, Concurrencry and Computation: Practice and Experience, <https://onlinelibrary.wiley.com/doi/10.1002/cpe.5479>
- (2) K. E. Marstein, "Improve auditing and privacy of electronic health records by using blockchain technology", 2019, Master's thesis, <http://bora.uib.no/handle/1956/20519>

Read (1) for a presentation of the framework embedded in the software.
Read (2) for information on the features incorporated in the software and how the software is configured.

Source code for Java chaincode is located in /chaincode.
Configuration files (.yaml) for the network are located in the root directory.
The scripts in /sample-setup contain additional scripts used to bring up the network.

To bring up the network, run the scripts (.sh) located in the root directory:

1. Run "./generate.sh" to generate cryptographic material and channel artifacts.
2. Run "./start.sh [seconds]" to start docker containers.

Run "./clean.sh" to tear down the network. This will remove the material generated in step 1 and all docker containers.
NOTE: This will remove ALL docker containers in your system!

TODO: Encrypt LogEntry objects and implement collective authority for key distribution.

The project is available under the Apache License, Version 2.0 (Apache-2.0).
