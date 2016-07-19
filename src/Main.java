import controllers.ServerBank;
import views.UserScreen;

/**
 * Created by iBrunoTome on 7/19/16.
 */
public class Main {
    public static void main(String args[]) throws Exception {
        ServerBank s1 = new ServerBank();
        Thread.sleep(2000);
        ServerBank s2 = new ServerBank();
        Thread.sleep(2000);
        UserScreen s = new UserScreen();
    }
}
