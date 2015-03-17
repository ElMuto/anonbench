@echo off
Set _path=C:\dev\workspace-arx\anonbench-fork\cluster-results
mkdir %_path%
cd %_path%

echo FED1
pscp PCluster-fed1:/home/imse/heurakles-bench/results/results.csv %_path%\results1.csv
echo FED2
pscp PCluster-fed2:/home/imse/heurakles-bench/results/results.csv %_path%\results2.csv
echo FED3
pscp PCluster-fed3:/home/imse/heurakles-bench/results/results.csv %_path%\results3.csv
echo FED4
pscp PCluster-fed4:/home/imse/heurakles-bench/results/results.csv %_path%\results4.csv

pause