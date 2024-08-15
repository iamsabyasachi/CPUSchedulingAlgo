import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Process {
    int id;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int priority;
    int completionTime;
    
    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.completionTime = 0;
    }
}

public class SchedulingSimulator extends JFrame {
    private JTextArea outputArea;
    private JTextField numProcessesField;
    private JTextField quantumField;
    private JTextField arrivalTimeField;
    private JTextField burstTimeField;
    private JTextField priorityField;
    private JButton addProcessButton;
    private JButton startButton;
    private JComboBox<String> algorithmComboBox;
    
    private Process[] processes;
    private int processCount = 0;

    public SchedulingSimulator() {
        setTitle("CPU Scheduling Simulator");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(8, 2));

        numProcessesField = new JTextField();
        quantumField = new JTextField();
        arrivalTimeField = new JTextField();
        burstTimeField = new JTextField();
        priorityField = new JTextField();
        
        addProcessButton = new JButton("Add Process");
        startButton = new JButton("Start Scheduling");
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        
        String[] algorithms = {"Round Robin", "Priority Non-Preemptive", "Shortest Time First"};
        algorithmComboBox = new JComboBox<>(algorithms);

        inputPanel.add(new JLabel("Number of Processes:"));
        inputPanel.add(numProcessesField);
        inputPanel.add(new JLabel("Time Quantum (for Round Robin):"));
        inputPanel.add(quantumField);
        inputPanel.add(new JLabel("Arrival Time:"));
        inputPanel.add(arrivalTimeField);
        inputPanel.add(new JLabel("Burst Time:"));
        inputPanel.add(burstTimeField);
        inputPanel.add(new JLabel("Priority (for Priority Scheduling):"));
        inputPanel.add(priorityField);
        inputPanel.add(new JLabel("Algorithm:"));
        inputPanel.add(algorithmComboBox);
        inputPanel.add(addProcessButton);
        inputPanel.add(startButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        addProcessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProcess();
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startScheduling();
            }
        });
    }

    private void addProcess() {
        int arrivalTime = Integer.parseInt(arrivalTimeField.getText());
        int burstTime = Integer.parseInt(burstTimeField.getText());
        int priority = Integer.parseInt(priorityField.getText());
        
        if (processes == null) {
            int numProcesses = Integer.parseInt(numProcessesField.getText());
            processes = new Process[numProcesses];
        }

        processes[processCount++] = new Process(processCount, arrivalTime, burstTime);
        processes[processCount - 1].priority = priority;
        
        outputArea.append("Added Process " + processCount + " | Arrival: " + arrivalTime + ", Burst: " + burstTime + ", Priority: " + priority + "\n");

        arrivalTimeField.setText("");
        burstTimeField.setText("");
        priorityField.setText("");
    }

    private void startScheduling() {
        String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
        outputArea.append("\nStarting " + selectedAlgorithm + " Scheduling...\n");

        switch (selectedAlgorithm) {
            case "Round Robin":
                int quantum = Integer.parseInt(quantumField.getText());
                roundRobin(quantum);
                break;
            case "Priority Non-Preemptive":
                priorityNonPreemptive();
                break;
            case "Shortest Time First":
                shortestTimeFirst();
                break;
        }
    }

    private void roundRobin(int quantum) {
        int time = 0;
        int processesRemaining = processes.length;
        
        while (processesRemaining > 0) {
            boolean allDone = true;
            for (Process process : processes) {
                if (process.remainingTime > 0) {
                    allDone = false;
                    if (process.remainingTime > quantum) {
                        time += quantum;
                        process.remainingTime -= quantum;
                    } else {
                        time += process.remainingTime;
                        process.remainingTime = 0;
                        processesRemaining--;
                    }
                    outputArea.append("Process " + process.id + " executed from " + (time - quantum) + " to " + time + "\n");
                }
            }
            if (allDone) break;
        }
    }

    private void priorityNonPreemptive() {
        int time = 0;
        int processesRemaining = processes.length;

        while (processesRemaining > 0) {
            int highestPriority = -1;
            int idx = -1;

            for (int i = 0; i < processes.length; i++) {
                if (processes[i].arrivalTime <= time && processes[i].completionTime == 0) {
                    if (highestPriority == -1 || processes[i].priority < processes[highestPriority].priority) {
                        highestPriority = i;
                    }
                }
            }

            if (highestPriority == -1) {
                time++;
                continue;
            }

            idx = highestPriority;
            processes[idx].completionTime = time + processes[idx].burstTime;
            time += processes[idx].burstTime;
            processesRemaining--;
            outputArea.append("Process " + processes[idx].id + " executed from " + (time - processes[idx].burstTime) + " to " + time + "\n");
        }
    }

    private void shortestTimeFirst() {
        int time = 0;
        int processesRemaining = processes.length;

        while (processesRemaining > 0) {
            int shortest = -1;
            int idx = -1;

            for (int i = 0; i < processes.length; i++) {
                if (processes[i].arrivalTime <= time && processes[i].completionTime == 0) {
                    if (shortest == -1 || processes[i].burstTime < processes[shortest].burstTime) {
                        shortest = i;
                    }
                }
            }

            if (shortest == -1) {
                time++;
                continue;
            }

            idx = shortest;
            processes[idx].completionTime = time + processes[idx].burstTime;
            time += processes[idx].burstTime;
            processesRemaining--;
            outputArea.append("Process " + processes[idx].id + " executed from " + (time - processes[idx].burstTime) + " to " + time + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SchedulingSimulator().setVisible(true);
            }
        });
    }
}
