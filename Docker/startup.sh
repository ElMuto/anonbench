#!/bin/bash

cd ${CODE_ADCC}
git pull
ant -DCUSTOM_TEST_CLASS="${CUSTOM_TEST_CLASS}" ${ANT_TARGET}