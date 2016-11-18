package util;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


class ByteBuffer {
    private static final int INITIAL_CAPACITY = 16;
    private byte[] data = new byte[INITIAL_CAPACITY];
    private int pos = -1;

    public static String readStringToEnd(InputStream s, Charset ch)
            throws IOException {
        int read_byte;
        ByteBuffer bb = new ByteBuffer();
        while ((read_byte = s.read()) != -1) {
            bb.put((byte) read_byte);
        }
        return bb.getString(ch);
    }

    public void put(byte b) {
        if (++pos == data.length)
            data = Arrays.copyOf(data, data.length * 2);
        data[pos] = b;
    }

    public String getString(Charset cs) {
        if (pos == -1) return null;
        return new String(data, 0, pos + 1, cs);
    }

    public void clear() {
        pos = -1;
    }
}

public class MetadataRetriever {
    private final Path workDir;
    private final String executable;
    private ByteBuffer byteBuffer = new ByteBuffer();

    public MetadataRetriever(Path workDir, Path executable) {
        this.workDir = workDir;
        this.executable = executable.toAbsolutePath().toString();
    }

    private Map<String, String> getMetadata(InputStream s) throws IOException {
        Map<String, String> metadata = new HashMap<>();
        String tmpKey = null, tmpVal;
        int n;
        byteBuffer.clear();

        while ((n = s.read()) != -1) {
            switch (n) {
                case '\\':
                    int escaped = s.read();
                    if (escaped == -1) throw new IllegalStateException("Stream ended while reading escaped char");
                    switch (escaped) {
                        case '\\':
                            byteBuffer.put((byte) '\\');
                            break;
                        case 'n':
                            byteBuffer.put((byte) '\n');
                            break;
                        case '=':
                            byteBuffer.put((byte) '=');
                            break;
                        default:
                            throw new IllegalStateException("Wrong escaped character");
                    }
                    break;
                case '=':
                    tmpKey = byteBuffer.getString(StandardCharsets.UTF_8);
                    byteBuffer.clear();
                    break;
                case '\n':
                    tmpVal = byteBuffer.getString(StandardCharsets.UTF_8);
                    metadata.put(tmpKey, tmpVal);
                    byteBuffer.clear();
                    break;
                default:
                    byteBuffer.put((byte) n);
                    break;
            }
        }

        return metadata;
    }

    //Extractor exit code 0 means everything is Ok, 1 means everything is ok but cover not found
    public Result extractMetadata(File f, String coverName)
            throws IOException, InterruptedException {
        double length;
        long bitrate;
        int n;
        String filePath = f.getAbsolutePath();
        String coverPath = workDir.resolve(coverName).toAbsolutePath().toString();
        ProcessBuilder pb = new ProcessBuilder(executable, filePath, coverPath);
        Process p = pb.start();
        int exitCode = p.waitFor();
        InputStream output = p.getInputStream();

        String errorLog = ByteBuffer.readStringToEnd(p.getErrorStream(), StandardCharsets.UTF_8);
        if (exitCode != 0 && exitCode != 1)
            return new Result(exitCode, errorLog, false, 0, 0, null);

        //Read length and bitrate
        byteBuffer.clear();
        while ((n = output.read()) != '\n') {
            if (n == -1) throw new IllegalStateException("Stream ended while reading first line");
            byteBuffer.put((byte) n);
        }
        {
            String[] vals = byteBuffer.getString(Charsets.UTF_8).split(" ");
            length = Double.parseDouble(vals[0]);
            bitrate = Long.parseLong(vals[1]);
        }

        Map<String, String> metadata = getMetadata(output);
        return new Result(exitCode, errorLog, exitCode == 0, length, bitrate, metadata);
    }

    public static class Result {
        private final double length; //In seconds
        private final long bitrate; //In kb
        private final boolean hasCover;
        private final Map<String, String> metadata;
        private final String errorLog;
        private final int exitCode;

        public Result(int exitCode, String errorLog, boolean hasCover,
                      double length, long bitrate, Map<String, String> metadata) {
            this.hasCover = hasCover;
            this.length = length;
            this.bitrate = bitrate;
            this.metadata = metadata;
            this.errorLog = errorLog;
            this.exitCode = exitCode;
        }

        public boolean isCoverExtracted() {
            return hasCover;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public String getErrorLog() {
            return errorLog;
        }

        public boolean exitSuccesfully() {
            return exitCode == 0 || exitCode == 1;
        }

        public int getExitCode() {
            return exitCode;
        }

        public long getBitrate() {
            return bitrate;
        }

        public double getLength() {
            return length;
        }
    }
}