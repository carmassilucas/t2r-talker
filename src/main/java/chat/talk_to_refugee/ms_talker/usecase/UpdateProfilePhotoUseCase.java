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

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static chat.talk_to_refugee.ms_talker.util.AWSConstant.PROFILE_PHOTO_ALLOWED_EXTENSIONS;

@Service
public class UpdateProfilePhotoUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProfilePhotoUseCase.class);

    private final TalkerRepository repository;
    private final S3Client s3Client;
    private final String endpoint;
    private final String bucketName;

    public UpdateProfilePhotoUseCase(TalkerRepository repository, S3Client s3Client,
                                     @Value("${aws.localstack.endpoint}") String endpoint,
                                     @Value("${aws.localstack.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
        this.repository = repository;
    }

    public void execute(UUID id, UpdateProfilePhoto requestBody) {
        log.info("Updating talker profile photo");

        try {
            var talker = this.repository.findById(id)
                    .orElseThrow(TalkerNotFoundException::new);

            if (!isValidExtension(requestBody.profilePhoto().getOriginalFilename())) {
                throw new InvalidExtensionException();
            }

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

    private boolean isValidExtension(String fileName) {
        if (fileName == null) {
            return false;
        }

        var extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        return PROFILE_PHOTO_ALLOWED_EXTENSIONS.contains(extension.toUpperCase());
    }
}
