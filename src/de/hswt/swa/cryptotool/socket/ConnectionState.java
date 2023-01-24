package de.hswt.swa.cryptotool.socket;

/**
 * @author AdrianWild
 * @version 1.0
 * This enum stores all messages that the socket client and socket server
 * use to sync their connection and communicate with each other.
 */
public enum ConnectionState {
    CLIENT_CONNECTION_REQUEST,
    CLIENT_CONNECTION_CLOSE,
    CLIENT_ENCRYPT_REQUEST,
    CLIENT_DECRYPT_REQUEST,
    CLIENT_PLAIN_TEXT_DONE,
    CLIENT_PASSWORD_DONE,
    CLIENT_CIPHER_DONE,
    SERVER_CONNECTION_ACCEPT,
    SERVER_CONNECTION_CLOSE,
    SERVER_ENCRYPT_ACCEPT,
    SERVER_ENCRYPT_SUCCESS,
    SERVER_ENCRYPT_FAILURE,
    SERVER_PLAIN_TEXT_REQUEST,
    SERVER_PLAIN_TEXT_DONE,
    SERVER_PASSWORD_REQUEST,
    SERVER_CIPHER_REQUEST,
    SERVER_DECRYPT_ACCEPT,
    SERVER_DECRYPT_SUCCESS,
    SERVER_DECRYPT_FAILURE,
}
