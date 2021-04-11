package me.ote.polishcalc.api.protocol;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@QuarkusTest
class PayloadAdapterTest {

    @Inject
    PayloadAdapter payloadAdapter;

    @Test
    public void testCharToNibble1() throws PayloadFormatException {
        String op = "1 2 3 + *";
        byte[] result = {0x1E, 0x2E, 0x3E, (byte)0xAE, (byte)0xCE};
        Assertions.assertArrayEquals(result, payloadAdapter.compress(op));
    }

    @Test
    public void testCharToNibble2() throws PayloadFormatException {
        String op = "11 2 3 + *";
        byte[] result = {0x11, (byte) 0xE2,(byte)  0xE3,(byte)  0xEA,(byte)  0xEC};
        Assertions.assertArrayEquals(result, payloadAdapter.compress(op));
    }

    @Test
    public void testCharToNibble3() throws PayloadFormatException {
        String op = "1 2 + 5 3 / *";
        byte[] result = {0x1E, 0x2E, (byte)0xAE, 0x5E, 0x3E, (byte)0xDE, (byte)0xCE};
        Assertions.assertArrayEquals(result, payloadAdapter.compress(op));
    }

    @Test
    public void testNibbleToChar1() throws PayloadFormatException {
        byte[] op = {0x1E, 0x2E, 0x3E, (byte)0xAE, (byte)0xCE};
        String result = "1 2 3 + *";
        Assertions.assertEquals(result, payloadAdapter.uncompress(op));
    }

    @Test
    public void testNibbleToChar2() throws PayloadFormatException {
        byte[] op = {0x11, (byte) 0xE2, (byte)  0xE3, (byte) 0xEA, (byte) 0xEC};
        String result = "11 2 3 + *";
        Assertions.assertEquals(result, payloadAdapter.uncompress(op));
    }

    @Test
    public void testNibbleToChar3() throws PayloadFormatException {
        byte[] op = {0x1E, 0x2E, (byte)0xAE, 0x5E, 0x3E, (byte)0xDE, (byte)0xCE};
        String result = "1 2 + 5 3 / *";
        Assertions.assertEquals(result, payloadAdapter.uncompress(op));
    }

}
