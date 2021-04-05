package me.ote.polishcalc.api.protocol;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class RequestFrameHelperTest {

    public static final byte[] HELLO_REQ_FRAME = {0x00, 0x00, 0x3b, 0x00, 0x3b, 0x24};
    public static final byte[] OP_REQ_SUCCESS_FRAME = {0x0F, 0x01, 0x3b, 0x01, 0x3b, 0x31, 0x20, 0x32, 0x20, 0x33, 0x20, 0x2A, 0x20, 0x2B, 0x24};
    public static final byte[] BYE_REQ_SUCCESS_FRAME = {0x20, 0x00, 0x3b, 0x02, 0x3b, 0x24};

    public static final byte[] MISSING_OP_FRAME = {0x00, 0x00, 0x3b, 0x31, 0x20, 0x32, 0x20, 0x2B, 0x24};
    public static final byte[] MISSING_MSG_ID_FRAME = {0x3b, 0x01, 0x3b, 0x20, 0x32, 0x20, 0x2B, 0x24};
    public static final byte[] MISSING_NO_END_FRAME = {0x00, 0x01, 0x3b, 0x02, 0x3b, 0x31, 0x20, 0x32, 0x20, 0x33, 0x20, 0x2A, 0x20, 0x2B};
    public static final byte[] MISSING_NO_SEPARATOR_FRAMES = {0x00, 0x01, 0x02, 0x31, 0x20, 0x32, 0x20, 0x33, 0x20, 0x2A, 0x20, 0x2B, 0x24};

    public static final byte[] BAD_OPERATION_TYPE_FRAME = {0x0F, 0x01, 0x3b, 0x0f, 0x3b, 0x31, 0x20, 0x32, 0x20, 0x33, 0x20, 0x2A, 0x20, 0x2B, 0x24};

    @Test
    public void readMessageIdTest() throws FrameFormatException, FrameUnkonwnTypeException {
        RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
        RequestFrame helloFrame = requestFrameHelper.readFrame(HELLO_REQ_FRAME);
        RequestFrame opFrame = requestFrameHelper.readFrame(OP_REQ_SUCCESS_FRAME);
        RequestFrame byeFrame = requestFrameHelper.readFrame(BYE_REQ_SUCCESS_FRAME);
        Assertions.assertEquals(helloFrame.getMessageId(), 0);
        Assertions.assertEquals(opFrame.getMessageId(), 3841);
        Assertions.assertEquals(byeFrame.getMessageId(), 8192);
    }

    @Test
    public void readHelloFrame() throws FrameFormatException, FrameUnkonwnTypeException {
        RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
        RequestFrame requestFrame = requestFrameHelper.readFrame(HELLO_REQ_FRAME);
        Assertions.assertEquals(HelloFrame.class, requestFrame.getClass());
    }

    @Test
    public void readPayload() throws FrameFormatException, FrameUnkonwnTypeException {
        RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
        RequestFrame helloFrame = requestFrameHelper.readFrame(HELLO_REQ_FRAME);
        RequestFrame opFrame = requestFrameHelper.readFrame(OP_REQ_SUCCESS_FRAME);
        RequestFrame byeFrame = requestFrameHelper.readFrame(BYE_REQ_SUCCESS_FRAME);
        Assertions.assertArrayEquals(new byte[0], helloFrame.getPayload());
        Assertions.assertArrayEquals(new byte[] {0x31, 0x20, 0x32, 0x20, 0x33, 0x20, 0x2A, 0x20, 0x2B}, opFrame.getPayload());
        Assertions.assertArrayEquals(new byte[0], byeFrame.getPayload());
        Assertions.assertEquals("1 2 3 + *", ((OperationFrame)opFrame).getStringPayload());
    }

    @Test
    public void readOperation() throws FrameFormatException, FrameUnkonwnTypeException {
        RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
        RequestFrame helloFrame = requestFrameHelper.readFrame(HELLO_REQ_FRAME);
        RequestFrame opFrame = requestFrameHelper.readFrame(OP_REQ_SUCCESS_FRAME);
        RequestFrame byeFrame = requestFrameHelper.readFrame(BYE_REQ_SUCCESS_FRAME);
        Assertions.assertEquals(Operations.HELLO, helloFrame.getOperation());
        Assertions.assertEquals(Operations.OPERATION, opFrame.getOperation());
        Assertions.assertEquals(Operations.BYE, byeFrame.getOperation());
    }

    @Test
    public void badFormatExceptionTest() {
        RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
        Assertions.assertThrows(FrameFormatException.class, () -> requestFrameHelper.readFrame(MISSING_OP_FRAME));
        Assertions.assertThrows(FrameFormatException.class, () -> requestFrameHelper.readFrame(MISSING_MSG_ID_FRAME));
        Assertions.assertThrows(FrameFormatException.class, () -> requestFrameHelper.readFrame(MISSING_NO_END_FRAME));
        Assertions.assertThrows(FrameFormatException.class, () -> requestFrameHelper.readFrame(MISSING_NO_SEPARATOR_FRAMES));
    }

    @Test
    public void unknownOperationTypeException() {
        RequestFrameHelper requestFrameHelper = new RequestFrameHelper();
        Assertions.assertThrows(FrameUnkonwnTypeException.class, () -> requestFrameHelper.readFrame(BAD_OPERATION_TYPE_FRAME));
    }


}
