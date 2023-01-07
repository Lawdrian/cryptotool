package de.hswt.swa.cryptotool.data;

/**
 * Enum used for determining what gui action should trigger what method.
 */
public enum EventType {
        IMPORT_TEXT,
        SAVE_TEXT,
        LOCAL_ENCODE,
        EXTERNAL_ENCODE,
        SOCKET_ENCODE,
        RMI_ENCODE,
        IMPORT_CIPHER,
        SAVE_CIPHER,
        LOCAL_DECODE,
        EXTERNAL_DECODE,
        SOCKET_DECODE,
        RMI_DECODE,
        IMPORT_CRYPTO,
        SAVE_CRYPTO,
        RESET_FIELDS
}
