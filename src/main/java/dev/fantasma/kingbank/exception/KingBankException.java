package dev.fantasma.kingbank.exception;

public class KingBankException extends Exception {
    public KingBankException(String message) {
        super(message);
    }

    public KingBankException(String message, Throwable cause) {
        super(message, cause);
    }

    public KingBankException(Throwable cause) {
        super(cause);
    }

    public KingBankException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {}
}
