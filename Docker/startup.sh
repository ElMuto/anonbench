#!/bin/bash

git clone --verbose --branch ${GIT_BRANCH} ${GIT_URL} ${PROJECT_NAME}
cd ${PROJECT_NAME}
mv ../data .
mv ../hierarchies .

ant -DCUSTOM_TEST_CLASS="${CUSTOM_TEST_CLASS}" ${ANT_TARGET}