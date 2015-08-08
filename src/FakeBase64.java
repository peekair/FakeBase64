import java.lang.StringBuilder;
import java.io.ByteArrayOutputStream;

public class FakeBase64 {
    private static final int RANGE = 0xff;
    private static final char[] Base64ByteToStr = new char[] {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 
        'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 
        'u', 'v', 'w', 'x', 'y', 'z', 
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 
        'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7','8', '9', 
        '+', '/'
    };

    private static byte[] StrToBase64Byte = new byte[128];

    private void generateDecoder() throws Exception {
        for(int i = 0; i <= StrToBase64Byte.length - 1; i++) {
            StrToBase64Byte[i] = -1;
        }
        for(int i = 0; i <= Base64ByteToStr.length - 1; i++) {
            StrToBase64Byte[Base64ByteToStr[i]] = (byte)i;
        }
    }

    private void showDecoder() throws Exception {
        String str = "";
        for(int i = 1; i <= StrToBase64Byte.length; i++) {
            str += (int)StrToBase64Byte[i - 1] + ",";
            if(i % 10 == 0 || i == StrToBase64Byte.length) {
                str = "";
            }
        }
    }

    private String Base64Encode(byte[] bytes) throws Exception {
        StringBuilder res = new StringBuilder();

        for(int i = 0; i <= bytes.length - 1; i+=3) {
            byte[] enBytes = new byte[4];
            byte tmp = (byte)0x00;
            for(int k = 0; k <= 2; k++) {
                if((i + k) <= bytes.length - 1) {
                    enBytes[k] = (byte) (((((int) bytes[i + k] & RANGE) >>> (2 + 2 * k))) | (int)tmp);
                    tmp = (byte) (((((int) bytes[i + k] & RANGE) << (2 + 2 * (2 - k))) & RANGE) >>> 2);
                } else {
                    enBytes[k] = tmp;
                    tmp = (byte)64;
                }
            }
            enBytes[3] = tmp;
            for (int k = 0; k <= 3; k++) {
                if((int)enBytes[k] <= 63) {
                    res.append(Base64ByteToStr[(int)enBytes[k]]);
                } else {
                    res.append('=');
                }
            }
        }
        return res.toString();
    }

    private byte[] Base64Decode(String val) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] srcBytes = val.getBytes();
        byte[] base64bytes = new byte[srcBytes.length];
        
        for(int i = 0; i <= srcBytes.length - 1; i++) {
            int ind = (int) srcBytes[i];
            base64bytes[i] = StrToBase64Byte[ind];
        }
        
        for(int i = 0; i <= base64bytes.length - 1; i+=4) {
            byte[] deBytes = new byte[3];
            int delen = 0;
            byte tmp ;
            for(int k = 0; k <= 2; k++) {
                if((i + k + 1) <= base64bytes.length - 1 && base64bytes[i + k + 1] >= 0) {
                    tmp = (byte) (((int)base64bytes[i + k + 1] & RANGE) >>> (2 + 2 * (2 - (k + 1))));
                    deBytes[k] = (byte) ((((int) base64bytes[i + k] & RANGE) << (2 + 2 * k) & RANGE) | (int) tmp);
                    delen++;
                }
            }
            for(int k = 0; k <= delen - 1; k++) {
                bos.write((int)deBytes[k]);
            }
        }
        return bos.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        FakeBase64 nb = new FakeBase64();
        nb.generateDecoder();
        nb.showDecoder();
        
        String enStr = "UDYs1D7bNmdE1o3g5ms1V6RrYCVvODJF1DpxKTxAJ9xuZW==";
        byte[] deStr = nb.Base64Decode(enStr);
        
        System.out.print("Decode:");
        for(int i = 0; i < deStr.length; i++)
        	System.out.print(Integer.toHexString(deStr[i]&0xFF).toUpperCase());
        System.out.println();
        
        System.out.println("Encode:" + nb.Base64Encode(deStr));
    }
}