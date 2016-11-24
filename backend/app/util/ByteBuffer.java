package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class ByteBuffer {
    private static final int INITIAL_CAPACITY = 512;
    private byte[] data = new byte[INITIAL_CAPACITY];
    private int pos = -1;

    public void clear(){
        pos = -1;
    }

    public void put(byte b){
        if(++pos == data.length)
            data = Arrays.copyOf(data, data.length * 2);
        data[pos] = b;
    }

    public String getString(Charset cs){
        //if(pos == -1) return null;
        return new String(data, 0 , pos + 1, cs);
    }

    public String readStreamToEnd(InputStream s, Charset ch) throws IOException {
        int n;
        clear();
        while((n = s.read()) != -1){
            put((byte) n);
        }
        return getString(ch);
    }

    public String readStreamToNewLine(InputStream s, Charset ch) throws IOException {
        int n;
        clear();
        while((n=s.read()) != '\n'){
            if(n == -1) throw new IllegalStateException("Stream ended but newline \\n expected");
            put((byte) n);
        }
        return getString(ch);
    }
}
