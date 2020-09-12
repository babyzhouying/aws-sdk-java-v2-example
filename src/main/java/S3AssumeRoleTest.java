import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;

import java.nio.file.Paths;


public class S3AssumeRoleTest {


    public static void main(String[] args) {

        AssumeRoleRequest assumeRoleRequest = AssumeRoleRequest.builder().roleArn("arn:aws:iam::343820092671:role/assume_me").roleSessionName("test").build();
        //DefaultCredentialsProvider is using the ~/.aws/credential KeyId & AccessKey
        StsClient sts = StsClient.builder().credentialsProvider(DefaultCredentialsProvider.create()).build();
        AssumeRoleResponse response = sts.assumeRole(assumeRoleRequest);
        Credentials credentials = response.credentials();

        //assumerole
        Region region = Region.US_WEST_2;
        AwsSessionCredentials awsCreds = AwsSessionCredentials.create(
                credentials.accessKeyId(),
                credentials.secretAccessKey(),
                credentials.sessionToken());

        S3Client s3 = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCreds)).region(region).build();

        //list buckets
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
        listBucketsResponse.buckets().stream().forEach(x -> System.out.println(x.name()));

        //download encrypted files. need kms:Decrypt Allow policy
        String bucketName="fooobaar";
        //encrpted file
        String objectKey="inv1-pre/fooobaar/inv1-name/data/21f3b707-d31a-4aa9-ad84-da6c4f289b28.parquet";
        //added policy for decyption kms:Decrypt
        s3.getObject(GetObjectRequest.builder().bucket(bucketName).key(objectKey).build(), ResponseTransformer.toFile(Paths.get("1.parquet")));

    }
}
