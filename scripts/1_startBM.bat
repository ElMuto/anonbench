	@echo off

Set _path=%WORKSPACE_PATH%\promotion\code-attr-disclosure-criteria-comparison

Set _cluster_node=4
Set _remote_dir=/home/imse/pc-bench-helmut

Set _commandfile=%_path%\scripts\plink-commands.txt
Set _benchmark_jar_name=pc-bench.jar
Set _benchmark_jar_path=%_path%\%_benchmark_jar_name%
Set _run_sh_path=%_path%\scripts\run.sh
cd %_path%

echo This batch file also deletes ALL JAR, CSV, LOG files and run.sh!
pause

plink -m %_commandfile% PCluster-fed%_cluster_node%

echo Local jar: 	%_benchmark_jar_path%
echo Local run.sh:	%_run_sh_path%
echo pc-bench.jar:  %_benchmark_jar_path%

pscp %_benchmark_jar_path% imse@PCluster-fed%_cluster_node%:%_remote_dir%/jars/%_benchmark_jar_name%
pscp %_run_sh_path% imse@PCluster-fed%_cluster_node%:%_remote_dir%/

plink imse@PCluster-fed%_cluster_node% chmod a+x %_remote_dir%/run.sh

plink imse@PCluster-fed%_cluster_node% screen -dmS "C" bash -c 'cd %_remote_dir%;./run.sh; read'

start putty -load PCluster-fed%_cluster_node%

pause
