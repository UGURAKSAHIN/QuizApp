import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class QuizSwing extends JFrame implements ActionListener {

    String[] questions = {
            "What is the capital of France?",
            "How many continents are there on Earth?",
            "Which country is known as the 'Land of the Rising Sun'?",
            "What is the largest country in the world by land area?",
            "What is the largest planet in our solar system?"
    };
    String[][] options = {
            {"Berlin", "Madrid", "Paris", "Rome"},
            {"5", "6", "7", "8"},
            {"Japan", "South Korea", "China", "Thailand"},
            {"Canada", "Russia", "China", "USA"},
            {"Earth", "Jupiter", "Mars", "Saturn"}
    };
    char[] answers = {'C', 'C', 'A', 'B', 'B'};
    char[] userAnswers = new char[questions.length];

    private static final int NUM_OPTIONS = 4;

    int currentIndex = 0;
    int score = 0;
    int timePerQuestion = 15;
    private static final int NUM_QUESTIONS = 5;

    JLabel questionLabel = new JLabel();
    JRadioButton[] optionButtons = new JRadioButton[NUM_OPTIONS];
    ButtonGroup optionsGroup = new ButtonGroup();
    JButton nextButton = new JButton("Next");
    JButton restartButton = new JButton("Restart");
    JLabel resultLabel = new JLabel();
    JLabel timerLabel = new JLabel("Time: 15s");

    Timer timer;

    public QuizSwing() {
        setTitle("Quiz Application");
        setSize(600, 400);
        setLayout(new BorderLayout());

        add(setupQuestionPanel(), BorderLayout.CENTER);
        add(setupBottomPanel(), BorderLayout.SOUTH);
        setupTimerLabel();
        setupTimer();

        loadQuestion();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel setupQuestionPanel() {
        JPanel questionPanel = new JPanel(new GridLayout(0, 1));
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
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        nextButton.addActionListener(this);
        restartButton.addActionListener(this);
        bottomPanel.add(nextButton);
        bottomPanel.add(restartButton);
        bottomPanel.add(resultLabel);
        return bottomPanel;
    }
    

    private void setupTimerLabel() {
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(timerLabel, BorderLayout.NORTH);
    }

    private void setupTimer(){

        timer = new Timer(1000, e -> {
        timePerQuestion--;
        timerLabel.setText("Time: " + timePerQuestion);
        if (timePerQuestion == 0) {
            timer.stop();
            resultLabel.setText("Time's up! Moving to next question.");
            resultLabel.setForeground(Color.RED);
            currentIndex++;
            loadQuestion();

        }
    });
}

    private void resetTimer(){
        timePerQuestion = 15;
        timerLabel.setText("Time: " + timePerQuestion);
        if(timer.isRunning()){

            timer.stop();

        }
        timer.start();
    }

    private void showFinalResults() {
        questionLabel.setText("Quiz Finished!");
        for (JRadioButton btn : optionButtons) {
            btn.setVisible(false);
        }
        nextButton.setEnabled(false);
        if (timer != null) {
            timer.stop();
        }
        resultLabel.setText("Your score: " + score + "/" + NUM_QUESTIONS);
        resultLabel.setForeground(Color.RED);
    }

    private void loadQuestion() {
        if (currentIndex < questions.length) {
            questionLabel.setText((currentIndex + 1) + ". " + questions[currentIndex]);
            for (int i = 0; i < NUM_OPTIONS; i++) {
                optionButtons[i].setText(options[currentIndex][i]);
            }
            optionsGroup.clearSelection();
            resetTimer();

        } else {
            showFinalResults();
            
        }
    }

    private void restartQuiz(){
        currentIndex = 0;
        score = 0;
        for(int i = 0; i < userAnswers.length; i++){
            userAnswers[i] = '\0';
        }
        for(JRadioButton btn : optionButtons){
            btn.setVisible(true);
        }
        nextButton.setEnabled(true);
        resultLabel.setText("");
    }

    private void showSummaryScreen(){

        JPanel summJPanel = new JPanel();
        summJPanel.setLayout(new BoxLayout(summJPanel, BoxLayout.Y_AXIS));

        JLabel titlelabel = new JLabel("Quiz Summary");
        titlelabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlelabel.setAlignmentX(CENTER_ALIGNMENT);
        summJPanel.add(titlelabel);
        summJPanel.add(new JLabel(" "));

        for(int i = 0; i < questions.length; i++){
            String correctAnswers = String.valueOf(answers[i]);
            String userAnswer = userAnswers[i] == '\0' ? "No Answer" : String.valueOf(userAnswers[i]);

            JLabel qLabel = new JLabel((i+1) + ". " + questions[i]);
            summJPanel.add(qLabel);
            JLabel questionResult = new JLabel();
            questionResult.setFont(new Font("Arial", Font.PLAIN, 16));
            summJPanel.add(questionResult);

            JLabel answersLabel = new JLabel("Your Answer: " + userAnswer + " | Correct Answer: " + correctAnswers);
            if(userAnswers[i] == answers[i]){
                answersLabel.setForeground(Color.GREEN.darker());
            }else{
                answersLabel.setForeground(Color.RED.darker());
            }
            summJPanel.add(answersLabel);
            summJPanel.add(new JLabel(" "));

        }
        getContentPane().removeAll();
        add(summJPanel, BorderLayout.CENTER);

        revalidate();
        repaint();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            if (optionsGroup.getSelection() == null) {
                resultLabel.setText("Please select an answer before proceeding!");
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
        } else if (e.getSource() == restartButton) {
            restartQuiz();
        } else {
            showSummaryScreen();
        }
    }

    public static void main(String[] args) {
        new QuizSwing();
    }
}
