package memorygame.org;

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.sql.*;

public class Main {
    // these are the instances that are created so the class can be accessed from the login class
    public static int guest_id;
    public static String[][] Board;
    public static String[][] cards;
    public static Scanner scanner;
    public static int guest_scores= 0;
    public static int played_games;
    public static int incorrectMoves = 0;

    // the following code will be used to access the main class
    public Main(int guest_id){
        this.guest_id = guest_id;
        this. Board = new String[4][5];// this array is used to give the rows and columns
        this. cards = new String[4][5];// this array is used to put cards in the game
        this.scanner = new Scanner(System.in);
        this.guest_scores = 0;
        this.played_games = 0;
    }

    public static void printBoard() {
        // this method will be used to create a wall that will separate each letter that will be hidden from each other
        for (int i = 0; i < 4; i++) {// this statement is used to initiate the rows
            System.out.print("I");// this will be used to separate the cards from each other, so it is easily recognisable
            for (int j = 0; j < 5; j++) {// will be used to initiate columns
                System.out.print(Board[i][j]);// this will show both the statements together
                System.out.print("I");
            }
            System.out.println();// used for an empty row
        }
    }

    public static boolean gameOver() {// this method is used to show the cards as an underscore in column and rows
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if (Board[i][j].equals("_")) {// this is used so the player won't know where which letter is
                    return false;// this returns a boolean that is true
                }
            }
        }
        return true;// returns a boolean value that is false
    }

    public static void main(String[] args) {// this is the main of the game which is used to operate the game in the
        // right order

        Main memoryGame = new Main(guest_id);// used to create a new game when started

        while (true) {
            System.out.println("Press g for New Game, n to see the top 10 scores or q to quit");
            // this will be visible to the user from which he can choose
            String gnq = scanner.nextLine();// is used to scan from the current position till it finds a line separator
            // in this case the closing curly brackets
            if (gnq.equals("q")) {// if the user uses q the game will shut down
                System.out.println("Game Over.....");
                System.out.println("Shutting Down.....");
                System.out.println("THANK YOU FOR PLAYING MEMORY GAME");
                break;
            } else if (gnq.equals("g")) {// if the user uses g the game will start

                cardShuffle();// the first step is that the cards will be shuffled, all the 20 cards in 4 rows and 5 columns
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 5; j++) {
                        Board[i][j] = "_";// after shuffling the cards will be hidden with an underscore
                    }
                }
                displayCards();// this is used to display the cards with the matching positions before the game starts
                System.out.println();// empty line

                clearConsole();// this is used to clear the console before the user starts to match so the positions
                // won't be compromised
                System.out.println();

                printBoard();// after every match the unmatched cards will be hidden again
                checkInserted(cards, guest_scores, played_games,  guest_id);// used to match and display the scores
                System.out.println("Final Score: " + guest_scores);// after every correct and incorrect match and at the
                // end of the game the score will be displayed
                break;
            }

            else if (gnq.equals("n")){// if the user chooses n the server will display the top 10 scores
                retrieveTopScores();
            }
            else {
                System.out.println("Character not found");// if the user enters a wrong character that is not int the
                // cards the server will show this

            }
        }
    }


    private static void displayCards(){// this method is used to show the cards before the game
        for  (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(cards[i][j] + " ");
            }
            System.out.println();
        }

        try {
            Thread.sleep(5000); // this will show the cards for 10 seconds and then flip the cards
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                Board[i][j] = "_"; // this code is for hiding the cards again
            }
        }

        printBoard(); // this will show the new board with the hidden cards

    }

    static void clearConsole() {// this method is used to clear the console which means that the user won't see the
        // positions of the cards after it has been flipped
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            System.out.println("Error clearing console screen: " + e.getMessage());
        }
    }


    private static void updateScore(int guest_scores, int played_games, int guest_id) {
        // this method is used to update the scores to the database
        try {// there will be a connection made first with the database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MG", "root", "Sanika2003@#");

            // Checks if the guest_id exists in the guest table
            String checkQuery = "SELECT guest_id FROM guest WHERE guest_id = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkQuery);
            checkStatement.setInt(1, guest_id);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // if the guest_id is found then the guest_scores and played_games will be updated
                String updateQuery = "UPDATE game SET guest_scores = ?, played_games = ? WHERE guest_id = ?";

                PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                updateStatement.setInt(1, guest_scores);
                updateStatement.setInt(2, played_games);
                updateStatement.setInt(3, guest_id);

                int rowsUpdated = updateStatement.executeUpdate();
                // once done the data will be updated to the database
                if (rowsUpdated > 0) {
                    System.out.println("Score updated successfully!");// shows if the data has been updated successfully
                    conn.commit();// used to commit the scores before updating
                }
            } else {
                // this code is just in case the system doesn't find the guest_id that is needed, then it will create a new row
                String insertQuery = "INSERT INTO game (guest_id, guest_scores, played_games) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                insertStatement.setInt(1, guest_id);
                insertStatement.setInt(2, guest_scores);
                insertStatement.setInt(3, played_games);
                int rowsInserted = insertStatement.executeUpdate();

                if (rowsInserted > 0) {
                    System.out.println("Score inserted successfully!");
                    conn.commit();
                }
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void retrieveTopScores() {// this method is used to retrieve the top 10 scores
        try {// the database connection is made first
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MG", "root", "Sanika2003@#");
            Statement statement = conn.createStatement();// there is a new statement created

            // Query to retrieve the top 10 scores
            String query = "SELECT guest_id, guest_scores FROM game ORDER BY guest_scores DESC LIMIT 10";
            // selects the highest scores from the database to the max limit of 10
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("Top 10 Scores:");// the user sees this before the scores are updated
            System.out.println("---------------");
            while (resultSet.next()) {
                int guestId = resultSet.getInt("guest_id");
                int score = resultSet.getInt("guest_scores");
                System.out.println("Guest ID: " + guestId + ", Score: " + score);
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkInserted(String[][] cards, int guest_scores,int played_games, int guest_id) {
        // this method is used to create the game itself and how it operates
        while (true) {
            if (!gameOver()) {
                System.out.println("Row: 1-4 (or q to quit)");//the user will have the option to choose the rows or quit
                String rowInput = scanner.nextLine();
                if (rowInput.equals("q")) {// if q is chosen the game will shut down
                    System.out.println("Quitting the game...");// the user sees that the game is shutting down
                    break;
                }
                int row1 = Integer.parseInt(rowInput);// is used to choose the rows
                System.out.println("Column: (1-5) (or q to quit)");// user presses a number between 1 and 5 or q
                String columnInput = scanner.nextLine();
                if (columnInput.equals("q")) {
                    System.out.println("Quitting the game...");
                }
                int column1 = Integer.parseInt(columnInput);

                if (!Board[row1 - 1][column1 - 1].equals("_")) {// if the same numbers are entered the server will
                    // show that the numbers are already entered
                    System.out.println("Already Inserted!!");
                    System.out.println();
                    printBoard();// the chosen cards will go back to how they were
                    continue;// continues the game
                } else {
                    Board[row1 - 1][column1 - 1] = " " + cards[row1 - 1][column1 - 1] + " ";
                    printBoard();
                }

                System.out.println("Row: 1-4 (or q to quit)");
                rowInput = scanner.nextLine();
                if (rowInput.equals("q")) {
                    System.out.println("Quitting the game...");
                    break;
                }// the same for the second row
                int row2 = Integer.parseInt(rowInput);
                System.out.println("Column: (1-5) (or q to quit)");
                columnInput = scanner.nextLine();
                if (columnInput.equals("q")) {
                    System.out.println("Quitting the game...");
                    break;
                }
                int column2 = Integer.parseInt(columnInput);

                if (!Board[row2 - 1][column2 - 1].equals("_")) {
                    System.out.println("Already Inserted!!");
                    Board[row1 - 1][column1 - 1] = "_";
                    System.out.println();
                    printBoard();
                    continue;
                } else {
                    Board[row2 - 1][column2 - 1] = " " + cards[row2 - 1][column2 - 1] + " ";
                    printBoard();
                    try {
                        Thread.sleep(2000); // Pause for 2 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Board[row1 - 1][column1 - 1] = "_";
                    Board[row2 - 1][column2 - 1] = "_";


                    if (!Board[row1 - 1][column1 - 1].equals(Board[row2 - 1][column2 - 1])) {
                        // when the chosen cards are a no match the server will show incorrect
                        System.out.println("Incorrect!!");
                        guest_scores--;// the scores will be shown in minus
                        incorrectMoves ++;// player can still keep playing
                        printBoard();// after incorrect match the cards will go back to normal
                        updateScore(guest_scores, played_games, guest_id);// scores will be updated

                        if (incorrectMoves >=3){// if the user makes 3 mismatches the game will close after showing
                            // the final score which is total points minus 3 and the multiplied by 3
                            guest_scores -= 3;
                            guest_scores *= 3;
                            System.out.println("You have made 3 incorrect moves. Your total points have decreased and multiplied by 3.");
                            System.out.println("Final Score: " + guest_scores);
                            updateScore( guest_scores, played_games, guest_id);// scores will be updated
                            break;
                        }
                        updateScore( guest_scores, played_games, guest_id);


                    } else {
                        printBoard();
                        System.out.println("Correct!!");// if the user chooses matching cards
                        guest_scores += 2;// the player gets 2 points
                        printBoard();// the matching cards stay open
                        updateScore(guest_scores, played_games, guest_id);// scores get updated

                    }
                }
            } else {
                System.out.println("Game Over!!");// if the user chooses to quit the game then the game will shut down
                System.out.println("Thank You For Playing Memory Game");
                break;
            }
            System.out.println("Final Score: " + guest_scores);// it shows the final score in the end
            updateScore(guest_scores, played_games, guest_id);// scores get updated

        }
    }

    private static void cardShuffle() {// this method is used to insert the cards and to shuffle them
        Random random = new Random();// used to generate random numbers on which the cards will be
        ArrayList<String> letters = new ArrayList<String>();// new arraylist and all the cards are added here
        letters.add("A");
        letters.add("B");
        letters.add("C");
        letters.add("D");
        letters.add("E");
        letters.add("F");
        letters.add("G");
        letters.add("H");
        letters.add("K");
        letters.add("P");
        letters.add("A");
        letters.add("B");
        letters.add("C");
        letters.add("D");
        letters.add("E");
        letters.add("F");
        letters.add("G");
        letters.add("H");
        letters.add("K");
        letters.add("P");

        int Index;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                Index = random.nextInt(letters.size());// chooses random index
                cards[i][j] = letters.get(Index);
                letters.remove(Index);
            }
        }
    }
}




