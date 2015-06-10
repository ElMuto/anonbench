@echo off

Rem Arbeit
Rem Set _path=C:\dev\workspace-arx\anonbench-fork

Rem Zuhause
Set _path=E:\dev\workspace\anonbench_fork




Set _messageFile=loginMessage.txt
Set _remoteDir=/home/imse/

plink PCluster-fed2 rm -f %_remoteDir%/%_messageFile%

pause