import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MIPSSimulatorGUI extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextArea registerArea;
    private JTextArea memoryArea;
    private MIPSSimulator simulator;
    private String[] instructions;

    public MIPSSimulatorGUI() {
        simulator = new MIPSSimulator();
        setTitle("MIPS Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        inputArea = new JTextArea();
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        registerArea = new JTextArea();
        registerArea.setEditable(false);
        memoryArea = new JTextArea();
        memoryArea.setEditable(false);

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(e -> {
            String code = inputArea.getText();
            instructions = code.split("\n");
            simulator.reset();
            outputArea.setText("");
            updateMemoryAndRegisters();
        });

        JButton stepButton = new JButton("Step");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    simulator.execute(new String[]{instructions[simulator.getPC()]});
                    updateMemoryAndRegisters();
                } catch (Exception ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadButton);
        buttonPanel.add(stepButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
        new JScrollPane(inputArea), new JScrollPane(outputArea));
        splitPane.setResizeWeight(0.5);

        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane,
         new JScrollPane(registerArea));
        mainPane.setResizeWeight(0.7);

        add(mainPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
        new JScrollPane(registerArea), new JScrollPane(memoryArea));
        rightPane.setResizeWeight(0.5);
        mainPane.setRightComponent(rightPane);
    }

    private void updateMemoryAndRegisters() {
        int[] registers = simulator.getRegisters();
        int[] memory = simulator.getMemory();
        int pc = simulator.getPC();

        StringBuilder registerText = new StringBuilder();
        for (int i = 0; i < registers.length; i++) {
            registerText.append(String.format("R%d: %d\n", i, registers[i]));
        }

        StringBuilder memoryText = new StringBuilder();
        for (int i = 0; i < memory.length; i++) {
            if (memory[i] != 0) {
                memoryText.append(String.format("M[%d]: %d\n", i, memory[i]));
            }
        }
        registerArea.setText(registerText.toString());
        memoryArea.setText(memoryText.toString());
        outputArea.setText("PC: " + pc);
    }

    public static void main(String[] args) {
        MIPSSimulatorGUI simulator = new MIPSSimulatorGUI();
        simulator.setVisible(true);
    }
}