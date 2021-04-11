package me.ote.polishcalc.api.protocol;

import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

@RequestScoped
public class PayloadAdapter {
    public static final byte NIBBLE_0 = 0x00;
    public static final byte NIBBLE_1 = 0x01;
    public static final byte NIBBLE_2 = 0x02;
    public static final byte NIBBLE_3 = 0x03;
    public static final byte NIBBLE_4 = 0x04;
    public static final byte NIBBLE_5 = 0x05;
    public static final byte NIBBLE_6 = 0x06;
    public static final byte NIBBLE_7 = 0x07;
    public static final byte NIBBLE_8 = 0x08;
    public static final byte NIBBLE_9 = 0x09;
    public static final byte NIBBLE_PLUS = 0x0A;
    public static final byte NIBBLE_SUBS = 0x0B;
    public static final byte NIBBLE_MUL  = 0x0C;
    public static final byte NIBBLE_DIV  = 0x0D;
    public static final byte NIBBLE_ESP  = 0x0E;
    public static final byte NIBBLE_FAIL = 0x0F;

    @Inject
    Logger log;

    PayloadAdapter() {
    }

    public byte[] compress(String strPayload) throws PayloadFormatException {
        byte[] nibblePayload = new byte[Math.floorDiv(strPayload.length() + 1, 2)];
        byte[] bytePayload = strPayload.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < strPayload.length(); i++) {
            int nibble_i = Math.floorDiv(i, 2);
            if(Math.floorMod(i, 2) == 0) {
                nibblePayload[nibble_i] = (byte) (charToNibble((char) bytePayload[i]) << 4);
            } else {
                nibblePayload[nibble_i] = (byte) (nibblePayload[nibble_i] | charToNibble((char) bytePayload[i]));
            }
        }
        if(Math.floorMod(strPayload.length(), 2) == 1) {
            nibblePayload[nibblePayload.length - 1] = (byte) (nibblePayload[nibblePayload.length - 1] | NIBBLE_ESP);
        }
        return nibblePayload;
    }

    public String uncompress(byte[] rawPayload) throws PayloadFormatException {
        String strPayload = "";
        for (int i = 0; i < rawPayload.length; i++) {
            strPayload = strPayload
                    .concat(nibbleToChar((byte) ((rawPayload[i] & 0xF0) >> 4)))
                    .concat(nibbleToChar((byte) (rawPayload[i] & 0x0F)));
        }
        return strPayload.trim();
    }

    public byte charToNibble(char character) throws PayloadFormatException {
        switch (character){
            case '0':
                return NIBBLE_0;
            case '1':
                return NIBBLE_1;
            case '2':
                return NIBBLE_2;
            case '3':
                return NIBBLE_3;
            case '4':
                return NIBBLE_4;
            case '5':
                return NIBBLE_5;
            case '6':
                return NIBBLE_6;
            case '7':
                return NIBBLE_7;
            case '8':
                return NIBBLE_8;
            case '9':
                return NIBBLE_9;
            case '+':
                return NIBBLE_PLUS;
            case '-':
                return NIBBLE_SUBS;
            case '/':
                return NIBBLE_DIV;
            case '*':
                return NIBBLE_MUL;
            case ' ':
                return NIBBLE_ESP;
            case 'F':
                return NIBBLE_FAIL;
            default:
                throw new PayloadFormatException(String.format("Unaccepted character %s", character));
        }
    }

    public String nibbleToChar(byte nibble) throws PayloadFormatException {
        switch (nibble) {
            case NIBBLE_0:
                return "0";
            case NIBBLE_1:
                return "1";
            case NIBBLE_2:
                return "2";
            case NIBBLE_3:
                return "3";
            case NIBBLE_4:
                return "4";
            case NIBBLE_5:
                return "5";
            case NIBBLE_6:
                return "6";
            case NIBBLE_7:
                return "7";
            case NIBBLE_8:
                return "8";
            case NIBBLE_9:
                return "9";
            case NIBBLE_PLUS:
                return "+";
            case NIBBLE_SUBS:
                return "-";
            case NIBBLE_MUL:
                return "*";
            case NIBBLE_DIV:
                return "/";
            case NIBBLE_FAIL:
                return "F";
            case NIBBLE_ESP:
                return " ";
            default:
                throw new PayloadFormatException(String.format("Unaccepted nibble: %4s", Integer.toBinaryString(nibble)));
        }
    }
}
