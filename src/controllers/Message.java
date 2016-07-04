package controllers;

import models.MessageTag;

/**
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 04/07/2016
 */
public class Message {

    public static String toString(MessageTag m) {

        switch (m) {
            case LOGIN_SUCCESSFUL:
                return "Login efetuado com sucesso";
            case LOGIN_ERROR:
                return "Conta ou senha inválida";
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
