package org.openhab.binding.evohome.internal.api;

public class EvohomeApiClientException extends Exception {

    public EvohomeApiClientException() {
    }

    public EvohomeApiClientException(String message) {
        super(message);
    }

    public EvohomeApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
