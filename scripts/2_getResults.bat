@echo off

Set _path=%WORKSPACE_PATH%\anonbench_fork

Set _cluster_node=3

Set _local_results_path=%_path%\results
pscp PCluster-fed%_cluster_node%:/home/imse/pc-bench-helmut/results/results.csv %_local_results_path%\results_node%_cluster_node%.csv

pause