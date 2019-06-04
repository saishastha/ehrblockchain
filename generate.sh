#!/bin/bash
#SPDX-License-Identifier: Apache-2.0
#Author: kehm

#Generate required cryptographic material and channel artifacts

set -e
echo "Running Cryptogen:Start"
../bin/cryptogen generate --config=./crypto-config.yaml #create cryptographic material
echo "Running Cryptogen:Done"
echo "Creating TrustStore:Start"
./sample-setup/create-truststore.sh &> /dev/null
echo "Creating TrustStore:Done"
export FABRIC_CFG_PATH=$PWD #setting Fabric configuration path
mkdir channel-artifacts
echo "Running Configtxgen:Start"
#create channel artifacts
../bin/configtxgen -profile InitialOrgsOrdererGenesis -outputBlock ./channel-artifacts/genesis.block -channelID ehrnetwork-sys-channel
export CHANNEL_NAME=providerschannel
../bin/configtxgen -profile InitialOrgsChannel -outputCreateChannelTx ./channel-artifacts/providerschannel.tx -channelID $CHANNEL_NAME
../bin/configtxgen -profile InitialOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Hospital1MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Hospital1MSP
../bin/configtxgen -profile InitialOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Pharmacy1MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Pharmacy1MSP
../bin/configtxgen -profile InitialOrgsChannel -outputAnchorPeersUpdate ./channel-artifacts/Practitioner1MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Practitioner1MSP
echo "Running Configtxgen:Done"
