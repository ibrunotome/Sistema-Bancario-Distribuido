package controllers;

import models.MessageAlertTag;

import java.io.Serializable;

/**
 * Class to make the toString for MessageAlertTags
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 04/07/2016
 */
public class MessageAlert implements Serializable {

    public static String toString(MessageAlertTag m) {

        switch (m) {
            case LOGIN_SUCCESSFUL:
                return "\nLogin efetuado com sucesso\n";
            case LOGIN_ERROR:
                return "ERRO: Conta ou senha inválida";
            case TRANSFER_SUCCESSFUL:
                return "Transferência realizada com sucesso";
            case TRANSFER_ERROR_NEGATIVE:
                return "ERRO: Valor de transferência deve ser positivo";
            case TRANSFER_ERROR_SAME_ACCOUNT:
                return "ERRO: Você não pode transferir para sua própria conta";
            case TRANSFER_ERROR_AMOUNT:
                return "ERRO: Saldo insuficiente";
            case TRANSFER_ERROR_ACCOUNT:
                return "ERRO: Conta inexistente";
            case UNKNOWN_ERROR:
                return "ERRO: Não foi possível enviar a última mensagem";
            case UNAVAILABLE_BANK:
                return "Transferência indisponível no momento";
            case SIGNUP_SUCCESSFUL:
                return "Cadastrado com sucesso";
            case SIGNUP_ERROR:
                return "ERRO: Conta já existente";
            default:
                return "Erro desconhecido :(";
        }
    }
}
