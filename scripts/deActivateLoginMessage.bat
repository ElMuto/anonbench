@echo off
Set _path=C:\dev\workspace-arx\anonbench-fork
Set _messageFile=loginMessage.txt
Set _remoteHomeDir=/home/imse
Set _heuraklesDir=heurakles-bench

plink PCluster-fed1 rm -f %_remoteHomeDir%/%_heuraklesDir%/%_messageFile%
plink PCluster-fed2 rm -f %_remoteHomeDir%/%_heuraklesDir%/%_messageFile%
plink PCluster-fed3 rm -f %_remoteHomeDir%/%_heuraklesDir%/%_messageFile%
plink PCluster-fed4 rm -f %_remoteHomeDir%/%_heuraklesDir%/%_messageFile%

pause