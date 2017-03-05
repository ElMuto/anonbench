#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

java -Xmx5G -jar jars/pc-bench.jar -D_Compare2d_PA_ATUS lr &
java -Xmx5G -jar jars/pc-bench.jar -D_Compare2d_PA_ATUS le &
java -Xmx5G -jar jars/pc-bench.jar -D_Compare2d_PA_ATUS t  &
java -Xmx5G -jar jars/pc-bench.jar -D_Compare2d_PA_ATUS d
