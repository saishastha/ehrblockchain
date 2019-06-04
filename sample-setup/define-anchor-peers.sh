#!/bin/bash
#SPDX-License-Identifier: Apache-2.0
#Author: kehm

#Defines anchor peers for each organization

set -e
export CHANNEL_NAME=providerschannel
export ORDERER_NAME=orderer0.hospital1.example.com
export ORG_NAME=hospital1.example.com
export PEER_NAME=peer0
export MSPID="Hospital1MSP"
export PANCHORS=Hospital1MSPanchors
echo "Setting anchor peer for $ORG_NAME:$PEER_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051 CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel update -o $ORDERER_NAME:7050 -c $CHANNEL_NAME -f ./channel-artifacts/$PANCHORS.tx --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/hospital1.example.com/peers/$ORDERER_NAME/msp/tlscacerts/tlsca.hospital1.example.com-cert.pem
export ORG_NAME=pharmacy1.example.com
export PEER_NAME=peer0
export MSPID="Pharmacy1MSP"
export PANCHORS=Pharmacy1MSPanchors
echo "Setting anchor peer for $ORG_NAME:$PEER_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051 CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel update -o $ORDERER_NAME:7050 -c $CHANNEL_NAME -f ./channel-artifacts/$PANCHORS.tx --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/hospital1.example.com/peers/$ORDERER_NAME/msp/tlscacerts/tlsca.hospital1.example.com-cert.pem
export ORG_NAME=practitioner1.example.com
export PEER_NAME=peer0
export MSPID="Practitioner1MSP"
export PANCHORS=Practitioner1MSPanchors
echo "Setting anchor peer for $ORG_NAME:$PEER_NAME"
CORE_PEER_MSPCONFIGPATH=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/users/Admin@$ORG_NAME/msp
CORE_PEER_ADDRESS=$PEER_NAME.$ORG_NAME:7051 CORE_PEER_LOCALMSPID=$MSPID
CORE_PEER_TLS_ROOTCERT_FILE=/opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/$ORG_NAME/peers/$PEER_NAME.$ORG_NAME/tls/ca.crt
peer channel update -o $ORDERER_NAME:7050 -c $CHANNEL_NAME -f ./channel-artifacts/$PANCHORS.tx --tls --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/peerOrganizations/hospital1.example.com/peers/$ORDERER_NAME/msp/tlscacerts/tlsca.hospital1.example.com-cert.pem
