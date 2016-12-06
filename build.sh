#!/bin/bash

(cd spot-core-support; mvn clean install)
(cd spot-core-model-infrastructure; mvn clean install)
(cd spot-core-base-models; mvn clean install)
(cd spot-spring-web-support; mvn clean install)
(cd spot-core; mvn clean package)
(cd spot-mail; mvn clean package)
