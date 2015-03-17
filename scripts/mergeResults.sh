#!/bin/bash

MERGE_AREA_PATH=$1
MERGED_FILENAME=$2

cd ${MERGE_AREA_PATH}

cp results1.csv ${MERGED_FILENAME}
for nodeNum in {2..4}
do
	FILENAME=results${nodeNum}.csv
	grep -v 'Execution time' ${FILENAME} | grep -v 'Standard Deviation' >> ${MERGED_FILENAME}
done
