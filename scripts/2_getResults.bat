@echo off

Set _path=%WORKSPACE_PATH%\promotion\code-attr-disclosure-criteria-comparison

Set _cluster_node=1

Set _local_results_path=%_path%\results
pscp PCluster-fed%_cluster_node%:/home/imse/pc-bench-helmut/results/* %_local_results_path%\

pause