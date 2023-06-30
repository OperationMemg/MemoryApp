/* CREATE DATABASE Memorygame;
use Memorygame;

-- Table to store user profiles
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  code VARCHAR(50) NOT NULL,
  birthdate DATE NOT NULL
);

-- Table to store game scores
CREATE TABLE scores (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  score INT NOT NULL,
  mistakes INT NOT NULL,
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
ALTER TABLE users
ADD COLUMN password VARCHAR(60) NOT NULL;

ALTER TABLE users
ADD COLUMN username VARCHAR(50) NOT NULL;

ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) NOT NULL;  */                                                       

package Memorygame.draft;

public class Main {
    public static void main(String[] args) {
        MemoryGameApplication gameApp = new MemoryGameApplication();
        gameApp.run();
    }
}                                                                                                                                                                                                                                   
                                                                                                                                            
package Memorygame.draft;

import java.util.Random;

public class Game {
    private int[][] gameBoard;
    private int[][] displayBoard;
    private int level;
    private int cardVisibilityDuration;
    private int cardClosingSpeed;
    private int numPairs;
    private int maxAttempts;
    private int score;
    private int bonusPoints;

    public void startGame() {
        // Initialize game board, display board, level, and other variables
        // ...

        // Set level-specific values
        setLevelValues();

        // Shuffle and distribute cards on the game board
        distributeCards();

        // Play the game
        playGame();
    }

    private void setLevelValues() {
        // Set level-specific values based on the current level
        switch (level) {
            case 1:
                cardVisibilityDuration = 20; // 20 seconds
                cardClosingSpeed = 3; // cards close after 3 seconds
                break;
            case 2:
                cardVisibilityDuration = 10; // 10 seconds
                cardClosingSpeed = 2; // cards close after 2 seconds
                break;
            case 3:
                cardVisibilityDuration = 5; // 5 seconds
                cardClosingSpeed = 1; // cards close after 1 second
                break;
            // Add more cases for higher levels with different durations and speeds
        }
    }

    private void distributeCards() {
        // Create an array to store all the card values
        int[] cardValues = new int[numPairs * 2];
        for (int i = 0; i < numPairs * 2; i += 2) {
            cardValues[i] = i / 2 + 1;
            cardValues[i + 1] = i / 2 + 1;
        }

        // Shuffle the card values randomly
        Random random = new Random();
        for (int i = cardValues.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = cardValues[i];
            cardValues[i] = cardValues[j];
            cardValues[j] = temp;
        }

        // Distribute the card values on the game board
        int index = 0;
        for (int row = 0; row < gameBoard.length; row++) {
            for (int col = 0; col < gameBoard[row].length; col++) {
                gameBoard[row][col] = cardValues[index];
                index++;
            }
        }
    }

    private void playGame() {
        int attempts = 0;
        int openCards = 0;
        int firstCardRow = -1;
        int firstCardCol = -1;

        while (openCards < numPairs * 2 && attempts < maxAttempts) {
            // Display the game board
            displayGameBoard();

            // Get user input for card selection
            int selectedRow = getUserInput("Enter the row number of the card: ");
            int selectedCol = getUserInput("Enter the column number of the card: ");

            // Check if the selected card is valid
            if (!isValidCard(selectedRow, selectedCol)) {
                System.out.println("Invalid card selection. Please try again.");
                continue;
            }

            // Check if the selected card is already open
            if (displayBoard[selectedRow][selectedCol] != 0) {
                System.out.println("The card is already open. Please select another card.");
                continue;
            }

            // Open the selected card
            displayBoard[selectedRow][selectedCol] = gameBoard[selectedRow][selectedCol];
            openCards++;

            // Check if it's the first or second card of a pair
            if (openCards % 2 == 1) {
                firstCardRow = selectedRow;
                firstCardCol = selectedCol;
            } else {
                // Second card selected, compare with the first card
                if (gameBoard[selectedRow][selectedCol] == gameBoard[firstCardRow][firstCardCol]) {
                    // Match found
                    System.out.println("Match found!");
                    score += 2;
                } else {
                    // No match
                    System.out.println("No match!");
                    attempts++;
                }

                // Wait for the specified card visibility duration
                try {
                    Thread.sleep(cardVisibilityDuration * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Close the selected cards
                displayBoard[selectedRow][selectedCol] = 0;
                displayBoard[firstCardRow][firstCardCol] = 0;
            }
        }

        // Calculate bonus points for finding all pairs with limited attempts
        if (openCards == numPairs * 2 && attempts <= maxAttempts) {
            bonusPoints = (maxAttempts - attempts) * 3;
            score += bonusPoints;
            System.out.println("Congratulations! You found all pairs and earned a bonus of " + bonusPoints + " points.");
        }

        // Display the final score
        System.out.println("Game over. Your score: " + score);
    }

    private void displayGameBoard() {
        // Display the current state of the game board
        // ...
    }

    private boolean isValidCard(int row, int col) {
        // Check if the selected card coordinates are within the valid range
        return row >= 0 && row < gameBoard.length && col >= 0 && col < gameBoard[row].length;
    }

    private int getUserInput(String message) {
        // Get user input for row or column selection
        // ...
        return 0; // Placeholder, replace with actual user input handling
    }
}                                            

  /* ERRORS this class has:                                                                                                  
  cardVisibilityDuration * 1000: integer multiplication implicitly cast to long                                                                    Variable 'firstCardIndex' initializer '-1' is redundant                                                                                                                                       Variable 'secondCardIndex' initializer '-1' is redundant                                                                                                               Call to 'Thread.sleep()' in a loop, probably busy-waiting  */
                                                                                                                                         
                                                                                                                                
                                                                                                                                                                     
package Memorygame.draft;

public class MemoryGameApplication {
        private DatabaseManager dbManager = new DatabaseManager();
        private User user;
        private Game game;
        private Score score;

        public MemoryGameApplication() {
            user = new User(dbManager);
            game = new Game();
            score = new Score(dbManager);
        }

        public void run() {
            dbManager.connect();

            // Display login or registration menu
            user.displayLoginOrRegistrationMenu();

            // Handle user input and perform login or registration
            user.handleLoginOrRegistration();

            // Start the game
            game.startGame();

            // Calculate the score
            int gameScore = game.calculateScore();

            // Save the score
            score.saveScore(user.getUserId(), gameScore);

            // Display top 10 scores
            score.displayTopScores();

            dbManager.disconnect();
        }
    }                                                                                                                                                                                                                      
/*The ERRORS in this class are:        

  Field 'dbmanager' may be 'final'                                                                                                                 Field 'user' may be 'final'                                                                                                                                                                           Field 'game' may be 'final'                                                                                                                                                                      Field 'score' may be 'final'  */                                                                                                                                                                        
package Memorygame.draft;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {
    private Connection connection;
    private String url;
    private String username;
    private String password;

    public DatabaseManager() {
        this.url = "jdbc:mysql://127.0.0.1:3306/Memorygame";
        this.username = "root";
        this.password = "Roodkapje_2004";
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Disconnected from the database.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to disconnect from the database: " + e.getMessage());
        }
    }

    public void saveUserDetails(String name, String code, String birthdate) throws SQLException {
        String insertQuery = "INSERT INTO users (name, code, birthdate) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, code);
            preparedStatement.setString(3, birthdate);

            preparedStatement.executeUpdate();
        }
    }

    // Other methods specific to the DatabaseManager class
}

/* The ERRORS this class has: 
Method 'verifyLogin(java.lang.String, java.lang.String)' is never used
Parameter 'username' is never used
Parameter 'password' is never used
Method 'getUserId(java.lang.String)' is never used
Parameter 'username' is never used
Method 'registerUser(java.lang.String, java.lang.String)' is never used
Parameter 'username' is never used
Parameter 'password' is never used
Method 'authenticateUser(java.lang.String, java.lang.String)' is never used
Parameter 'username' is never used
Parameter 'password' is never used */

package Memorygame.draft;

import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private DatabaseManager dbManager;
    private Scanner scanner;

    public User(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.scanner = new Scanner(System.in);
    }

    public void handleLoginOrRegistration() {
        // Code for handling login or registration logic
        // This method can be implemented based on your requirements
    }

    public void displayLoginOrRegistrationMenu() {
        // Code for displaying login or registration menu
        // This method can be implemented based on your requirements
    }

    public void startGame() {
        // Code for starting the game
        // This method can be implemented based on your requirements
    }

    public void enterUserDetails() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your code: ");
        String code = scanner.nextLine();

        System.out.print("Enter your birthdate (YYYY-MM-DD): ");
        String birthdate = scanner.nextLine();

        try {
            dbManager.saveUserDetails(name, code, birthdate);
            System.out.println("User details saved successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to save user details: " + e.getMessage());
        }
    }

    public void run() {
        dbManager.connect();

        displayLoginOrRegistrationMenu();
        handleLoginOrRegistration();

        startGame();

        System.out.println("Type C to continue or Q to quit");
        String userInput = scanner.nextLine();
        while (!userInput.equalsIgnoreCase("Q")) {
            startGame();
            System.out.println("Type C to continue or Q to quit");
            userInput = scanner.nextLine();
        }

        dbManager.disconnect();
        scanner.close();
    }
}

/* The ERRORS in this class are:

Field 'scanner' may be 'final'
Field 'dbManager' may be 'final'
Variable 'username' is never used
Variable 'password' is never used
Method 'startGame()' is never used
Method 'run()' is never used */


package Memorygame.draft;

public class Score {
        private DatabaseManager dbManager;

        public Score(DatabaseManager dbManager) {
            this.dbManager = dbManager;
        }

        public void saveScore(int userId, int score) {
            // Save the score to the database
            // ...
        }

        public void displayTopScores() {
            // Display top 10 scores from the database
            // ...
        }
    }

/* The Errors in this class are: 
Parameter 'dbManager' is never used
Variable is already assigned to this value
Variable 'dbmanager' is assigned to itself
Parameter 'userId' is never used
Parameter 'score' is never used
Value 'dbmanager' is always 'null'*/


package Memorygame;

public class Card {
    private String design;
    private boolean visible;
    private boolean matched;

    public Card(String design) {
        this.design = design;
        this.visible = false;
        this.matched = false;
    }

    public String getDesign() {
        return design;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }
}

/* The ERRORS in this class are:
Field 'design' may be 'final'
Method 'isMatched()' is never used */


package Memorygame;

import java.util.List;

public class CardDesign {
    public static final String[] DESIGN = {
            // Design for card value 1 (Apple)
            "\u001B[31m    .-\"\"\"-.\n" +
                    "   /       \\\n" +
                    "  |   (o)   |\n" +
                    "   \\   ^   /\n" +
                    "    '-...-' \u001B[0m",

            // Design for card value 2 (Banana)
            "\u001B[33m  .-\"\"--.\n" +
                    " /       \\\n" +
                    "|  .-. .- |\n" +
                    " \\ `   ` / \n" +
                    "  '-...-'  \u001B[0m",

            // Design for card value 3 (Cherry)
            "\u001B[31m   .--.\n" +
                    "  /_\\/_\\\n" +
                    " ////\\\\\n" +
                    " |\\_/|\n" +
                    "  'o'  \u001B[0m",

            // Design for card value 4 (Orange)
            "\u001B[33m   .-\"\"\"-.\n" +
                    "  /       \\\n" +
                    " |   _   _ |\n" +
                    "  \\  '_'  /\n" +
                    "   '-...-' \u001B[0m",

            // Design for card value 5 (Strawberry)
            "\u001B[31m   .--.\n" +
                    "  /    \\\n" +
                    " |  _   |\n" +
                    "  \\(_) / \n" +
                    "   '--'  \u001B[0m",

            // Design for card value 6 (Grapes)
            "\u001B[35m  .-\"\"--.\n" +
                    " /_     _\\\n" +
                    " |(_\\_/_)|\n" +
                    "  \\     /\n" +
                    "   '---'  \u001B[0m",

            // Design for card value 7 (Pineapple)
            "\u001B[33m   .-\"\"\"-.\n" +
                    "  /       \\\n" +
                    " |   (^)   |\n" +
                    "  \\   ^   /\n" +
                    "   '-...-' \u001B[0m",

            // Design for card value 8 (Watermelon)
            "\u001B[32m   .-\"\"\"-.\n" +
                    "  /       \\\n" +
                    " |   \\_/   |\n" +
                    "  \\       /\n" +
                    "   '-...-' \u001B[0m",

            // Design for card value 9 (Pear)
            "\u001B[33m   .--.\n" +
                    "  /    \\\n" +
                    " |  (@) |\n" +
                    "  \\  '-' \\\n" +
                    "   '--'   \u001B[0m",

            // Design for card value 10 (Lemon)
            "\u001B[33m   .--.\n" +
                    "  /    \\\n" +
                    " |  \\\\  |\n" +
                    "  \\_//  /\n" +
                    "   '--'  \u001B[0m"
    };

    public CardDesign(List<String> designs) {
    }

    public List<String> getDesigns() {
        return null;
    }
}

/* The ERRORS in this class are:
Field 'DESIGN' is never used
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Concatenation can be replaced with text block
Parameter 'designs' is never used */
