import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class Main {
    private static int numVariables, numConstraints, objectiveType, constraintType;
    private static double[][] tableau;
    private static int type[];
    private static String lessthan = "≤";
    private static String morethan = "≥";
    private static String[] options = { "≤", "≥" };
    private static double objectiveValue;
    private static double[] decisionVariables;

    private static List<JTextField> getAllTextFields(JPanel panel) {
        List<JTextField> textFields = new ArrayList<JTextField>();
        type = new int[numConstraints];
        int cnt = 0;
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JTextField) {
                textFields.add((JTextField) component);
            } else if (component instanceof JPanel) {
                textFields.addAll(getAllTextFields((JPanel) component));
            } else if (component instanceof JComboBox) {
                String s = (String) ((JComboBox) component).getSelectedItem();
                if (s == lessthan) {
                    type[cnt] = 0;
                } else if (s == morethan) {
                    type[cnt] = 1;
                }
                // System.out.println(type[cnt]);
                cnt++;
            }
        }
        return textFields;
    }

    public static void main(String[] args) {

        Border border = BorderFactory.createLineBorder(Color.black, 2, true);
        Integer[] varOptions = { 2, 3, 4, 5, 6, 7, 8, 9 };
        String[] objOptions = { "Maximization", "Minimization" };
        JComboBox<String> objOptionsComboBox = new JComboBox<>(objOptions);

        JFrame frame = new JFrame();
        JLabel numVariableLabel = new JLabel();
        JLabel numConstraintLabel = new JLabel();
        JComboBox<Integer> numVariableBox = new JComboBox<>(varOptions);
        JComboBox<Integer> numConstraintBox = new JComboBox<>(varOptions);
        JButton generateButton = new JButton();
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0, 1));
        JPanel outputPanel = new JPanel();
        JButton calButton = new JButton();
        calButton.setVisible(false);
        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setBorder(border);
        resultTextArea.setVisible(false);

        // no of variables
        numVariableLabel.setText(" Number of Variables: ");
        numVariableLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        numVariableLabel.setBackground(new Color(255, 255, 255, 10));
        numVariableLabel.setBorder(border);
        numVariableLabel.setBounds(20, 20, 271, 38);
        // input
        numVariableBox.setBounds(240, 21, 50, 36);
        numVariableBox.setFocusable(false);
        numVariableBox.setBackground(Color.WHITE);

        // no of constraints
        numConstraintLabel.setText(" Number of Constraints: ");
        numConstraintLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        numConstraintLabel.setBackground(new Color(255, 255, 255, 10));
        numConstraintLabel.setBorder(border);
        numConstraintLabel.setBounds(371, 20, 281, 38);

        // input
        numConstraintBox.setBounds(601, 21, 50, 36);
        numConstraintBox.setFocusable(false);
        numConstraintBox.setBackground(Color.WHITE);

        // generate
        generateButton.setBounds(752, 20, 200, 36);
        generateButton.setText(" Generate ");
        generateButton.setFocusable(false);
        generateButton.setFont(new Font("Poppins", Font.BOLD, 18));
        generateButton.setBackground(Color.WHITE);
        generateButton.setBorder(border);
        generateButton.addActionListener(e -> {
            numVariables = (Integer) numVariableBox.getSelectedItem();
            numConstraints = (Integer) numConstraintBox.getSelectedItem();
            tableau = new double[numConstraints + 1][numVariables + numConstraints + 1];

            if (numVariables > 0 && numConstraints > 0) {
                // Clear existing components in the inputPanel
                inputPanel.removeAll();

                // Create text fields for coefficients
                JPanel objectivPanel = new JPanel();
                objectivPanel.add(objOptionsComboBox);
                objectivPanel.add(new JLabel("Objective Function: Z ="));
                for (int i = 0; i < numVariables; i++) {
                    JTextField textField = new JTextField(5);
                    objectivPanel.add(textField);
                    objectivPanel.add(new JLabel("x" + (i + 1)));
                }
                inputPanel.add(objectivPanel);
                for (int i = 0; i < numConstraints; i++) {
                    JPanel constraintPanel = new JPanel();
                    for (int j = 0; j < numVariables; j++) {
                        JTextField textField = new JTextField(5);
                        constraintPanel.add(textField);
                        constraintPanel.add(new JLabel("x" + (j + 1)));
                    }
                    constraintPanel.add(new JComboBox<>(options));
                    JTextField rhsField = new JTextField(5);
                    constraintPanel.add(rhsField);

                    inputPanel.add(constraintPanel);
                }

                inputPanel.revalidate();
                inputPanel.repaint();
                calButton.setVisible(true);
            }
        });

        // panel
        inputPanel.setBounds(20, 100, 800, 500);
        // inputPanel.setBackground(Color.BLUE);
        inputPanel.setOpaque(true);

        // output panel
        outputPanel.setBounds(820, 100, 500, 500);
        // outputPanel.setBackground(Color.GREEN);
        outputPanel.add(calButton);
        outputPanel.add(resultTextArea);
        resultTextArea.setBounds(702, 250, 400, 200);
        resultTextArea.setFont(new Font("Poppins", Font.BOLD, 18));

        // calculate button
        calButton.setBounds(752, 20, 200, 36);
        calButton.setText(" Calculate ");
        calButton.setFocusable(false);
        calButton.setFont(new Font("Poppins", Font.BOLD, 30));
        calButton.setBackground(Color.WHITE);
        calButton.setBorder(border);

        calButton.addActionListener(e -> {
            if (objOptionsComboBox.getSelectedItem() == "Maximization") {
                objectiveType = 0;
            } else
                objectiveType = 1;

            resultTextArea.setText("");
            int i = 0;
            List<JTextField> textFields = getAllTextFields(inputPanel);
            double[] values = new double[30];

            // System.out.println(objectiveType);
            for (JTextField textField : textFields) {
                String value = textField.getText();
                double valuei = Double.parseDouble(value);
                if (value != null) {
                    values[i] = valuei;
                    i++;
                }
            }

            int totalinputs = (numConstraints * (numVariables + 1)) + numVariables;
            System.out.println("");

            System.out.println("Initial Simplex Table: ");
            int numIteration = 1;
            switch (objectiveType) {
                case 0:
                    int j = 0;
                    int a = 0;
                    while (j < totalinputs) {
                        for (int k = 0; k < numVariables; j++, k++) {
                            tableau[numConstraints][k] = -values[j];
                        }
                        for (int l = 0; l < numConstraints; l++) {
                            for (int m = 0; m < numVariables; m++) {
                                tableau[l][m] = values[j];
                                j++;
                            }
                            tableau[l][numVariables + numConstraints] = values[j];
                            j++;
                        }
                    }
                    while (a < numConstraints) {
                        constraintType = 0;
                        switch (constraintType) {
                            case 0:
                                for (int b = 0; b < numConstraints; b++) {
                                    tableau[b][numVariables + b] = 1;
                                }
                                break;
                        }
                        a++;
                    }
                    DisplayTableau(tableau);
                    while (hasNegativeEntry(tableau)) {
                        System.out.println("");
                        System.out.println("Iteration " + numIteration);
                        SimplexIteration(tableau);
                        DisplayTableau(tableau);
                        numIteration++;
                    }

                    objectiveValue = tableau[numConstraints][numVariables + numConstraints];
                    decisionVariables = new double[numVariables];

                    for (int ii = 0; ii < numVariables; ii++) {
                        int nonBasicVar = findNonBasicVariable(tableau, ii);
                        if (nonBasicVar != -1) {
                            decisionVariables[ii] = tableau[nonBasicVar][numVariables + numConstraints];
                        } else {
                            decisionVariables[ii] = 0.0;
                        }
                    }
                    break;

                case 1:
                    int jj = 0;
                    int aa = 0;
                    while (jj < totalinputs) {
                        for (int k = 0; k < numVariables; jj++, k++) {
                            tableau[k][numVariables + numConstraints] = values[jj];
                        }
                        for (int l = 0; l < numConstraints; l++) {
                            for (int m = 0; m < numVariables; m++) {
                                tableau[m][l] = values[jj];
                                jj++;
                            }
                            tableau[numConstraints][l] = -values[jj];
                            jj++;
                        }
                    }
                    while (aa < numConstraints) {
                        constraintType = 1;
                        switch (constraintType) {
                            case 1:
                                for (int b = 0; b < numConstraints; b++) {
                                    tableau[b][numVariables + b] = 1;
                                }
                                break;
                        }
                        aa++;
                    }
                    numIteration = 1;
                    DisplayTableau(tableau);
                    while (hasNegativeEntry(tableau)) {
                        System.out.println("");
                        System.out.println("Iteration " + numIteration);
                        SimplexIteration(tableau);
                        DisplayTableau(tableau);
                        numIteration++;
                    }
                    objectiveValue = tableau[numConstraints][numVariables + numConstraints];
                    decisionVariables = new double[numVariables];

                    int var = numVariables;
                    for (int ii = 0; ii < numVariables; var++, ii++) {
                        decisionVariables[ii] = tableau[numConstraints][var];
                    }

                    break;
            }

            if (objectiveType == 0) {
                System.out.println("Maximization:");
                resultTextArea.append("Maximization:\n");
            } else {
                System.out.println("Minimization:");
                resultTextArea.append("Minimization:\n");
            }

            System.out.println("Objective Value (Z) = " + objectiveValue);
            resultTextArea.append("Objective Value (Z) = " + objectiveValue + "\n");

            for (int jj = 0; jj < numVariables; jj++) {
                System.out.println("x" + (jj + 1) + " = " + decisionVariables[jj]);
                resultTextArea.append("x" + (jj + 1) + " = " + decisionVariables[jj] + "\n");
            }
            resultTextArea.setVisible(true);
        });

        // frame
        frame.setTitle("LPP SOLVER");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        // frame.getContentPane().setBackground(new Color(123,50,250));
        frame.add(numVariableLabel);
        frame.add(numVariableBox);
        frame.add(numConstraintLabel);
        frame.add(numConstraintBox);
        frame.add(generateButton);
        frame.add(inputPanel);
        frame.setLayout(null);
        frame.add(outputPanel);

    }

    public static void DisplayTableau(double[][] tableau) {
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[i].length; j++) {
                System.out.print(tableau[i][j] + "     ");
            }
            System.out.println();
        }
    }

    public static int findNonBasicVariable(double[][] tableau, int basicVarColumn) {
        for (int i = 0; i < tableau.length; i++) {
            if (tableau[i][basicVarColumn] == 1 && countNonZeroInColumn(tableau, basicVarColumn) == 1) {
                return i;
            }
        }
        return -1;
    }

    // Method to count the number of non-zero entries in a column of the tableau
    public static int countNonZeroInColumn(double[][] tableau, int column) {
        int count = 0;
        for (int i = 0; i < tableau.length; i++) {
            if (tableau[i][column] != 0) {
                count++;
            }
        }
        return count;
    }

    public static boolean hasNegativeEntry(double[][] tableau) {
        for (int i = 0; i < tableau[tableau.length - 1].length; i++) {
            if (tableau[tableau.length - 1][i] < 0) {
                return true;
            }
        }
        return false;
    }

    public static void SimplexIteration(double[][] tableau) {
        int pivotColumn = 0;
        int pivotRow = 0;
        double temp = 0;

        for (int i = 0; i < tableau[tableau.length - 1].length; i++) {
            if (tableau[tableau.length - 1][i] < 0 && tableau[tableau.length - 1][i] < temp) {
                temp = tableau[tableau.length - 1][i];
                pivotColumn = i;
            }
        }

        temp = tableau[0][tableau[0].length - 1] / tableau[0][pivotColumn];
        for (int i = 1; i < tableau.length - 1; i++) {
            double ratio = tableau[i][tableau[i].length - 1] / tableau[i][pivotColumn];
            if (ratio > 0 && ratio < temp) {
                temp = ratio;
                pivotRow = i;
            }
        }

        double pivotElement = tableau[pivotRow][pivotColumn];

        for (int i = 0; i < tableau[pivotRow].length; i++) {
            tableau[pivotRow][i] /= pivotElement;
        }

        for (int i = 0; i < tableau.length; i++) {
            if (i == pivotRow) {
                continue;
            }
            double mult = -1 * tableau[i][pivotColumn];

            for (int j = 0; j < tableau[i].length; j++) {
                tableau[i][j] += mult * tableau[pivotRow][j];
            }
        }
    }
}