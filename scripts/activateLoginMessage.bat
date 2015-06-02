@echo off
Set _path=C:\dev\workspace-arx\anonbench-fork
Set _messageFile=loginMessage.txt
Set _remoteDir=/home/imse/

pscp %_path%\scripts\%_messageFile% PCluster-fed3:%_remoteDir%/

pause