import views.UserScreen;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class Main {
    public static void main(String args[]) {
        UserScreen screen = new UserScreen();
        screen.prepareGUI();
        screen.login();
        //screen.login();
    }
}