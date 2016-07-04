package controllers;

import models.MessageAlertTag;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 04/07/2016
 */
public class MessageAlert {

    public static String toString(MessageAlertTag m) {

        switch (m) {
            case LOGIN_SUCCESSFUL:
                return "\nLogin efetuado com sucesso\n";
            case LOGIN_ERROR:
                return "ERRO: Conta ou senha inválida";
            case TRANSFER_SUCCESSFUL:
                return "Transferência realizada com sucesso";
            case TRANSFER_ERROR_AMOUNT:
                return "ERRO: Saldo insuficiente";
            case TRANSFER_ERROR_ACCOUNT:
                return "ERRO: Conta inexistente";
            default:
                return "Erro desconhecido :(";
        }
    }
}
