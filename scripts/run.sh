#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar>

java -Xmx5G -DHelmut -jar jars/pc-bench.jar ACS13 ld &
java -Xmx5G -DHelmut -jar jars/pc-bench.jar ACS13 lr &
java -Xmx5G -DHelmut -jar jars/pc-bench.jar ACS13 le 
#java -Xmx5G -DHelmut -jar jars/pc-bench.jar ACS13 t  
#java -Xmx5G -DHelmut -jar jars/pc-bench.jar ACS13 d