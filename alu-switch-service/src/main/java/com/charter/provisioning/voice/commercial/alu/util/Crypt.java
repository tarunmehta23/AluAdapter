package com.charter.provisioning.voice.commercial.alu.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@NoArgsConstructor
@Slf4j
public class Crypt {

    public static final String CHA = "*CHA*";

    public static String decrypt(String enc) {
        StringBuilder retVal = new StringBuilder();
        byte buffer[] = strConvertDecrypt(enc);

        try {
            String algo = "DES/CBC/NoPadding";
            Cipher c = Cipher.getInstance(algo);
            byte key[] = "55695673".getBytes();
            SecretKeySpec deskey = new SecretKeySpec(key, "DES");
            IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            c.init(Cipher.DECRYPT_MODE, deskey, iv);
            byte[] cipherText = c.doFinal(buffer);

            for (int i = 0; i < cipherText.length && cipherText[i] != 0; i++) {
                retVal.append((char) cipherText[i]);
            }
        }
        catch (Exception ex) {
            log.error("Exception decrypting.",ex);
        }
        return retVal.toString().trim();
    }

    private static byte[] charToBinary(byte ch) {
        int i, bitStatus = 0;
        byte binary[] = new byte[8];

        for (i = 0; i < 8; i++) {
            switch (i) {
                case 0:
                    bitStatus = (int) ch & 128;
                    break;
                case 1:
                    bitStatus = (int) ch & 64;
                    break;
                case 2:
                    bitStatus = (int) ch & 32;
                    break;
                case 3:
                    bitStatus = (int) ch & 16;
                    break;
                case 4:
                    bitStatus = (int) ch & 8;
                    break;
                case 5:
                    bitStatus = (int) ch & 4;
                    break;
                case 6:
                    bitStatus = (int) ch & 2;
                    break;
                case 7:
                    bitStatus = (int) ch & 1;
                    break;
            }
            binary[i] = (byte) ((bitStatus != 0) ? 1 : 0);
        }
        return binary;
    }

    private static byte[] binary_to_encrypted_str(byte[] binary_str) {
        int i, j;
        char ch;
        byte[] str = new byte[64];

        for (i = 0, j = 0; i < binary_str.length; i += 8, j++) {
            ch = 0;

            for (int k = i; k < i + 8; k++) {
                ch = (char) (ch | ((char) ((binary_str[k] == (byte) 1)
                        ? 1 << 7 - (k - i) : 0)));
            }

            if (ch < 48) {
                ch += 48;

                if ((ch > 57 && ch < 63) || (ch > 90 && ch < 97)) {
                    ch += 7;
                    str[j++] = '5';
                }
                else {
                    str[j++] = '1';
                }
            }
            else if (ch > 122) {
                if (ch < 196) {
                    ch -= 75;
                    if ((ch > 57 && ch < 63) || (ch > 90 && ch < 97)) {
                        ch -= 7;
                        str[j++] = '6';
                    }
                    else {
                        str[j++] = '2';
                    }
                } else {
                    ch -= 140;
                    if ((ch > 57 && ch < 63) || (ch > 90 && ch < 97)) {
                        ch -= 7;
                        str[j++] = '7';
                    }
                    else {
                        str[j++] = '3';
                    }
                }
            } else {
                if ((ch > 57 && ch < 63) || (ch > 90 && ch < 97)) {
                    ch -= 7;
                    str[j++] = '4';
                }
                else {
                    str[j++] = '0';
                }
            }
            str[j] = (byte) ch;
        }
        return str;
    }

    private static byte[] strToBinary2(byte[] str) {
        byte i, ch;
        byte[] byte_str;
        byte binary_str[] = null;

        for (i = 0; i < str.length; i++) {
            ch = str[i];
            byte_str = charToBinary(ch);

            if (binary_str == null) {
                binary_str = new byte[byte_str.length];
                System.arraycopy(byte_str, 0, binary_str, 0, byte_str.length);
            }
            else {
                byte[] work = new byte[byte_str.length + binary_str.length];
                System.arraycopy(binary_str, 0, work, 0, binary_str.length);
                System.arraycopy(byte_str, 0, work, binary_str.length, byte_str.length);
                binary_str = work;
            }
        }
        return binary_str;
    }

    private static String strConvertEncrypt(byte[] eValues) {
        byte[] pw = binary_to_encrypted_str(strToBinary2(eValues));
        return String.format("%s%s", CHA, new String(pw));
    }

    private static byte[] strConvertDecrypt(String encrypted) {
        String cha = encrypted;

        if (cha.startsWith(CHA)) {
            cha = cha.substring(5);
        }

        return encrypted_str_to_binary(cha.getBytes());
    }

    public static void main(String[] str) {
        String passwd = Crypt.encrypt();

        System.out.println("the encrypted password is " + Crypt.encrypt());
        System.out.println("the decrypt password is " + Crypt.decrypt(passwd));
        System.out.println("the encrypted password is " + Crypt.decrypt("*CHA*0L5D2B0B2c222p2s2v0N5D3n2N163G160h7U4X3a5f0m4X770s3e2T0g3E013H0z"));
    }

    public static String encrypt() {
        String retVal = "1980";
        byte buffer[] = new byte[32];

        for (int i = 0; i < buffer.length; i++) {
            if (i >= retVal.getBytes().length) {
                buffer[i] = 0;
            } else {
                buffer[i] = (retVal.getBytes())[i];
            }
        }
        try {
            String algo = "DES/CBC/NoPadding";
            Cipher c = Cipher.getInstance(algo);
            byte key[] = "55695673".getBytes();
            SecretKeySpec deskey = new SecretKeySpec(key, "DES");
            IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            c.init(Cipher.ENCRYPT_MODE, deskey, iv);
            byte[] ciphertext = c.doFinal(buffer);
            retVal = strConvertEncrypt(ciphertext);
        }
        catch (Exception ex) {
            log.error("Exception encrypt.",ex);
        }

        return retVal.trim();
    }

    private static byte[] encrypted_str_to_binary(byte[] str) {
        int i, bit_flag = 0;
    /*
     * Convert the string to binary
     */
        byte[] ret = new byte[256];
        int pos = 0;

        for (i = 0; i < str.length; i++) {
            if ((i % 2) == 0) {
                switch (str[i]) {
                    case '0':
                        bit_flag = 0;
                        break;
                    case '1':
                        bit_flag = 1;
                        break;
                    case '2':
                        bit_flag = 2;
                        break;
                    case '3':
                        bit_flag = 3;
                        break;
                    case '4':
                        bit_flag = 4;
                        break;
                    case '5':
                        bit_flag = 5;
                        break;
                    case '6':
                        bit_flag = 6;
                        break;
                    case '7':
                        bit_flag = 7;
                        break;
                }
                continue;
            }

            int ch = (int) str[i];

            switch (bit_flag) {
                case 1:
                    ch -= 48;
                    break;
                case 2:
                    ch += 75;
                    break;
                case 3:
                    ch += 140;
                    break;
                case 4:
                    ch += 7;
                    break;
                case 5:
                    ch -= 55;
                    break;
                case 6:
                    ch += 82;
                    break;
                case 7:
                    ch += 147;
                    break;
                default:
                    break;
            }

            byte[] byte_str = charToBinary((byte) ch);
            for (byte aByte_str : byte_str) ret[pos++] = aByte_str;
        }

        for (i = pos; i < 256; i++)
            ret[i] = 0;
        return binary_to_decrypted_str2(ret);
    }

    private static byte[] binary_to_decrypted_str2(byte[] binary_str) {
        int i, j;
        byte[] str = new byte[32];

        for (i = 0, j = 0; i < binary_str.length; i += 8, j++) {
            int ch = 0;
            for (int k = 0; k < 8; k++) {
                ch = ch * 2 + binary_str[i + k];
            }
            str[j] = (byte) ch;
        }

        return str;
    }
}

