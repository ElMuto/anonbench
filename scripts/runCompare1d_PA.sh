#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

#fed1
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS ld MS & // durchgelaufen
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS ld ED & // durchgelaufen
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS lr MS   // durchgelaufen

#fed2
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS lr ED & // killed - restarted on fed4
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS le MS & // durchgelaufen
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS le ED   // sicherheitshalber noch mal neu gestartet auf fed1

#fed3
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS d &      // durchgelaufen
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS t &      // durchgelaufen
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS b        // durchgelaufen

#fed4
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS lr ED

#fed1
java -Xmx5G -jar jars/Compare1d_PA.jar IHIS le ED