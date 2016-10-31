#!/bin/bash

(cd spot-core-support; mvn install)
(cd spot-core-model-infrastructure; mvn install)
(cd spot-core-model; mvn install)
(cd spot-core; mvn install)
(cd spot-mail; mvn install)
