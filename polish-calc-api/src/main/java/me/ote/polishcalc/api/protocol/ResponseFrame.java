package me.ote.polishcalc.api.protocol;

public class ResponseFrame {
    private final Integer messageId;
    private final byte[] payload;

    public ResponseFrame(Integer messageId, byte[] payload) {
        this.messageId = messageId;
        this.payload = payload;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public byte[] getPayload() {
        return payload;
    }

}
