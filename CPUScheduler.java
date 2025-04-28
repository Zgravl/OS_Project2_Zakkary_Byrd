import java.util.*;

public class CPUScheduler{

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Process> processes = new ArrayList<>();

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        if (n == 50) {
            System.out.println("Loading 50 predefined test processes...");

            Random rand = new Random();
            for (int i = 0; i < 50; i++) {
                int at = rand.nextInt(50);        // Arrival time between 0-49
                int bt = rand.nextInt(10) + 1;     // Burst time between 1-10
                int pr = rand.nextInt(5) + 1;      // Priority between 1-5
                processes.add(new Process(i + 1, at, bt, pr));
            }

            // Sort by arrival time to simulate realistic system behavior
            processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

            System.out.println("Processes Loaded:");
            System.out.println("PID\tAT\tBT\tPRIORITY");
            for (Process p : processes) {
                System.out.printf("%d\t%d\t%d\t%d\n", p.id, p.arrivalTime, p.burstTime, p.priority);
            }

        } else {
            for (int i = 0; i < n; i++) {
                System.out.println("Process " + (i + 1));
                System.out.print("Arrival Time: ");
                int at = sc.nextInt();
                System.out.print("Burst Time: ");
                int bt = sc.nextInt();
                System.out.print("Priority (lower = higher): ");
                int pr = sc.nextInt();
                processes.add(new Process(i + 1, at, bt, pr));
            }
        }

        List<Metrics> allResults = new ArrayList<>();

        allResults.add(runSRTF(cloneProcesses(processes)));
        allResults.add(runMLFQ(cloneProcesses(processes)));
        allResults.add(runHRRN(cloneProcesses(processes)));

        printComparisonTable(allResults);
    }

    static List<Process> cloneProcesses(List<Process> original) {
        List<Process> clone = new ArrayList<>();
        for (Process p : original) {
            clone.add(new Process(p.id, p.arrivalTime, p.burstTime, p.priority));
        }
        return clone;
    }

    static Metrics runSRTF(List<Process> processes) {
        System.out.println("\n=== Shortest Remaining Time First (SRTF) ===");
        int currentTime = 0, completed = 0, busyTime = 0;
        int n = processes.size();

        while (completed < n) {
            Process shortest = null;
            int minTime = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && p.remainingTime < minTime) {
                    minTime = p.remainingTime;
                    shortest = p;
                }
            }

            if (shortest != null) {
                if (shortest.startTime == -1) shortest.startTime = currentTime;
                shortest.remainingTime--;
                busyTime++;
                if (shortest.remainingTime == 0) {
                    shortest.completionTime = currentTime + 1;
                    completed++;
                }
            }
            currentTime++;
        }

        return printResults(processes, busyTime, currentTime, "SRTF");
    }

    static Metrics runMLFQ(List<Process> processes) {
        System.out.println("\n=== Multi-Level Feedback Queue (MLFQ) ===");

        Queue<Process>[] queues = new Queue[3];
        for (int i = 0; i < 3; i++) queues[i] = new LinkedList<>();

        int[] timeQuantum = {4, 8, Integer.MAX_VALUE};
        int currentTime = 0, busyTime = 0, completed = 0;
        List<Process> arrived = new ArrayList<>(processes);
        arrived.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completed < processes.size()) {
            Iterator<Process> it = arrived.iterator();
            while (it.hasNext()) {
                Process p = it.next();
                if (p.arrivalTime <= currentTime) {
                    queues[0].add(p);
                    it.remove();
                }
            }

            Process current = null;
            int level = -1;

            for (int i = 0; i < 3; i++) {
                if (!queues[i].isEmpty()) {
                    current = queues[i].poll();
                    level = i;
                    break;
                }
            }

            if (current != null) {
                if (current.startTime == -1) current.startTime = currentTime;

                int execTime = Math.min(timeQuantum[level], current.remainingTime);
                for (int t = 0; t < execTime; t++) {
                    for (Iterator<Process> iter = arrived.iterator(); iter.hasNext(); ) {
                        Process p = iter.next();
                        if (p.arrivalTime == currentTime) {
                            queues[0].add(p);
                            iter.remove();
                        }
                    }

                    current.remainingTime--;
                    busyTime++;
                    currentTime++;

                    if (current.remainingTime == 0) break;
                }

                if (current.remainingTime == 0) {
                    current.completionTime = currentTime;
                    completed++;
                } else {
                    queues[Math.min(level + 1, 2)].add(current);
                }
            } else {
                currentTime++;
            }
        }

        return printResults(processes, busyTime, currentTime, "MLFQ");
    }

    static Metrics runHRRN(List<Process> processes) {
        System.out.println("\n=== Highest Response Ratio Next (HRRN) ===");

        int currentTime = 0, completed = 0, busyTime = 0;
        int n = processes.size();
        List<Process> queue = new ArrayList<>();

        while (completed < n) {
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.completionTime == 0 && !queue.contains(p)) {
                    queue.add(p);
                }
            }

            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process selected = null;
            double highestRatio = -1;

            for (Process p : queue) {
                int waitTime = currentTime - p.arrivalTime;
                double responseRatio = (waitTime + p.burstTime) / (double) p.burstTime;
                if (responseRatio > highestRatio) {
                    highestRatio = responseRatio;
                    selected = p;
                }
            }

            queue.remove(selected);
            selected.startTime = currentTime;
            currentTime += selected.burstTime;
            selected.completionTime = currentTime;
            busyTime += selected.burstTime;
            completed++;
        }

        return printResults(processes, busyTime, currentTime, "HRRN");
    }

    static Metrics printResults(List<Process> processes, int busyTime, int totalTime, String label) {
        double totalWT = 0, totalTAT = 0, totalRT = 0;
        int n = processes.size();

        System.out.println("PID\tAT\tBT\tCT\tTAT\tWT\tRT");
        for (Process p : processes) {
            int tat = p.turnaroundTime();
            int wt = p.waitingTime();
            int rt = p.responseTime();
            totalWT += wt;
            totalTAT += tat;
            totalRT += rt;
            System.out.printf("%d\t%d\t%d\t%d\t%d\t%d\t%d\n",
                    p.id, p.arrivalTime, p.burstTime, p.completionTime, tat, wt, rt);
        }

        double avgWT = totalWT / n;
        double avgTAT = totalTAT / n;
        double avgRT = totalRT / n;
        double cpuUtil = (busyTime / (double) totalTime) * 100;
        double throughput = n / (double) totalTime;

        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWT);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTAT);
        System.out.printf("Average Response Time: %.2f\n", avgRT);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtil);
        System.out.printf("Throughput: %.2f processes/unit time\n", throughput);

        return new Metrics(label, avgWT, avgTAT, avgRT, cpuUtil, throughput);
    }

    static void printComparisonTable(List<Metrics> metricsList) {
        System.out.println("\n=== Summary Comparison Table ===");
        System.out.printf("%-10s %-10s %-10s %-10s %-18s %-12s\n",
                "Algorithm", "Avg WT", "Avg TAT", "Avg RT", "CPU Utilization", "Throughput");

        for (Metrics m : metricsList) {
            System.out.printf("%-10s %-10.2f %-10.2f %-10.2f %-18.2f %-12.2f\n",
                    m.algorithm, m.avgWT, m.avgTAT, m.avgRT, m.cpuUtil, m.throughput);
        }
    }
}