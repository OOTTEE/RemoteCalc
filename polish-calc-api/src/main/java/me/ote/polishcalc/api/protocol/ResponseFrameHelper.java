package me.ote.polishcalc.api.protocol;

import java.nio.charset.StandardCharsets;

public class ResponseFrameHelper {

    public static final byte[] FAIL_PAYLOAD = {'F', 'A', 'I', 'L'};
    public static final byte[] ERROR_PAYLOAD = {'E', 'R', 'R', 'O', 'R'};
    public static final byte SEPARATOR = ';';
    public static final byte END = '$';

    public ResponseFrame createResponse(Integer messageId, String payload) {
        return new ResponseFrame(messageId, payload.getBytes(StandardCharsets.UTF_8));
    }

    public ResponseFrame createFailResponse(Integer messageId) {
        return new ResponseFrame(messageId, FAIL_PAYLOAD);
    }

    public ResponseFrame createErrorResponse(Integer messageId) {
        return new ResponseFrame(messageId, ERROR_PAYLOAD);
    }

    public byte[] buildFrame(ResponseFrame responseFrame) {
        byte[] rawFrame = new byte[4 + responseFrame.getPayload().length];
        rawFrame[0] = (byte) ((responseFrame.getMessageId() >> 8 ) & 0xFF);
        rawFrame[1] = (byte) (responseFrame.getMessageId() & 0xFF);
        rawFrame[2] = SEPARATOR;
        if(responseFrame.getPayload() != null) {
            for (int i = 0; i < responseFrame.getPayload().length; i++) {
                rawFrame[3 + i] = responseFrame.getPayload()[i];
            }
        }
        rawFrame[rawFrame.length-1] = END;
        return rawFrame;
    }

}