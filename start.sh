#!/bin/bash
#SPDX-License-Identifier: Apache-2.0
#Author: karthik

#Start the required docker containers, create channel and instantiate chaincode
#Create required cryptographic material and channel configuration (generate.sh) before running this script

set -e
export COMPOSE_PROJECT_NAME=ehrnetwork
#set variables used in compose-base.yaml
export HOSPITAL1_CA_KEYFILE=`find crypto-config/peerOrganizations/hospital1.example.com/ca/*_sk -printf "%f\n"`
export PHARMACY1_CA_KEYFILE=`find crypto-config/peerOrganizations/pharmacy1.example.com/ca/*_sk -printf "%f\n"`
export PRACTITIONER1_CA_KEYFILE=`find crypto-config/peerOrganizations/practitioner1.example.com/ca/*_sk -printf "%f\n"`
echo "Creating docker containers:Start"
docker-compose -f compose-with-raft.yaml -f compose-with-couchdb.yaml up -d #create docker containers
echo "Creating docker containers:Done"
echo "Waiting $1s for orderers to get ready"
sleep 1s #wait for docker containers to finish startup
echo "Send create channel request:Start"
#docker exec -i cli0 bash < sample-setup/create-channel-request.sh &> /dev/null #create channel
echo "Send create channel request:Done"
echo "Adding peers to channel:Start"
#docker exec -i cli0 bash < sample-setup/join-peers-to-channel.sh &> /dev/null #add peers to channel
echo "Adding peers to channel:Done"
echo "Define anchor peers:Start"
docker exec -i cli0 bash < sample-setup/define-anchor-peers.sh &> /dev/null #define anchor peers
echo "Define anchor peers:Done"
echo "Install chaincode:Start"
docker exec -i cli0 bash < sample-setup/instantiate-chaincode.sh &> /dev/null #instantiate chaincode 
echo "Install chaincode:Done"
echo "Add affiliations:Start"
docker exec -i ca.hospital1.example.com bash < sample-setup/create-affiliations.sh &> /dev/null #create affiliations
docker exec -i ca.pharmacy1.example.com bash < sample-setup/create-affiliations.sh &> /dev/null #create affiliations
docker exec -i ca.practitioner1.example.com bash < sample-setup/create-affiliations.sh &> /dev/null #create affiliations
echo "Add affiliations:Done"
