@echo off

Set _merge_host=PCluster-fed1

rem raffael
Set _path=C:\IDE\eclipse_kepler\arx\anonbench-fork

rem johanna
rem Set _path=C:\IDE\eclipse_luna\workspace\anonbench_fork

rem Helmut
rem Set _path=C:\dev\workspace-arx\anonbench-fork


Set _merged_file_name=results_merged.csv


Set _local_results_path=%_path%\cluster-results

echo getting results from FED1
pscp PCluster-fed1:/home/imse/heurakles-bench/results/results.csv %_local_results_path%\results1.csv
echo getting results from FED2
pscp PCluster-fed2:/home/imse/heurakles-bench/results/results.csv %_local_results_path%\results2.csv
echo getting results from FED3
pscp PCluster-fed3:/home/imse/heurakles-bench/results/results.csv %_local_results_path%\results3.csv
echo getting results from FED4
pscp PCluster-fed4:/home/imse/heurakles-bench/results/results.csv %_local_results_path%\results4.csv


Set _merging_area_path=/home/imse/heurakles-bench/merging-area
Set _commandfile=mergeResults.sh

cd %_local_results_path%

echo .
echo create merging-area on server
plink %_merge_host% mkdir -p %_merging_area_path%

echo .
echo copy results files and merging scripts to merging area
pscp %_local_results_path%\*.csv %_merge_host%:%_merging_area_path%
pscp %_path%\scripts\%_commandfile% %_merge_host%:%_merging_area_path%

echo .
echo chmod+x the merging script
plink %_merge_host% chmod a+x %_merging_area_path%/%_commandfile%

echo .
echo call the actual merging script
plink %_merge_host% %_merging_area_path%/%_commandfile% %_merging_area_path% %_merged_file_name%

echo .
echo get the merged result from server
pscp %_merge_host%:%_merging_area_path%/%_merged_file_name% %_local_results_path%

echo .
echo delete merging-area on server
plink %_merge_host% rm -rf %_merging_area_path%

pause