@echo off

Set NODE_NUM=2

Set _path=%LOCAL_DEV_PATH%
Set _messageFile=loginMessage.txt
Set _remoteDir=/home/imse/

pscp %_path%\scripts\%_messageFile% PCluster-fed%NODE_NUM%:%_remoteDir%/

pause