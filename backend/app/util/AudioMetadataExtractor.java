package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gregory on 10.09.16.
 */

class ByteBuffer{
    private static final int INITIAL_CAPACITY = 16;
    private byte[] data = new byte[INITIAL_CAPACITY];
    private int pos = -1;

    public void put(byte b){
        if(++pos == data.length)
            data = Arrays.copyOf(data, data.length * 2);
        data[pos] = b;
    }

    public String getString(Charset cs){
        if(pos == -1) return null;
        return new String(data, 0, pos+1, cs);
    }

    public void clear(){
        pos = -1;
    }

    public static String readStringToEnd(InputStream s, Charset ch) throws IOException {
        int read_byte;
        ByteBuffer bb = new ByteBuffer();
        while((read_byte = s.read())!=-1){
            bb.put((byte)read_byte);
        }
        return bb.getString(ch);
    }
}

public class AudioMetadataExtractor {
    public static class MetadataExtractionResult{
        private boolean hasCover;
        private Map<String, String> metadata;
        private String errorLog;
        private int exitCode;

        public MetadataExtractionResult(boolean hasCover, Map<String, String> metadata, String errorLog, int exitCode) {
            this.hasCover = hasCover;
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

        public int getExitCode() {
            return exitCode;
        }
    }

    private String extractorExecutable;
    private String coverPath;

    public AudioMetadataExtractor(Path workDir, Path extractorExecutable, String tmpCoverName){
        this.extractorExecutable = extractorExecutable.toAbsolutePath().toString();
        this.coverPath = workDir.resolve(tmpCoverName).toAbsolutePath().toString();
    }

    private static Map<String, String> getMetadata(InputStream s) throws IOException {
        Map<String, String> metadata = new HashMap<>();
        ByteBuffer byteBuffer = new ByteBuffer();
        String tmpKey=null,tmpVal;
        int n;

        while((n = s.read()) != -1){
            switch (n){
                case '\\':
                    int escaped = s.read();
                    if(escaped == -1) throw new IllegalStateException("Stream ended while reading escaped char");
                    switch (escaped){
                        case '\\': byteBuffer.put((byte)'\\');break;
                        case 'n':  byteBuffer.put((byte) '\n');break;
                        case '=':  byteBuffer.put((byte) '='); break;
                        default: throw new IllegalStateException("Wrong escaped character");
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
                default: byteBuffer.put((byte)n);break;
            }
        }

        return metadata;
    }

    //Extractor exit code 0 means everything is Ok, 1 means everything is ok but cover not found
    public MetadataExtractionResult extractMetadata(File f) throws IOException, InterruptedException {
        String filePath = f.getAbsolutePath();
        ProcessBuilder pb = new ProcessBuilder(extractorExecutable, filePath, coverPath);
        Process p = pb.start();
        int exitCode = p.waitFor();

        String errorLog = ByteBuffer.readStringToEnd(p.getErrorStream(), StandardCharsets.UTF_8);
        if(exitCode != 0 && exitCode != 1)
            return new MetadataExtractionResult(false, null, errorLog, exitCode);
        Map<String, String> metadata = getMetadata(p.getInputStream());
        return new MetadataExtractionResult(exitCode == 0, metadata, errorLog, exitCode);
    }
}
