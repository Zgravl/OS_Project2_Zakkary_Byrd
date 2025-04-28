# CPU Scheduler Simulator

This project is a **Java-based CPU Scheduler Simulator** implementing the following classic scheduling algorithms:

- **Shortest Remaining Time First (SRTF)**
- **Multi-Level Feedback Queue (MLFQ)**
- **Highest Response Ratio Next (HRRN)**

It reads processes (Process Control Blocks) from user input or a predefined test case and simulates their scheduling while measuring:

- **Average Waiting Time (AWT)**
- **Average Turnaround Time (ATT)**
- **CPU Utilization (%)**
- **Throughput (Processes per second)**
- **Response Time (RT)**

At the end, it outputs a comparison table across all three algorithms.

---

#Features

- Supports user-defined or auto-generated (50) processes.
- Measures and reports key performance metrics.
- Displays scheduling results clearly in tabular format.
- no external libraries required.

---

# Environment Setup

You need the following to run the project:
- **Java Development Kit (JDK)**
- **Windows Command Prompt**

## Check if Java is installed:
Open Command Prompt and run:

```bash
java -version
javac -version
```

# Installation and Running

### 1. Create a Project Folder:
First, navigate to the root folder for your project using the following commands:

```bash
cd [path to root foler]
```
### 2. Compile and Run The Project
compile and run the project by running the following commands

```bash
javac CPUScheduler.java
java CPUScheduler
```
