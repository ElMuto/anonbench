#!/bin/bash

#deactivate speedstep
for f in `ls /sys/devices/system/cpu/ | grep cpu[0-9]`; do
test -e /sys/devices/system/cpu/$f/cpufreq/scaling_governor && echo performance | sudo tee /sys/devices/system/cpu/$f/cpufreq/scaling_governor
done

#run benchmark
#Usage: java -Xmx5G -jar <jar> mode numRepetitions [configurationFile]
#Usage of mode:");
#1 - execute algorithms/compute ILbounds/compute relative IL
#2 - execute algorithms/compute ILbounds
#3 - execute algorithms/compute relative IL
#4 - compute ILbounds
#5 - compute relative IL
#6 - create configuration file based on code

java -Xmx5G -jar jars/heurakles-bench.jar 2 1 worklist.csv
