#!/bin/bash

MERGED_FILENAME=results_merged.csv

cp results1.csv ${MERGED_FILENAME}
for nodeNum in {2..4}
do
	FILENAME=results${nodeNum}.csv
	grep -v 'Execution time' ${FILENAME} | grep -v 'Standard Deviation' >> ${MERGED_FILENAME}
done
