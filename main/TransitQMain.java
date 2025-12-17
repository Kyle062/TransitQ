    package main;

    import ui.LoginForm;
    import ui.TransitQGUI;

    public class TransitQMain {
        public static void main(String[] args) {
                TransitQGUI mainApp = new TransitQGUI();
                mainApp.setVisible(false);
        
                LoginForm loginForm = new LoginForm(mainApp);
                loginForm.setVisible(true);
        
        }
    }   