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

#Examples:

#execute algos, compute ILbounds, compute relative IL, use 2 repetitions, use configuration file
#java -Xmx5G -jar heurakles-bench.jar 1 2 "results/benchmarkConfiguration.csv

#compute ILbounds, use 2 repetitions (which will be ignored for ILbounds), use configuration from code (omit configurationFile)
#java -Xmx5G -jar heurakles-bench.jar 1 2 

#compute relative information loss, required are files results.csv and informationLossBounds.csv in your results folder
#java -Xmx5G -jar heurakles-bench.jar 5 2

# only generate configuration file based on code (2nd param is required but will be ignored)
java -Xmx5G -jar heurakles-bench.jar 6 * 