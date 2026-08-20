package io.lemonjuice.flan_mai_plugin.exception;

import lombok.Getter;

@Getter
public class DivingFishException extends RuntimeException {
    private final String error;

    public DivingFishException(String error) {
        this.error = error;
    }

    public DivingFishException() {
        this.error = "unknown";
    }

    public static class Unbound extends DivingFishException {
        public Unbound(String error) {
            super(error);
        }

        public Unbound() {
            super("consent_required");
        }
    }
}
