package com.wego.carpark.exceptions;

import java.util.List;

public class ImportValidationException extends RuntimeException {
    private final List<String> missingHeaders;
    private final List<String> receivedHeaders;

    public ImportValidationException(String message, List<String> missingHeaders, List<String> receivedHeaders) {
        super(message);
        this.missingHeaders = missingHeaders;
        this.receivedHeaders = receivedHeaders;
    }
    public List<String> getMissingHeaders() { return missingHeaders; }
    public List<String> getReceivedHeaders() { return receivedHeaders; }
}
