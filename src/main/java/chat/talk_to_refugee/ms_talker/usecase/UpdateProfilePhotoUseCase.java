package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.exception.CommonException;
import chat.talk_to_refugee.ms_talker.exception.InvalidExtensionException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateProfilePhoto;
import chat.talk_to_refugee.ms_talker.usecase.facade.UpdateProfilePhotoFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
public class UpdateProfilePhotoUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProfilePhotoUseCase.class);

    private final UpdateProfilePhotoFacade dependencies;

    public UpdateProfilePhotoUseCase(UpdateProfilePhotoFacade dependencies) {
        this.dependencies = dependencies;
    }

    public void execute(UUID id, UpdateProfilePhoto requestBody) {
        log.info("Updating talker profile photo");

        try {
            var talker = this.dependencies.repository().findById(id)
                    .orElseThrow(TalkerNotFoundException::new);

            checkExtensionValid(requestBody.profilePhoto().getOriginalFilename());

            var name = Instant.now().toEpochMilli() + ".jpeg";
            var request = PutObjectRequest.builder().bucket(this.dependencies.bucketName()).key(name).build();

            try (var s3Client = this.dependencies.s3Client()) {
                s3Client.putObject(request, RequestBody.fromBytes(requestBody.profilePhoto().getBytes()));
                log.info("New talker profile photo uploaded");

                if (talker.getProfilePhoto() != null && StringUtils.hasText(talker.getProfilePhoto())) {
                    var profilePhoto = Arrays.stream(
                            talker.getProfilePhoto().split("/")
                    ).toList().getLast();

                    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(this.dependencies.bucketName()).key(profilePhoto).build());
                    log.info("Talker current profile photo deleted");
                }
            }

            talker.setProfilePhoto(this.dependencies.endpoint() + "/" + this.dependencies.bucketName() + "/" + name);
            this.dependencies.repository().save(talker);
        } catch (IOException ex) {
            log.error("Unexpected error when uploading talker profile photo: {}", ex.getMessage(), ex);
            throw new CommonException();
        }

        log.info("Updating talker profile photo done");
    }

    private void checkExtensionValid(String fileName) {
        try (var ssmClient = this.dependencies.ssmClient()) {
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
}
