package me.ote.polishcalc.api.protocol;

import java.nio.charset.StandardCharsets;

public class OperationFrame extends RequestFrame {
    public OperationFrame(Integer messageId, Integer operation, byte[] payload) {
        super(messageId, operation, payload);
    }

    public String getStringPayload() {
        return new String(super.getPayload(), StandardCharsets.UTF_8);
    }

}
