@echo off

Set NODE_NUM=2

Set _path=%WORKSPACE_PATH%\anonbench_fork
Set _messageFile=loginMessage.txt
Set _remoteDir=/home/imse/

plink PCluster-fed%NODE_NUM% rm -f %_remoteDir%/%_messageFile%

pause