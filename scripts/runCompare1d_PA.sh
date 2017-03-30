#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

#fed1
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 ld both &
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 lr both

#fed2
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 le both &
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 t  both &
#java -Xmx5G -jar jars/Compare1d_PA.jar ACS13 d  both

#fed3
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS ld both &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS lr both

#fed4
java -Xmx5G -jar jars/Compare1d_PA.jar IHIS le both &
java -Xmx5G -jar jars/Compare1d_PA.jar IHIS t  both &
java -Xmx5G -jar jars/Compare1d_PA.jar IHIS d  both