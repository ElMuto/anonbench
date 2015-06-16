@echo off

Rem Helmut Arbeit
Set _path=C:\dev\workspace-arx\anonbench-fork

Rem Helmut Zuhause
Rem Set _path=E:\dev\workspace\anonbench_fork

Set _cluster_node=2
Set _remote_dir=/home/imse/pc-bench-helmut

Set _commandfile=%_path%\scripts\plink-commands.txt
Set _benchmark_jar_name=pc-bench.jar
Set _benchmark_jar_path=%_path%\%_benchmark_jar_name%
Set _run_sh_path=%_path%\scripts\run.sh
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
