package de.hswt.swa.cryptotool.data;

/**
 * Enum used for determining what gui action should trigger what method.
 */
public enum EventType {
        IMPORT_TEXT,
        SAVE_TEXT,
        LOCAL_ENCRYPT,
        EXTERNAL_ENCRYPT,
        SOCKET_ENCRYPT,
        RMI_ENCRYPT,
        API_ENCRYPT,
        IMPORT_CIPHER,
        SAVE_CIPHER,
        LOCAL_DECRYPT,
        EXTERNAL_DECRYPT,
        SOCKET_DECRYPT,
        RMI_DECRYPT,
        API_DECRYPT,
        IMPORT_CRYPTO,
        SAVE_CRYPTO,
        RESET_FIELDS
}
