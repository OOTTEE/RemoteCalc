package me.ote.polishcalc.api.protocol;

import java.util.Arrays;

import static me.ote.polishcalc.api.protocol.Operations.*;
import static me.ote.polishcalc.api.protocol.Operations.HELLO;

public class RequestFrameHelper {
    public static final int FRAME_MIN_SIZE = 6;

    public RequestFrameHelper() {
    }

    public RequestFrame readFrame(byte[] rawFrame) throws FrameFormatException, FrameUnkonwnTypeException {
        if (rawFrame == null
                || rawFrame.length < FRAME_MIN_SIZE
                || rawFrame[2] != RequestFrame.SEPARATOR
                || rawFrame[4] != RequestFrame.SEPARATOR
                || rawFrame[rawFrame.length - 1] != RequestFrame.END) {
            throw new FrameFormatException();
        }
        final Integer msg_id = ((rawFrame[RequestFrame.BYTE_MSG_ID] & 0XFF) << 8) + rawFrame[RequestFrame.BYTE_MSG_ID + 1];
        final Integer operation = rawFrame[RequestFrame.BYTE_OPERATION] & 0xFF;
        final byte[] payload = Arrays.copyOfRange(rawFrame, RequestFrame.BYTE_PAYLOAD, rawFrame.length-1);
        switch (operation) {
            case HELLO:
                return new HelloFrame(msg_id, operation, payload);
            case OPERATION:
                return new OperationFrame(msg_id, operation, payload);
            case BYE:
                return new ByeFrame(msg_id, operation, payload);
        }
        throw new FrameUnkonwnTypeException();
    }

}
