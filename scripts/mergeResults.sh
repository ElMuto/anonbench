#!/bin/bash

MERGE_AREA_PATH=$1
MERGED_FILENAME=$2

cd ${MERGE_AREA_PATH}

cp results1.csv ${MERGED_FILENAME}
for nodeNum in {2..4}
do
	FILENAME=results${nodeNum}.csv
	sed -i -e '1,2d' ${FILENAME}
	cat ${FILENAME} >> ${MERGED_FILENAME}
done
