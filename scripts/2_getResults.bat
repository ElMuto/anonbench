@echo off

Set _path=%WORKSPACE_PATH%\pcc-compare\anonbench-fork

Set _cluster_node=4

Set _local_results_path=%_path%\results
pscp PCluster-fed%_cluster_node%:/home/imse/pc-bench-helmut/results/* %_local_results_path%\

pause