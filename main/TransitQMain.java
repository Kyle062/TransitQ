package main;

import ui.LoginForm;
import ui.TransitQGUI;

public class TransitQMain {
    public static void main(String[] args) {
        // Start with the login form
       
            TransitQGUI mainApp = new TransitQGUI();
            // Hide the main app initially
            mainApp.setVisible(false);

            // Show login form with reference to main app
            LoginForm loginForm = new LoginForm(mainApp);
            loginForm.setVisible(true);
       
    }
}