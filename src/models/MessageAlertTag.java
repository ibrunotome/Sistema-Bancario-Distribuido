package models;

/**
 * Enum to facility the return of alert messages
 *
 * @author Bruno Tomé
 * @author Cláudio Menezes
 * @since 04/07/2016
 */
public enum MessageAlertTag {
    LOGIN_SUCCESSFUL,
    LOGIN_ERROR,
    TRANSFER_SUCCESSFUL,
    TRANSFER_ERROR_ACCOUNT,
    TRANSFER_ERROR_AMOUNT,
    TRANSFER_ERROR_NEGATIVE,
    SIGNUP_SUCCESSFUL,
    SIGNUP_ERROR
}
