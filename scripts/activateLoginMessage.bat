@echo off
Set _path=C:\dev\workspace-arx\anonbench-fork
Set _messageFile=loginMessage.txt
Set _remoteHomeDir=/home/imse
Set _heuraklesDir=heurakles-bench


pscp %_path%\scripts\%_messageFile% PCluster-fed1:%_remoteHomeDir%/%_heuraklesDir%
pscp %_path%\scripts\%_messageFile% PCluster-fed2:%_remoteHomeDir%/%_heuraklesDir%
pscp %_path%\scripts\%_messageFile% PCluster-fed3:%_remoteHomeDir%/%_heuraklesDir%
pscp %_path%\scripts\%_messageFile% PCluster-fed4:%_remoteHomeDir%/%_heuraklesDir%

plink PCluster-fed1 sed -i -e 's/^^#cat %_heuraklesDir%\/%_messageFile%/cat %_heuraklesDir%\/%_messageFile%/' %_remoteHomeDir%/.profile
plink PCluster-fed2 sed -i -e 's/^^#cat %_heuraklesDir%\/%_messageFile%/cat %_heuraklesDir%\/%_messageFile%/' %_remoteHomeDir%/.profile
plink PCluster-fed3 sed -i -e 's/^^#cat %_heuraklesDir%\/%_messageFile%/cat %_heuraklesDir%\/%_messageFile%/' %_remoteHomeDir%/.profile
plink PCluster-fed4 sed -i -e 's/^^#cat %_heuraklesDir%\/%_messageFile%/cat %_heuraklesDir%\/%_messageFile%/' %_remoteHomeDir%/.profile

pause