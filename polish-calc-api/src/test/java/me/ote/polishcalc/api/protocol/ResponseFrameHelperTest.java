package me.ote.polishcalc.api.protocol;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ResponseFrameHelperTest {

    public static final byte[] RES_FRAME = {0x00, 0x0A, 0x3B, 'T', 'E', 'S', 'T', '_', 'V', 'A', 'L', 'U', 'E', '$'};
    public static final byte[] FAIL_FRAME = {0x00, (byte) 0xFA, 0x3B, 'F', 'A', 'I', 'L', '$'};
    public static final byte[] ERROR_FRAME = {0x04, (byte) 0xE2, 0x3B, 'E', 'R', 'R', 'O', 'R', '$'};
    public static final byte[] EMPTY_FRAME = {(byte) 0xFF, (byte) 0xFF, 0x3B, '$'};

    @Test
    public void createResponseFrame(){
        ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
        ResponseFrame responseFrame = responseFrameHelper.createResponse(10, "TEST_VALUE");
        byte[] rawFrame = responseFrameHelper.buildFrame(responseFrame);
        Assertions.assertArrayEquals(RES_FRAME, rawFrame);
    }

    @Test
    public void createFailResponseFrame(){
        ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
        ResponseFrame responseFrame = responseFrameHelper.createFailResponse(250);
        byte[] rawFrame = responseFrameHelper.buildFrame(responseFrame);
        Assertions.assertArrayEquals(FAIL_FRAME, rawFrame);
    }

    @Test
    public void createErrorResponseFrame(){
        ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
        ResponseFrame responseFrame = responseFrameHelper.createErrorResponse(1250);
        byte[] rawFrame = responseFrameHelper.buildFrame(responseFrame);
        Assertions.assertArrayEquals(ERROR_FRAME, rawFrame);
    }

    @Test
    public void createEmptyFrame(){
        ResponseFrameHelper responseFrameHelper = new ResponseFrameHelper();
        ResponseFrame responseFrame = responseFrameHelper.createResponse(65535, "");
        byte[] rawFrame = responseFrameHelper.buildFrame(responseFrame);
        Assertions.assertArrayEquals(EMPTY_FRAME, rawFrame);
    }


}
