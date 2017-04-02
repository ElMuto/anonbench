#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

#fed3
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS ld MS reverse

#fed1
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS ld both &
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS lr both

#fed2
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS d both &
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS t both &
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS le both

#fed1
java -Xmx5G -jar jars/Compare1d_PA.jar ATUS ld ED reverse