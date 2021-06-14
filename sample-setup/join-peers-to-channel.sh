#!/bin/bash
#SPDX-License-Identifier: Apache-2.0
#Author: karthik

#Join peers to channel

set -e
export CHANNEL_NAME=providerschannel
export PEER_NAME=peer0
export ORG_NAME=hospital1.example.com
export MSPID="Hospital1MSP"
echo "Adding peers to $CHANNEL_NAME:Start"
echo "Adding $PEER_NAME.$ORG_NAME to $CHANNEL_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051
CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel join -b $CHANNEL_NAME.block
export PEER_NAME=peer1
echo "Adding $PEER_NAME.$ORG_NAME to $CHANNEL_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051
CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel join -b $CHANNEL_NAME.block
export PEER_NAME=peer0
export ORG_NAME=pharmacy1.example.com
export MSPID="Pharmacy1MSP"
echo "Adding $PEER_NAME.$ORG_NAME to $CHANNEL_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051
CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel join -b $CHANNEL_NAME.block
export PEER_NAME=peer1
echo "Adding $PEER_NAME.$ORG_NAME to $CHANNEL_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051
CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel join -b $CHANNEL_NAME.block
export PEER_NAME=peer0
export ORG_NAME=practitioner1.example.com
export MSPID="Practitioner1MSP"
echo "Adding $PEER_NAME.$ORG_NAME to $CHANNEL_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051
CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel join -b $CHANNEL_NAME.block
export PEER_NAME=peer1
echo "Adding $PEER_NAME.$ORG_NAME to $CHANNEL_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051
CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel join -b $CHANNEL_NAME.block
echo "Adding peers to $CHANNEL_NAME:Done"
