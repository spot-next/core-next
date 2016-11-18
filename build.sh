#!/bin/bash

(cd spot-core-support; mvn clean install)
(cd spot-core-model-infrastructure; mvn clean install)
(cd spot-core-model; mvn clean install)
(cd spot-core; mvn clean install)
(cd spot-mail; mvn clean install)
