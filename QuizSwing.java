import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QuizSwing extends JFrame implements ActionListener {

    private static final int NUM_OPTIONS = 4;
    private static final int TIME_LIMIT = 15;

    private final String[] questions = {
            "What is the capital of France?",
            "How many continents are there on Earth?",
            "Which country is known as the 'Land of the Rising Sun'?",
            "What is the largest country in the world by land area?",
            "What is the largest planet in our solar system?"
    };

    private final String[][] options = {
            {"Berlin", "Madrid", "Paris", "Rome"},
            {"5", "6", "7", "8"},
            {"Japan", "South Korea", "China", "Thailand"},
            {"Canada", "Russia", "China", "USA"},
            {"Earth", "Jupiter", "Mars", "Saturn"}
    };

    private final char[] answers = {'C', 'C', 'A', 'B', 'B'};
    private final char[] userAnswers = new char[questions.length];

    private int currentIndex = 0;
    private int score = 0;
    private int timeLeft = TIME_LIMIT;

    private final JLabel questionLabel = new JLabel();
    private final JRadioButton[] optionButtons = new JRadioButton[NUM_OPTIONS];
    private final ButtonGroup optionsGroup = new ButtonGroup();

    private final JButton nextButton = new JButton("Next");
    private final JButton restartButton = new JButton("Restart");

    private final JLabel resultLabel = new JLabel();
    private final JLabel timerLabel = new JLabel("Time: " + TIME_LIMIT + "s");

    private Timer timer;

    public QuizSwing() {
        setTitle("Quiz Application");
        setSize(700, 450);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(setupQuestionPanel(), BorderLayout.CENTER);
        add(setupBottomPanel(), BorderLayout.SOUTH);
        setupTimerLabel();

        setupTimer();
        loadQuestion();

        setVisible(true);
    }

    private JPanel setupQuestionPanel() {
        JPanel questionPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionPanel.add(questionLabel);

        for (int i = 0; i < NUM_OPTIONS; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            optionButtons[i].setActionCommand(String.valueOf((char) ('A' + i)));
            optionsGroup.add(optionButtons[i]);
            questionPanel.add(optionButtons[i]);
        }

        return questionPanel;
    }

    private JPanel setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        nextButton.addActionListener(this);
        restartButton.addActionListener(this);

        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bottomPanel.add(nextButton);
        bottomPanel.add(restartButton);
        bottomPanel.add(resultLabel);

        return bottomPanel;
    }

    private void setupTimerLabel() {
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(timerLabel, BorderLayout.NORTH);
    }

    private void setupTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft + "s");

            if (timeLeft <= 0) {
                timer.stop();
                userAnswers[currentIndex] = '\0';
                resultLabel.setText("Time's up!");
                resultLabel.setForeground(Color.RED);

                currentIndex++;
                loadQuestion();
            }
        });
    }

    private void resetTimer() {
        timeLeft = TIME_LIMIT;
        timerLabel.setText("Time: " + timeLeft + "s");

        if (timer.isRunning()) {
            timer.stop();
        }

        timer.start();
    }

    private void loadQuestion() {
        if (currentIndex < questions.length) {
            questionLabel.setText((currentIndex + 1) + ". " + questions[currentIndex]);

            for (int i = 0; i < NUM_OPTIONS; i++) {
                optionButtons[i].setText((char) ('A' + i) + ") " + options[currentIndex][i]);
                optionButtons[i].setVisible(true);
            }

            optionsGroup.clearSelection();
            resultLabel.setText("");
            nextButton.setEnabled(true);

            resetTimer();
        } else {
            showSummaryScreen();
        }
    }

    private void checkAnswerAndMoveNext() {
        if (optionsGroup.getSelection() == null) {
            resultLabel.setText("Please select an answer!");
            resultLabel.setForeground(Color.RED);
            return;
        }

        char selectedOption = optionsGroup.getSelection().getActionCommand().charAt(0);
        userAnswers[currentIndex] = selectedOption;

        if (selectedOption == answers[currentIndex]) {
            score++;
        }

        currentIndex++;
        loadQuestion();
    }

    private void restartQuiz() {
        currentIndex = 0;
        score = 0;

        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = '\0';
        }

        getContentPane().removeAll();

        add(setupQuestionPanel(), BorderLayout.CENTER);
        add(setupBottomPanel(), BorderLayout.SOUTH);
        setupTimerLabel();

        loadQuestion();

        revalidate();
        repaint();
    }

    private void showSummaryScreen() {
        if (timer != null) {
            timer.stop();
        }

        getContentPane().removeAll();

        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("Quiz Summary");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel("Your score: " + score + "/" + questions.length);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        summaryPanel.add(titleLabel);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(scoreLabel);
        summaryPanel.add(Box.createVerticalStrut(20));

        for (int i = 0; i < questions.length; i++) {
            String userAnswer = userAnswers[i] == '\0' ? "No Answer" : String.valueOf(userAnswers[i]);
            String correctAnswer = String.valueOf(answers[i]);

            JLabel question = new JLabel((i + 1) + ". " + questions[i]);
            question.setFont(new Font("Arial", Font.BOLD, 14));

            JLabel answerInfo = new JLabel("Your Answer: " + userAnswer + " | Correct Answer: " + correctAnswer);
            answerInfo.setFont(new Font("Arial", Font.PLAIN, 14));

            if (userAnswers[i] == answers[i]) {
                answerInfo.setForeground(Color.GREEN.darker());
            } else {
                answerInfo.setForeground(Color.RED.darker());
            }

            summaryPanel.add(question);
            summaryPanel.add(answerInfo);
            summaryPanel.add(Box.createVerticalStrut(10));
        }

        JButton restartSummaryButton = new JButton("Restart Quiz");
        restartSummaryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartSummaryButton.addActionListener(e -> restartQuiz());

        summaryPanel.add(Box.createVerticalStrut(20));
        summaryPanel.add(restartSummaryButton);

        JScrollPane scrollPane = new JScrollPane(summaryPanel);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            checkAnswerAndMoveNext();
        } else if (e.getSource() == restartButton) {
            restartQuiz();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QuizSwing::new);
    }
}
