#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

#fed1
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 ld &
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13  d

#fed2
java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 lr &
java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 le &
java -Xmx5G -jar jars/Compare1d_PA.jar ACS13  t 

#fed2
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS ld &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS  d

#fed1
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS lr &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS le &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS  t 