#!/bin/bash

keytool -genkey -keyalg RSA -keysize 4096 -alias yarfs -keypass password -keystore yarfs.keystore -storepass password
