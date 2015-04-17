@echo off
Set _path=C:\IDE\eclipse_kepler\arx\anonbench-fork
Set _worklist_path=%_path%\cluster-worklists
Set _commandfile=%_path%\scripts\plink-commands.txt
Set _heurakles_jar_name=heurakles-bench.jar
Set _heurakles_jar_path=%_path%\%_heurakles_jar_name%
Set _run_sh_path=%_path%\scripts\run.sh
cd %_path%

echo This batch file also deletes ALL JAR, CSV, LOG files and run.sh!
pause

plink -m %_commandfile% PCluster-fed1 
plink -m %_commandfile% PCluster-fed2 
plink -m %_commandfile% PCluster-fed3 
plink -m %_commandfile% PCluster-fed4

pscp %_heurakles_jar_path% PCluster-fed1:/home/imse/heurakles-bench/jars/heurakles-bench.jar
pscp %_heurakles_jar_path% PCluster-fed2:/home/imse/heurakles-bench/jars/heurakles-bench.jar
pscp %_heurakles_jar_path% PCluster-fed3:/home/imse/heurakles-bench/jars/heurakles-bench.jar
pscp %_heurakles_jar_path% PCluster-fed4:/home/imse/heurakles-bench/jars/heurakles-bench.jar

pscp -r %_worklist_path%\worklist-1.csv PCluster-fed1:/home/imse/heurakles-bench/worklist.csv
pscp -r %_worklist_path%\worklist-2.csv PCluster-fed2:/home/imse/heurakles-bench/worklist.csv
pscp -r %_worklist_path%\worklist-3.csv PCluster-fed3:/home/imse/heurakles-bench/worklist.csv
pscp -r %_worklist_path%\worklist-4.csv PCluster-fed4:/home/imse/heurakles-bench/worklist.csv

pscp %_run_sh_path% PCluster-fed1:/home/imse/heurakles-bench/
pscp %_run_sh_path% PCluster-fed2:/home/imse/heurakles-bench/
pscp %_run_sh_path% PCluster-fed3:/home/imse/heurakles-bench/
pscp %_run_sh_path% PCluster-fed4:/home/imse/heurakles-bench/
plink PCluster-fed1 chmod a+x /home/imse/heurakles-bench/run.sh
plink PCluster-fed2 chmod a+x /home/imse/heurakles-bench/run.sh
plink PCluster-fed3 chmod a+x /home/imse/heurakles-bench/run.sh
plink PCluster-fed4 chmod a+x /home/imse/heurakles-bench/run.sh

plink PCluster-fed1 screen -dmS "A" bash -c 'cd heurakles-bench;./run.sh; read'
plink PCluster-fed2 screen -dmS "B" bash -c 'cd heurakles-bench;./run.sh; read'
plink PCluster-fed3 screen -dmS "C" bash -c 'cd heurakles-bench;./run.sh; read'
plink PCluster-fed4 screen -dmS "D" bash -c 'cd heurakles-bench;./run.sh; read'

start putty -load PCluster-fed1
start putty -load PCluster-fed2
start putty -load PCluster-fed3
start putty -load PCluster-fed4

pause