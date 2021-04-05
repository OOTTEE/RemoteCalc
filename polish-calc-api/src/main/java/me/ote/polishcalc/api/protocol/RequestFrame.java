package me.ote.polishcalc.api.protocol;

import java.util.Arrays;

public class RequestFrame {
    public static final byte SEPARATOR = 0x3B;
    public static final byte END = 0x24;
    public static final int BYTE_MSG_ID = 0;
    public static final int BYTE_OPERATION = 3;
    public static final int BYTE_PAYLOAD = 5;

    private final Integer messageId;
    private final Integer operation;
    private final byte[] payload;

    public RequestFrame(Integer messageId, Integer operation, byte[] payload) {
        this.messageId = messageId;
        this.operation = operation;
        this.payload = payload;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public Integer getOperation() {
        return operation;
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestFrame that = (RequestFrame) o;

        if (messageId != null ? !messageId.equals(that.messageId) : that.messageId != null) return false;
        if (operation != null ? !operation.equals(that.operation) : that.operation != null) return false;
        return Arrays.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        int result = messageId != null ? messageId.hashCode() : 0;
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
