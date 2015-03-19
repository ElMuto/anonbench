@echo off
Set _path=C:\dev\workspace-arx\anonbench-fork
Set _messageFile=loginMessage.txt
Set _remoteHomeDir=/home/imse
Set _heuraklesDir=heurakles-bench


pscp %_path%\scripts\%_messageFile% PCluster-fed1:%_remoteHomeDir%/%_heuraklesDir%
pscp %_path%\scripts\%_messageFile% PCluster-fed2:%_remoteHomeDir%/%_heuraklesDir%
pscp %_path%\scripts\%_messageFile% PCluster-fed3:%_remoteHomeDir%/%_heuraklesDir%
pscp %_path%\scripts\%_messageFile% PCluster-fed4:%_remoteHomeDir%/%_heuraklesDir%

pause