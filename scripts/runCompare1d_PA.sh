#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

#fed3
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS ld &
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS  d

#fed4
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS lr &
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS le &
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS  t &
#java -Xmx5G -jar jars/Compare1d_PA.jar ATUS  b 

#fed2
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS ld MS &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS ld ED &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS  d

#fed1
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS lr &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS le &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS  t &
#java -Xmx5G -jar jars/Compare1d_PA.jar IHIS  b

#fed2
java -Xmx5G -jar jars/Compare1d_PA.jar IHIS  b 