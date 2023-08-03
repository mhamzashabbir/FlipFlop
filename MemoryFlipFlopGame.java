import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import net.ucanaccess.jdbc.UcanaccessDriver;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MemoryFlipFlopGame extends JFrame implements ActionListener {

    private JButton[] buttons;
    private ImageIcon[] buttonPairs;
    private boolean[] buttonFlipped;
    private int numFlipped;
    private int firstIndex;
    private int score;
    private Timer timer;
    private int seconds;

    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel topTimeLabel; // New label for top time
    private String username;

    private Map<ImageIcon, String> iconFileMap;

    private int topTime; // Variable to store the top time

    public MemoryFlipFlopGame() {
        super("Memory Flip-Flop Game");

        buttons = new JButton[16];
        buttonPairs = new ImageIcon[16];
        buttonFlipped = new boolean[16];
        numFlipped = 0;
        firstIndex = -1;
        score = 0;
        seconds = 0;

        // Initialize the button pairs with icons
        buttonPairs[0] = new ImageIcon(resizeImage("pic1.png", 70, 70));
        buttonPairs[1] = new ImageIcon(resizeImage("pic1.png", 70, 70));
        buttonPairs[2] = new ImageIcon(resizeImage("pic2.png", 70, 70));
        buttonPairs[3] = new ImageIcon(resizeImage("pic2.png", 70, 70));
        buttonPairs[4] = new ImageIcon(resizeImage("pic3.png", 70, 70));
        buttonPairs[5] = new ImageIcon(resizeImage("pic3.png", 70, 70));
        buttonPairs[6] = new ImageIcon(resizeImage("pic4.png", 70, 70));
        buttonPairs[7] = new ImageIcon(resizeImage("pic4.png", 70, 70));
        buttonPairs[8] = new ImageIcon(resizeImage("pic5.png", 70, 70));
        buttonPairs[9] = new ImageIcon(resizeImage("pic5.png", 70, 70));
        buttonPairs[10] = new ImageIcon(resizeImage("pic6.png", 70, 70));
        buttonPairs[11] = new ImageIcon(resizeImage("pic6.png", 70, 70));
        buttonPairs[12] = new ImageIcon(resizeImage("pic7.png", 70, 70));
        buttonPairs[13] = new ImageIcon(resizeImage("pic7.png", 70, 70));
        buttonPairs[14] = new ImageIcon(resizeImage("pic8.png", 70, 70));
        buttonPairs[15] = new ImageIcon(resizeImage("pic8.png", 70, 70));
        // Add more pairs as needed

        // Create a map to store the file paths for each icon
        iconFileMap = new HashMap<>();
        iconFileMap.put(buttonPairs[0], "pic1.png");
        iconFileMap.put(buttonPairs[1], "pic1.png");
        iconFileMap.put(buttonPairs[2], "pic2.png");
        iconFileMap.put(buttonPairs[3], "pic2.png");
        iconFileMap.put(buttonPairs[4], "pic3.png");
        iconFileMap.put(buttonPairs[5], "pic3.png");
        iconFileMap.put(buttonPairs[6], "pic4.png");
        iconFileMap.put(buttonPairs[7], "pic4.png");
        iconFileMap.put(buttonPairs[8], "pic5.png");
        iconFileMap.put(buttonPairs[9], "pic5.png");
        iconFileMap.put(buttonPairs[10], "pic6.png");
        iconFileMap.put(buttonPairs[11], "pic6.png");
        iconFileMap.put(buttonPairs[12], "pic7.png");
        iconFileMap.put(buttonPairs[13], "pic7.png");
        iconFileMap.put(buttonPairs[14], "pic8.png");
        iconFileMap.put(buttonPairs[15], "pic8.png");


        // Shuffle the button pairs
        for (int i = 0; i < buttonPairs.length; i++) {
            int j = (int) (Math.random() * buttonPairs.length);
            ImageIcon temp = buttonPairs[i];
            buttonPairs[i] = buttonPairs[j];
            buttonPairs[j] = temp;
        }


        // Create the buttons
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].setBackground(Color.GRAY);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 16));
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(this);

            // Customize the button's border
            Border lineBorder = new LineBorder(Color.WHITE, 3);
            buttons[i].setBorder(lineBorder);
        }

        // Set up the game board
		JPanel gameBoardPanel = new JPanel(new GridLayout(4, 4, 5, 5)); // Adjust the spacing as desired
		gameBoardPanel.setBackground(Color.LIGHT_GRAY); // Replace with your preferred background color
		for (JButton button : buttons) {
			gameBoardPanel.add(button);
		}

		// Set up the score and time labels
		topTimeLabel = new JLabel("Top Time: " + topTime + " seconds");
		timeLabel = new JLabel("Time: 0 seconds");
		scoreLabel = new JLabel("Score: 0");


		//JPanel infoPanel = new JPanel(new GridLayout(1, 3));// Modify the grid layout to accommodate the top time label
		// Create and add an info panel to the frame
		JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		infoPanel.setBackground(Color.DARK_GRAY);
		infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		Border coloredBorder = BorderFactory.createLineBorder(Color.RED);
		TitledBorder titledBorder = BorderFactory.createTitledBorder(coloredBorder, "Game Info");

		// Set the color of the title text
		titledBorder.setTitleColor(Color.WHITE);

		// Apply the custom border to the info panel
		infoPanel.setBorder(BorderFactory.createCompoundBorder(
			titledBorder,
			BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));

		// Customize the top time label
		topTimeLabel.setForeground(Color.WHITE); // Replace with your preferred font color
		topTimeLabel.setFont(new Font("Arial", Font.BOLD, 13)); // Replace "Arial" with your preferred font
		topTimeLabel.setHorizontalAlignment(SwingConstants.LEFT); // Adjust the alignment as needed

		// Customize the time label
		timeLabel.setForeground(Color.WHITE); // Replace with your preferred font color
		timeLabel.setFont(new Font("Arial", Font.BOLD, 13)); // Replace "Arial" with your preferred font
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER); // Adjust the alignment as needed

		// Customize the score label
		scoreLabel.setForeground(Color.WHITE); // Replace with your preferred font color
		scoreLabel.setFont(new Font("Arial", Font.BOLD, 13)); // Replace "Arial" with your preferred font
		scoreLabel.setHorizontalAlignment(SwingConstants.LEFT); // Adjust the alignment as needed

		// Add the labels to the info panel
		infoPanel.add(scoreLabel);
		infoPanel.add(timeLabel);
		infoPanel.add(topTimeLabel);

		// Add the info panel to the frame
		add(infoPanel, BorderLayout.NORTH);


        username = JOptionPane.showInputDialog(this, "Enter your username:");
        setTitle("Memory Flip-Flop Game - " + username);

        add(infoPanel, BorderLayout.NORTH);
        add(gameBoardPanel, BorderLayout.CENTER);

        // Set up the timer
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seconds++;
                timeLabel.setText("Time: " + seconds + " s");
            }
        });

		 private class TimerRunnable implements Runnable {
		        @Override
		        public void run() {
		            gameTimer = new Timer(1000, new ActionListener() {
		                public void actionPerformed(ActionEvent e) {
		                    seconds++;
		                    timeLabel.setText("Time: " + seconds + "s");
		                }
		            });

		            gameTimer.start();
		        }
    	}

        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        startGame();
    }

    private void startGame() {
		retrieveTopTime(); // Retrieve the top time from the database
        updateTopTimeLabel(); // Update the top time label
        // Reset all game variables and UI
        for (int i = 0; i < buttonFlipped.length; i++) {
            buttonFlipped[i] = false;
            buttons[i].setIcon(null);
        }
        numFlipped = 0;
        score = 0;
        seconds = 0;
        scoreLabel.setText("Score: 0");
        timeLabel.setText("Time: 0 s");

        timer.start();

    }

    private boolean isGameOver() {
        for (boolean flipped : buttonFlipped) {
            if (!flipped) {
                return false;
            }
        }
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        // Find the index of the clicked button
        int clickedIndex = -1;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == clickedButton) {
                clickedIndex = i;
                break;
            }
        }

        // Check if the clicked button is already flipped or matched
        if (buttonFlipped[clickedIndex]) {
            return;
        }

        // Flip the clicked button
        buttons[clickedIndex].setIcon(buttonPairs[clickedIndex]);
        buttonFlipped[clickedIndex] = true;

        // Check if it's the first or second button flipped
        if (numFlipped == 0) {
            firstIndex = clickedIndex;
            numFlipped++;
        } else if (numFlipped == 1) {
            // Compare the icons by file path
            String firstImagePath = iconFileMap.get(buttonPairs[firstIndex]);
            String clickedImagePath = iconFileMap.get(buttonPairs[clickedIndex]);

            if (firstImagePath.equals(clickedImagePath)) {
                // Match found
                buttonFlipped[firstIndex] = true;
                buttonFlipped[clickedIndex] = true;
                score++;
                scoreLabel.setText("Score: " + score);
            } else {
                // No match, flip both buttons back
                final int index1 = firstIndex;
                final int index2 = clickedIndex;
                Timer flipBackTimer = new Timer(500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        buttons[index1].setIcon(null);
                        buttons[index2].setIcon(null);
                        buttonFlipped[index1] = false;
                        buttonFlipped[index2] = false;
                    }
                });
                flipBackTimer.setRepeats(false);
                flipBackTimer.start();
            }

            numFlipped = 0;
        }


        // Check if the game is over
        if (isGameOver()) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Congratulations, " + username + "! You won the game!\nTime: " + seconds + " seconds\nScore: " + score);

		// Save game results to a file
		try {
			File file = new File("game_results.txt");
			FileWriter writer = new FileWriter(file, true); // append to an existing file
			writer.write("Username: " + username + "\n");
			writer.write("Time: " + seconds + " seconds\n");
			writer.write("Score: " + score + "\n\n");
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Store the username and time in the database
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			// Register the JDBC-ODBC Bridge driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			// Set up the database URL
			String url = "jdbc:ucanaccess://score.accdb";

			// Establish the connection
			connection = DriverManager.getConnection(url);

			// Prepare the SQL statement to insert data
			String sql = "INSERT INTO Scores (Username, Time) VALUES (?, ?)";
			statement = connection.prepareStatement(sql);
			statement.setString(1, username);
			statement.setInt(2, seconds);

			// Execute the insert statement
			statement.executeUpdate();
			connection.commit();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}


            int choice = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                startGame();
            } else {
                System.exit(0);
            }
        }
    }


    private Image resizeImage(String imagePath, int width, int height) {
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return resizedImage;
    }


    private void retrieveTopTime() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Register the JDBC-ODBC Bridge driver
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // Set up the database URL
            String url = "jdbc:ucanaccess://score.accdb";

            // Establish the connection
            connection = DriverManager.getConnection(url);

            // Prepare the SQL statement to retrieve the top time
            String sql = "SELECT MIN(Time) FROM Scores";
            statement = connection.prepareStatement(sql);

            // Execute the query
            resultSet = statement.executeQuery();

            // Retrieve the top time from the result set
            if (resultSet.next()) {
                topTime = resultSet.getInt(1);
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void updateTopTimeLabel() {
        topTimeLabel.setText("Top Time: " + topTime + " s");
    }

    private void updateTopTime() {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Register the JDBC-ODBC Bridge driver
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            // Set up the database URL
            String url = "jdbc:ucanaccess://score.accdb";

            // Establish the connection
            connection = DriverManager.getConnection(url);

            // Prepare the SQL statement to update the top time
            String sql = "UPDATE Scores SET Time = ? WHERE Username = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, seconds);
            statement.setString(2, username);

            // Execute the update
            statement.executeUpdate();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MemoryFlipFlopGame game = new MemoryFlipFlopGame();
            }
        });
    }
}
