package kr.starly.libs.nms.util;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class DataUtils {

    private static final char[] HEX_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void writeByteArray(@NotNull DataOutputStream dos, byte @NotNull [] array) throws IOException {
        dos.writeInt(array.length);
        dos.write(array);
    }

    public static byte @NotNull [] readByteArray(@NotNull DataInputStream din) throws IOException {
        int size = din.readInt();
        byte[] array = new byte[size];
        din.readFully(array);
        return array;
    }

    public static void write2DByteArray(@NotNull DataOutputStream dos, byte @NotNull [] @NotNull [] array2d) throws IOException {
        dos.writeInt(array2d.length);
        for (byte[] array : array2d) {
            writeByteArray(dos, array);
        }
    }

    public static byte @NotNull [] @NotNull [] read2DByteArray(@NotNull DataInputStream din) throws IOException {
        int size2d = din.readInt();

        byte[][] array2d = new byte[size2d][];
        for (int i = 0; i < size2d; i++) {
            array2d[i] = readByteArray(din);
        }

        return array2d;
    }

    public static String toHexadecimalString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}