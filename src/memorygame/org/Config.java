package memorygame.org;

import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Connection;

/* NAME                       STUDENT NUMBER
CHANGOER REWINDRANATH          SE/1122/018
JULISHA A.M BUSGHIT            SE/1122/016
AALIYA GHOGLI                  SE/1122/028
FARINA HOEPEL                  SE/1122/032
*/


public class Config {
    static String DB_URL = "jdbc:mysql://localhost:3306/MG";
    // this provides the link to the database
    static String USER = "root";
    // Insert the name with which is logged in to MySQL
    static String PASS = "Sanika2003@#";
    // password van de user account in MySQL
    static String Query = "select * from Guest";

    public static void main(String[] args) {
        boolean hit = true; // als dit statement true is dan gaat het de volgende code uitvoeren
        Connection connection = null;

        Scanner scan = new Scanner(System.in);// used to make new accounts
        // Using this the data will be inserted while making a new account


        while (hit = true)
            try {
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                // Using this statement there will be a connection made with the database

                System.out.println("Successfully connected to database MG");
                // Shows that the connection has been made successfully
                System.out.println("Create account");
                // Making a new account

                System.out.println("First Name :");// Shows what the person using it will see on the page
                String first_name = scan.nextLine();
                // direct to where you want the data to be inserted in the database

                System.out.println("Last Name :");
                String last_name = scan.nextLine();

                System.out.println("Password :");
                String guest_code = scan.nextLine();

                System.out.println("Age :");
                String age = scan.nextLine();

                System.out.println("Date of Birth :");
                String geboorte_datum = scan.nextLine();

                String instertData = "INSERT INTO guest(first_name, last_name, guest_code, age, geboorte_datum)" +
                        "VALUES(?,?,?,?,?)";
                // Shows the inserted values and which values will be inserted

                PreparedStatement preparedStatement = connection.prepareStatement(instertData);
                preparedStatement.setString(1, first_name);
                preparedStatement.setString(2, last_name);
                preparedStatement.setString(3, guest_code);
                preparedStatement.setString(4, age);
                preparedStatement.setString(5, geboorte_datum);
                // this statement is used to secure the data, so it can not be changed by an outsider
                // And it also shows in what order the data should be inserted

                int rowsAffected = preparedStatement.executeUpdate();
                // this will execute all statements

                if (rowsAffected > 0) {
                    System.out.println("UPDATE Successful");
                    System.out.println("Welcome to MG");
                }// iff all the rows are inserted correctly it will be successful
                else {
                    System.out.println("UPDATE Failed");
                }// if there is a mistake while inserting the execution will fail

                System.out.println("Type X to Quit MG");// this statement is used to stop the loop of the code
                if (scan.nextLine().equalsIgnoreCase("X")) {
                    hit = false;// when x is typed in then the statement hit will show false, and stop the loop
                    System.out.println("Thank You for using MG");
                }
                preparedStatement.close();

            } catch (SQLException e) { // is used to make exceptions when the code is running
                e.printStackTrace();

            } finally {
                scan.close();
            }
    }
}