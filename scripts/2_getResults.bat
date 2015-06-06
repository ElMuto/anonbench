@echo off

Rem Helmut Arbeit
Rem Set _path=C:\dev\workspace-arx\anonbench-fork

Rem Helmut Zuhause
Set _path=E:\dev\workspace\anonbench_fork

Set _merge_host=PCluster-fed3
Set _cluster_node=3


Set _local_results_path=%_path%\results
pscp PCluster-fed%_cluster_node%:/home/imse/pc-bench-helmut/results/results.csv %_local_results_path%\results_node%_cluster_node%.csv

pause