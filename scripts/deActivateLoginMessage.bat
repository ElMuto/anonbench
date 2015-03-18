@echo off
Set _path=C:\dev\workspace-arx\anonbench-fork
Set _messageFile=loginMessage.txt
Set _remoteHomeDir=/home/imse
Set _heuraklesDir=heurakles-bench

plink PCluster-fed1 sed -i -e 's/^^cat %_heuraklesDir%\/%_messageFile%/#cat heurakles-bench\/%_messageFile%/' /home/imse/.profile; rm -f %_remoteHomeDir%/%_heuraklesDir%/%_messageFile%
plink PCluster-fed2 sed -i -e 's/^^cat %_heuraklesDir%\/%_messageFile%/#cat %_heuraklesDir%\/%_messageFile%/' /home/imse/.profile; rm -f %_remoteHomeDir%/%_heuraklesDir%/%_messageFile%
plink PCluster-fed3 sed -i -e 's/^^cat %_heuraklesDir%\/%_messageFile%/#cat %_heuraklesDir%\/%_messageFile%/' /home/imse/.profile; rm -f %_remoteHomeDir%/%_heuraklesDir%/%_messageFile%
plink PCluster-fed4 sed -i -e 's/^^cat %_heuraklesDir%\/%_messageFile%/#cat %_heuraklesDir%\/%_messageFile%/' /home/imse/.profile; rm -f %_remoteHomeDir%/%_heuraklesDir%/%_messageFile%

pause