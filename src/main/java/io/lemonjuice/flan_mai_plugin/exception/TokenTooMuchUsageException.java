package io.lemonjuice.flan_mai_plugin.exception;

public class TokenTooMuchUsageException extends RuntimeException {
    public TokenTooMuchUsageException(String message) {
        super(message);
    }
}
