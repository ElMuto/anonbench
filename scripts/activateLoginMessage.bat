@echo off

Rem Arbeit
Set _path=C:\dev\workspace-arx\anonbench-fork

Rem Zuhause
Rem Set _path=E:\dev\workspace\anonbench_fork


Set _messageFile=loginMessage.txt
Set _remoteDir=/home/imse/

pscp %_path%\scripts\%_messageFile% PCluster-fed2:%_remoteDir%/

pause