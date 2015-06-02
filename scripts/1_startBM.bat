@echo off

Set _path=C:\dev\workspace-arx\anonbench-fork

Set _commandfile=%_path%\scripts\plink-commands.txt
Set _benchmark_jar_name=pc-bench.jar
Set _benchmark_jar_path=%_path%\%_benchmark_jar_name%
Set _run_sh_path=%_path%\scripts\run.sh
Set _cluster_node=3
Set _remote_dir=/home/imse/pc-bench-helmut
cd %_path%

echo This batch file also deletes ALL JAR, CSV, LOG files and run.sh!
pause

plink -m %_commandfile% PCluster-fed%_cluster_node%

pscp %_benchmark_jar_path% PCluster-fed%_cluster_node%:%_remote_dir%/jars/%_benchmark_jar_name%

pscp %_run_sh_path% PCluster-fed%_cluster_node%:%_remote_dir%/
plink PCluster-fed%_cluster_node% chmod a+x %_remote_dir%/run.sh

plink PCluster-fed%_cluster_node% screen -dmS "C" bash -c 'cd %_remote_dir%;./run.sh; read'

start putty -load PCluster-fed%_cluster_node%

pause