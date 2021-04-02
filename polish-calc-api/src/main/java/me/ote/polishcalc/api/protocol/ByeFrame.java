package me.ote.polishcalc.api.protocol;

public class ByeFrame extends RequestFrame{
    public ByeFrame(Integer messageId, Integer operation, byte[] payload) {
        super(messageId, operation, payload);
    }
}
