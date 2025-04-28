import java.util.*;

class Process {
    int id, arrivalTime, burstTime, remainingTime, completionTime, startTime = -1, priority;

    public Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
    }

    int turnaroundTime() {
        return completionTime - arrivalTime;
    }

    int waitingTime() {
        return turnaroundTime() - burstTime;
    }

    int responseTime() {
        return startTime - arrivalTime;
    }
}

class Metrics {
    String algorithm;
    double avgWT, avgTAT, avgRT, cpuUtil, throughput;

    public Metrics(String algorithm, double avgWT, double avgTAT, double avgRT, double cpuUtil, double throughput) {
        this.algorithm = algorithm;
        this.avgWT = avgWT;
        this.avgTAT = avgTAT;
        this.avgRT = avgRT;
        this.cpuUtil = cpuUtil;
        this.throughput = throughput;
    }
}