@echo off

Set NODE_NUM=3

Set _path=%WORKSPACE_PATH%\anonbench-fork
Set _messageFile=loginMessage.txt
Set _remoteDir=/home/imse/

pscp %_path%\scripts\%_messageFile% PCluster-fed%NODE_NUM%:%_remoteDir%/

pause