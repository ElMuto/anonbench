#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

#fed2
#java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ACS13 ld &
#java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ACS13 d

#fed2
#java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ATUS le &
#java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ATUS t  &
#java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ATUS d

#fed3
#java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ATUS ld

#fed1
#java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar IHIS ld ED

#fed3
#java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ATUS ld MS

#fed1
java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ATUS le MS &
java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ATUS t  MS &
java -Xmx5G -DHelmut -jar jars/Compare1d_PA.jar ATUS d  MS