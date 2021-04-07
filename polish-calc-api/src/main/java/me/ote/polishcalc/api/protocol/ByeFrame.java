package me.ote.polishcalc.api.protocol;

public class ByeFrame extends RequestFrame{
    ByeFrame(Integer messageId, Integer operation, byte[] payload) {
        super(messageId, operation, payload);
    }

    public static ByeFrame create(Integer messageId) {
        return new ByeFrame(messageId, Operations.BYE, new byte[0]);
    }

}
