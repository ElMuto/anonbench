#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

#fed1
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 lr ED &
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 le ED

#fed2
java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 ld ED &
java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 d  ED &
java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 t  ED