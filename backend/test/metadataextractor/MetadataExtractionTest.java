package metadataextractor;

import util.AudioMetadataExtractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by gregory on 17.09.16.
 */
public class MetadataExtractionTest {
    public static void main(String... args) throws IOException, InterruptedException {
        Path workDir = Paths.get("/home/gregory/programming/recordholder/backend/test/metadataextractor");
        Path analyzer = Paths.get("/home/gregory/programming/recordholder/backend/test/metadataextractor/analyzer");
        File f = new File("/home/gregory/programming/recordholder/backend/test/metadataextractor/audio.mp3");
        AudioMetadataExtractor extractor = new AudioMetadataExtractor(
                workDir,
                analyzer,
                "cover.jpg");
        AudioMetadataExtractor.MetadataExtractionResult result =
                extractor.extractMetadata(f);
        for (Map.Entry<String, String> p: result.getMetadata().entrySet()
             ) {
            System.out.println(p.getKey() + "=" + p.getValue());
        }
    }
}
