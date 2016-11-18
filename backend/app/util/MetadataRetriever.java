package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;



public class MetadataRetriever {

    private final String executable;
    private ByteBuffer byteBuffer = new ByteBuffer();

    public MetadataRetriever(Path executable) {
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

    private void readLengthBitrate(Result res, InputStream s) throws IOException{
        byteBuffer.clear();
        int n;
        while ((n=s.read()) != '\n'){
            if(n == -1) throw new IllegalStateException("Stream ended while reading length&bitrait");
            byteBuffer.put((byte) n);
        }
        String[] vals = byteBuffer.getString(StandardCharsets.UTF_8).split(" ");
        res.length = Double.parseDouble(vals[0]);
        res.bitrate = Long.parseLong(vals[1]);
    }

    //Extractor exit code 0 means everything is Ok, 1 means everything is ok but cover not found
    public Result extractMetadata(File f)
            throws IOException, InterruptedException {
        String filePath = f.getAbsolutePath();
        File cover = File.createTempFile("cover", "jpg");
        ProcessBuilder pb = new ProcessBuilder(executable, filePath, cover.getAbsolutePath());
        Process p = pb.start();

        Result res = new Result();
        res.exitCode = p.waitFor();
        if(res.exitCode != 0) cover.delete();
        else res.cover = cover;
        InputStream output = p.getInputStream();

        res.errorLog = byteBuffer.readStreamToEnd(p.getErrorStream(), StandardCharsets.UTF_8);
        if (res.exitCode != 0 && res.exitCode != 1) {
            return res;
        }

        if(res.exitCode == 0) res.cover = cover;
        readLengthBitrate(res, output);
        res.metadata = getMetadata(output);
        return res;
    }

    public static class Result {
        private double length; //In seconds
        private long bitrate; //In kb
        private Map<String, String> metadata;
        private String errorLog;
        private int exitCode;
        private File cover;

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public String getErrorLog() {
            return errorLog;
        }

        public boolean isExitSuccesfull() {
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

        public File getCover() {
            return cover;
        }
    }
}
