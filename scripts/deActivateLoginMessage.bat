@echo off
Set _path=C:\IDE\eclipse_kepler\arx\anonbench-fork
Set _messageFile=loginMessage.txt
Set _remoteDir=/home/imse/

plink PCluster-fed3 rm -f %_remoteDir%/%_messageFile%

pause