package me.ote.polishcalc.api.protocol;

public class HelloFrame extends RequestFrame {
    public HelloFrame(Integer messageId, Integer operation, byte[] payload) {
        super(messageId, operation, payload);
    }
}
