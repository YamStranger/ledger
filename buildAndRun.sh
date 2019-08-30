#!/usr/bin/env bash

./gradlew clean build shadowJar
java -jar build/libs/reut-ledger-1.0.0-all.jar
