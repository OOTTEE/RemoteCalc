package me.ote.polishcalc.api.protocol;

public class HelloFrame extends RequestFrame {

    HelloFrame(Integer messageId, Integer operation, byte[] payload) {
        super(messageId, operation, payload);
    }

    public static HelloFrame create(Integer messageId) {
        return new HelloFrame(messageId, Operations.HELLO, new byte[0]);
    }

    public static HelloFrame create() {
        return create(0);
    }
}
