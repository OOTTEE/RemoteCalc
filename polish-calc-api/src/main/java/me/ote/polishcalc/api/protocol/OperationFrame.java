package me.ote.polishcalc.api.protocol;

public class OperationFrame extends RequestFrame {
    public OperationFrame(Integer messageId, Integer operation, byte[] payload) {
        super(messageId, operation, payload);
    }
}
