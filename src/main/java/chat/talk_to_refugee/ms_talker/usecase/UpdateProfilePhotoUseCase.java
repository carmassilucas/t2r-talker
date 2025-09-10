package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.CommonException;
import chat.talk_to_refugee.ms_talker.exception.InvalidExtensionException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateProfilePhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
public class UpdateProfilePhotoUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProfilePhotoUseCase.class);

    private final TalkerRepository repository;
    private final S3Client s3Client;
    private final SsmClient ssmClient;
    private final String endpoint;
    private final String bucketName;

    public UpdateProfilePhotoUseCase(TalkerRepository repository,
                                     S3Client s3Client,
                                     SsmClient ssmClient,
                                     @Value("${aws.localstack.endpoint}") String endpoint,
                                     @Value("${aws.localstack.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
        this.repository = repository;
        this.ssmClient = ssmClient;
    }

    public void execute(UUID id, UpdateProfilePhoto requestBody) {
        log.info("Updating talker profile photo");

        try {
            var talker = this.repository.findById(id)
                    .orElseThrow(TalkerNotFoundException::new);

            checkExtensionValid(requestBody.profilePhoto().getOriginalFilename());

            var name = Instant.now().toEpochMilli() + ".jpeg";
            var request = PutObjectRequest.builder().bucket(bucketName).key(name).build();

            this.s3Client.putObject(request, RequestBody.fromBytes(requestBody.profilePhoto().getBytes()));
            log.info("New talker profile photo uploaded");

            if (talker.getProfilePhoto() != null && StringUtils.hasText(talker.getProfilePhoto())) {
                var profilePhoto = Arrays.stream(
                        talker.getProfilePhoto().split("/")
                ).toList().getLast();

                this.s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(profilePhoto).build());
                log.info("Talker current profile photo deleted");
            }

            talker.setProfilePhoto(endpoint + "/" + bucketName + "/" + name);
            this.repository.save(talker);
        } catch (IOException ex) {
            log.error("Unexpected error when uploading talker profile photo: {}", ex.getMessage(), ex);
            throw new CommonException();
        }

        log.info("Updating talker profile photo done");
    }

    private void checkExtensionValid(String fileName) {
        var response = ssmClient.getParameter(
                GetParameterRequest.builder().name("PROFILE_PHOTO_ALLOWED_EXTENSIONS").build()
        );
        var allowedExtensions = Arrays.stream(response.parameter().value().split(","));

        if (fileName == null) {
            throw new InvalidExtensionException(allowedExtensions);
        }

        var extension = fileName.substring(fileName.lastIndexOf('.') + 1);

        if (allowedExtensions.noneMatch(extension::equalsIgnoreCase)) {
            throw new InvalidExtensionException(allowedExtensions);
        }
    }
}
