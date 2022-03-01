package br.com.arcasoftware.sbs.listener;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public class NFeXMLZipListener {

    private static final String BUCKET_NAME = "nfereader232856-dev";
    private static final String REGION = "us-east-1";
    private static final String QUEUE_NAME = "https://sqs.us-east-1.amazonaws.com/492510987777/nfeReaderQueueZip";

    @SqsListener(value = QUEUE_NAME, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(Object message) {
        String sequencer = null;
        String userName = null;
        try {
            log.info("Received new SQS message: {}", message);

            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(REGION).build();

            SQSMessageDTO sqsMessage = new Gson().fromJson(message.toString(), SQSMessageDTO.class);

            String s3FileName = java.net.URLDecoder.decode(sqsMessage.getFileName(), StandardCharsets.UTF_8.name());

            S3Object o = s3.getObject(BUCKET_NAME, s3FileName);

            //"private/us-east-1:139cc8ac-b743-4a2e-98ca-db917c84e1ab/104814+-+NF-e-+35201207022495000107550010001048141973521268.xml"
            String[] pathComposition = s3FileName.split("/");

            String originalFileName = pathComposition[2];

            String dest = pathComposition[0] + "/" + pathComposition[1];

            processZipFile(o, originalFileName, dest);

            log.info("Processamento Finalizado");

        } catch (Exception e) {
            log.error("It was not possible to process this NFe because: " + e.getMessage());
        }
    }

    private void processZipFile(S3Object o, String s3FileName, String destination) throws IOException {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(REGION).build();

        File destDir = new File("/tmp/unzipFiles/" + s3FileName);
        byte[] buffer = new byte[1024];

        ZipInputStream zis = new ZipInputStream(o.getObjectContent());
        ZipEntry zipEntry = zis.getNextEntry();


        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);

            if (newFile.getName().startsWith(".")) {
                zipEntry = zis.getNextEntry();
                continue;
            }

            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();

                s3.putObject(BUCKET_NAME, destination + "/" + newFile.getName(), newFile);
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
class SQSMessageDTO {
    private String fileName;
    private String sequencer;
}