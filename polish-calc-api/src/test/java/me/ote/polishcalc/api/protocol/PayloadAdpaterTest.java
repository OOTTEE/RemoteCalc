package me.ote.polishcalc.api.protocol;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PayloadAdapterTest {

    @Test
    public void testCharToNibble1() {
        String op = "1 2 3 + *";
        byte[] result = {0x10, 0x20, 0x30, (byte)0xA0, (byte)0xC0};
        Assertions.assertArrayEquals(result, PayloadAdapter.compress(op));
    }

    @Test
    public void testCharToNibble2() {
        String op = "11 2 3 + *";
        byte[] result = {0x11, 0x02, 0x03, 0x0A, 0x0C};
        Assertions.assertArrayEquals(result, PayloadAdapter.compress(op));
    }

    @Test
    public void testCharToNibble3() {
        String op = "1 2 + 5 3 / *";
        byte[] result = {0x10, 0x20, (byte)0xA0, 0x50, 0x30, (byte)0xD0, (byte)0xC0};
        Assertions.assertArrayEquals(result, PayloadAdapter.compress(op));
    }

    @Test
    public void testNibbleToChar1() {
        byte[] op = {0x10, 0x20, 0x30, (byte)0xA0, (byte)0xC0};
        String result = "1 2 3 + *";
        Assertions.assertEquals(result, PayloadAdapter.uncompress(op));
    }

    @Test
    public void testNibbleToChar2() {
        byte[] op = {0x11, 0x02, 0x03, 0x0A, 0x0C};
        String result = "11 2 3 + *";
        Assertions.assertEquals(result, PayloadAdapter.uncompress(op));
    }

    @Test
    public void testNibbleToChar3() {
        byte[] op = {0x10, 0x20, (byte)0xA0, 0x50, 0x30, (byte)0xD0, (byte)0xC0};
        String result = "1 2 + 5 3 / *";
        Assertions.assertEquals(result, PayloadAdapter.uncompress(op));
    }

}
