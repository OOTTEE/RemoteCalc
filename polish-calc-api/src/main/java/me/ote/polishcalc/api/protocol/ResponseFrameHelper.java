package me.ote.polishcalc.api.protocol;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ResponseFrameHelper {

    public static final byte[] FAIL_PAYLOAD = {'F', 'A', 'I', 'L'};
    public static final byte[] ERROR_PAYLOAD = {'E', 'R', 'R', 'O', 'R'};
    public static final byte[] BYE_PAYLOAD = {'B', 'Y', 'E'};
    public static final byte[] ACK = {0x06};
    public static final byte SEPARATOR = ';';
    public static final byte END = '$';
    public static final int FRAME_MIN_SIZE = 4;
    private static final int BYTE_MSG_ID = 0;
    private static final int BYTE_PAYLOAD = 3;

    public ResponseFrame createResponse(Integer messageId, byte[] payload) {
        return new ResponseFrame(messageId, payload);
    }

    public ResponseFrame createFailResponse(Integer messageId) {
        return new ResponseFrame(messageId, FAIL_PAYLOAD);
    }

    public ResponseFrame createErrorResponse(Integer messageId) {
        return new ResponseFrame(messageId, ERROR_PAYLOAD);
    }

    public ResponseFrame createHelloResponse(Integer messageId) {
        return new ResponseFrame(messageId, ACK);
    }

    public ResponseFrame createByeResponse(Integer messageId) {
        return new ResponseFrame(messageId, BYE_PAYLOAD);
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

    public ResponseFrame readFrame(byte[] rawFrame) throws FrameFormatException {
        if (rawFrame == null
                || rawFrame.length < FRAME_MIN_SIZE
                || rawFrame[2] != SEPARATOR
                || rawFrame[rawFrame.length - 1] != END) {
            throw new FrameFormatException();
        }
        final Integer msg_id = ((rawFrame[BYTE_MSG_ID] & 0XFF) << 8) + rawFrame[BYTE_MSG_ID + 1];
        final byte[] payload = Arrays.copyOfRange(rawFrame, BYTE_PAYLOAD, rawFrame.length-1);
        return new ResponseFrame(msg_id, payload);
    }
}
