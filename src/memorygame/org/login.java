package memorygame.org;

import java.sql.*;
import java.util.Scanner;
import java.util.jar.Manifest;

import memorygame.org.Main;

public class login {
    public static void main(String[] args) {
        String FirstName = "";
        String LastName  = "";
        String password  = "";
        String database  = "MG";
        Scanner scan = new Scanner(System.in);
// if the person tries to log in then the server will try this code to help the person log in to the game
        try {
            Connection conn = null;
// the server makes a database connection using this code
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MG","root","Sanika2003@#");
            System.out.println("Connected to the database");
// user has to enter the following
            System.out.print("insert First Name: ");
            String first_name = scan.nextLine();

            System.out.print("insert Last Name: ");
            String last_name = scan.nextLine();

            System.out.print("insert Password: ");
            String guest_code = scan.nextLine();
// the server selects the entered First Name, Last Name and password from the database table guest and leads the user to the game
            String query = "SELECT * FROM guest" +" WHERE first_name = ? " + "AND last_name = ?" + "AND guest_code = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,first_name);
            preparedStatement.setString(2,last_name);
            preparedStatement.setString(3,guest_code);

// the server executes the prepared query
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){// if the login has been successful then the following will happen
                System.out.println("Login Successful");
                int guest_id = rs.getInt("guest_id");
                System.out.println();
//the game will start

                // if the user want to change their name or password the server will use the following to help the user change data
                // the user can change name by pressing Y or N to keep going
                System.out.print("Change Name (Y/N): ");
                String changeName = scan.nextLine();

                if (changeName.equalsIgnoreCase("Y")) {// the user can change name by pressing Y or y
// user enters new data
                    System.out.print("Enter New First Name: ");
                    String newFirstName = scan.nextLine();

                    System.out.print("Enter New Last Name: ");
                    String newLastName = scan.nextLine();

                    // Update the first_name and last_name in the database
                    String updateQuery = "UPDATE guest SET first_name = ?, last_name = ? WHERE guest_id = ?";
                    PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                    updateStatement.setString(1, newFirstName);
                    updateStatement.setString(2, newLastName);
                    updateStatement.setInt(3, guest_id);
                    int rowsUpdated = updateStatement.executeUpdate();

// and if it is successfully updated it will show
                    if (rowsUpdated > 0) {
                        System.out.println("Name updated successfully.");
// once done the game will start
                    } else if(!changeName.equalsIgnoreCase("N")) {
                        System.out.println("Failed to update name, continuing.....");

                    }
                }

                //user can change password by pressing Y or N to continue to game
                System.out.print("Change Password (K/N): ");
                String changePassword = scan.nextLine();

                if (changePassword.equalsIgnoreCase("K")) {
                    System.out.print("Enter New Password: ");
                    String newGuest_code = scan.nextLine();

                    // Update the password in the database
                    String updateQuery = "UPDATE guest SET guest_code = ? WHERE guest_id = ?";
                    PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                    updateStatement.setString(1, newGuest_code);
                    updateStatement.setInt(2, guest_id);
                    int rowsUpdated = updateStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                        System.out.println("Password updated successfully.");
// once done the game will start

                    } else if (!changePassword.equalsIgnoreCase("N")){
                        System.out.println("Failed to update password, continuing to game......");
                    }
                }
// the person can press n to continue without changing anything or y and k to
                if (changeName.equalsIgnoreCase("N") || changePassword.equalsIgnoreCase("N") ||
                        changeName.equalsIgnoreCase("Y") || changePassword.equalsIgnoreCase("K")){

                    Main main = new Main(guest_id);
                    Main.main(null);
                }
            }

            // if the user enters a wrong name or password the server will show the following
            else {
                System.out.println("Invalid First Name, Last Name or Password.");
            }
            conn.close();

        }catch (SQLException e){// is to make sure the code runs even if it is with an exception
            e.printStackTrace();
        }
    }
}

