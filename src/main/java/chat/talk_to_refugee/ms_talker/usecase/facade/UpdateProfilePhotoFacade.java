package chat.talk_to_refugee.ms_talker.usecase.facade;

import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ssm.SsmClient;

@Component
public record UpdateProfilePhotoFacade(TalkerRepository repository,
                                       S3Client s3Client,
                                       SsmClient ssmClient,
                                       @Value("${aws.localstack.endpoint}") String endpoint,
                                       @Value("${aws.localstack.s3.bucket-name}") String bucketName) {
}
