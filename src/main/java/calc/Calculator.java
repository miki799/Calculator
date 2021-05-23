package calc;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class Calculator {

    // log4j
    private static final Logger logger = LogManager.getLogger();

    private static JButton createButton(char field) {

        JButton btn = new JButton(Character.toString(field));

        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.WHITE);

        btn.setPreferredSize(new Dimension(50, 50)); // size of each button

        return btn;
    }

    private static JTextField createTextField() {

        JTextField textField = new JTextField("0");

        textField.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        textField.setForeground(Color.WHITE);
        textField.setBackground(Color.BLUE);

        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // limits available space for text

        textField.setPreferredSize(new Dimension(100, 60));
        textField.setEditable(false); // nothing can be written manually

        return textField;
    }

    private static void createAndShowGUI() {

        JFrame jf = new JFrame("Calculator");

        // TEXT FIELD
        JTextField textField = createTextField();
        jf.getContentPane().add(textField, BorderLayout.NORTH);

        // PANEL WITH BUTTONS
        JPanel jPanel = new JPanel();
        jf.getContentPane().add(jPanel, BorderLayout.CENTER);
        jPanel.setLayout(new GridLayout(4, 4, 5, 5));
        jPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel.setBackground(Color.GRAY);

        // BUTTONS
        char[] fields = {'1', '2', '3', '+', '4', '5', '6', '-', '7', '8', '9', '*', '0', '=', 'C', '/'};
        MyActionListener myActionListener = new MyActionListener(textField);
        for (char field : fields) {
            JButton btn = createButton(field);
            jPanel.add(btn);
            btn.addActionListener(myActionListener);
        }

        // SETTING APP ICON
        ImageIcon icon = new ImageIcon("calculator_logo.png");
        jf.setIconImage(icon.getImage());

        jf.pack();
        jf.setLocationRelativeTo(null); // window on the center of the screen
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static class MyActionListener implements java.awt.event.ActionListener {

        private String s0 = "0", s1 = "", s2 = ""; // number, operator, number
        private int result = 0;
        private boolean exceptionFlag = false;
        private int repeated = 0;
        private final JTextField textField;

        MyActionListener(JTextField textField) {
            this.textField = textField;
        }

        public void actionPerformed(ActionEvent e) {

            char input = e.getActionCommand().charAt(0);
            logger.info("input = " + input);

            // if previous result was NaN,
            if (exceptionFlag) {
                s0 = "0";
                exceptionFlag = false;
            }

            if (Character.isDigit(input)) {

                if (repeated != 0) {
                    s1 = s2 = "";
                    repeated = 0;
                }

                if (!s1.equals("")) {
                    if (input == '0' && s2.equals("0")) return;
                    if (s2.equals("0")) {
                        s2 = "";
                        s2 += input;
                        textField.setText(s2);
                        logger.info("s0 = " + s0 + " s1 = " + s1 + " s2 = " + s2);
                        return;
                    }
                    s2 += input;
                } else if (result != 0 || s0.equals("0")) {
                    result = 0;
                    s0 = s1 = s2 = "";
                    s0 += input;
                } else {
                    s0 += input;
                }

                logger.info("s0 = " + s0 + " s1 = " + s1 + " s2 = " + s2);

                // display
                if (s2.equals("")) {
                    textField.setText(s0);
                } else {
                    textField.setText(s2);
                }

            } else if (input == '=') {

                if (!s0.isEmpty() && !s1.isEmpty() && s2.isEmpty()) {
                    s2 = s0;
                    repeated++;
                    calculation();
                    logger.info("s0 = " + s0 + ", s1 = " + s1 + ", s2 = " + s2);
                    textField.setText(s0);
                    return;
                }
                else if (!s0.equals("") && s2.equals("")) {
                    logger.info("s0 = " + s0 + ", s1 = " + s1 + ", s2 = " + s2);
                    textField.setText(s0);
                    //equalizeOnly = true;  ToDo
                    return;
                }

                //if (s2.equals("")) return;

                repeated++;
                calculation();
                logger.info("s0 = " + s0 + " s1 = " + s1 + " s2 = " + s2);
                textField.setText(s0);

            } else if (input == 'C') {

                s0 = "0";
                s1 = s2 = "";
                logger.info("s0 = " + s0 + " s1 = " + s1 + " s2 = " + s2);
                textField.setText(s0);

            } else {

                if (repeated != 0) {
                    s1 = s2 = "";
                    repeated = 0;
                }

                if (s1.equals("") || s2.equals("")) {
                    s1 = Character.toString(input);
                } else {
                    calculation();
                    if (!s1.equals(input)) s1 = Character.toString(input);
                    s2 = "";
                }

                logger.info("s0 = " + s0 + " s1 = " + s1 + " s2 = " + s2);

                // display
                if (s2.equals("")) {
                    textField.setText(s0);
                } else {
                    textField.setText(s2);
                }
            }
        }

        private void calculation() {

            result = Integer.parseInt(s0);

            switch (s1) {                               // result without pressing '='
                case "+":
                    result = Integer.parseInt(s0) + Integer.parseInt(s2);
                    break;
                case "-":
                    result = Integer.parseInt(s0) - Integer.parseInt(s2);
                    break;
                case "/":
                    try {
                        result = Integer.parseInt(s0) / Integer.parseInt(s2);
                    } catch (ArithmeticException ex) {
                        //System.out.println(ex.getMessage());
                        logger.error(ex.getMessage());
                        exceptionFlag = true;
                    }
                    break;
                case "*":
                    result = Integer.parseInt(s0) * Integer.parseInt(s2);
                    break;
            }

            if (exceptionFlag) {
                s0 = "NaN";
            } else {
                s0 = Integer.toString(result);
            }

        }

    }


    public static void calculatorRun() {

        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.INFO);

        logger.info("Launching calculator! Launching calculator! Launching calculator! Launching calculator!");

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

    }

}
