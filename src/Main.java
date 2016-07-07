import views.UserScreen;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 03/07/2016
 */
public class Main {
    public static void main(String args[]) throws Exception {
        UserScreen screen = new UserScreen();
        screen.prepareGUI();
        screen.login();

        /**
         *
         * Anotações pra uso futuro
         *
         * // enviar mensagem com objeto serializado
         * // http://www.jgroups.org/manual/html/user-channel.html
         * public void send(Address dst, Serializable obj) throws Exception;
         */
    }
}