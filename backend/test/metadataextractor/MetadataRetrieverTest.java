package metadataextractor;

import util.MetadataRetriever;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by gregory on 17.09.16.
 */
public class MetadataRetrieverTest {
    public static void main(String... args) throws IOException, InterruptedException {
        Path workDir = Paths.get("/home/gregory/programming/recordholder/backend/test/metadataextractor");
        Path analyzer = Paths.get("/home/gregory/programming/recordholder/backend/test/metadataextractor/analyzer");
        File f = new File("/home/gregory/programming/recordholder/backend/test/metadataextractor/audio.mp3");
        MetadataRetriever extractor = new MetadataRetriever(workDir, analyzer);
        MetadataRetriever.Result result =
                extractor.extractMetadata(f, "cover.jpg");
        for (Map.Entry<String, String> p : result.getMetadata().entrySet()
                ) {
            System.out.println(p.getKey() + "=" + p.getValue());
        }
    }
}
