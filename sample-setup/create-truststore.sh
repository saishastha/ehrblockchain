#!/bin/bash
#SPDX-License-Identifier: Apache-2.0
#Author: karthik

#Creates TrustStore to be used in Java application

echo "Create TrustStore:Start"
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/hospital1.example.com/peers/orderer0.hospital1.example.com/tls/server.crt -alias orderer0.hospital1.example.com -keystore certs.jks
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/hospital1.example.com/peers/peer0.hospital1.example.com/tls/server.crt -alias peer0.hospital1.example.com -keystore certs.jks
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/hospital1.example.com/peers/peer1.hospital1.example.com/tls/server.crt -alias peer1.hospital1.example.com -keystore certs.jks
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/pharmacy1.example.com/peers/orderer0.pharmacy1.example.com/tls/server.crt -alias orderer0.pharmacy1.example.com -keystore certs.jks
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/pharmacy1.example.com/peers/peer0.pharmacy1.example.com/tls/server.crt -alias peer0.pharmacy1.example.com -keystore certs.jks
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/pharmacy1.example.com/peers/peer1.pharmacy1.example.com/tls/server.crt -alias peer1.pharmacy1.example.com -keystore certs.jks
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/practitioner1.example.com/peers/orderer0.practitioner1.example.com/tls/server.crt -alias orderer0.practitioner1.example.com -keystore certs.jks
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/practitioner1.example.com/peers/peer0.practitioner1.example.com/tls/server.crt -alias peer0.practitioner1.example.com -keystore certs.jks
yes | keytool -storepass changeit -import -file crypto-config/peerOrganizations/practitioner1.example.com/peers/peer1.practitioner1.example.com/tls/server.crt -alias peer1.practitioner1.example.com -keystore certs.jks
echo "Create TrustStore:Done"
