package chat.talk_to_refugee.ms_talker.usecase;

import chat.talk_to_refugee.ms_talker.entity.Talker;
import chat.talk_to_refugee.ms_talker.exception.CommonException;
import chat.talk_to_refugee.ms_talker.exception.InvalidExtensionException;
import chat.talk_to_refugee.ms_talker.exception.TalkerNotFoundException;
import chat.talk_to_refugee.ms_talker.repository.TalkerRepository;
import chat.talk_to_refugee.ms_talker.resource.dto.UpdateProfilePhoto;
import chat.talk_to_refugee.ms_talker.usecase.facade.UpdateProfilePhotoFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProfilePhotoUseCaseTest {

    @InjectMocks
    private UpdateProfilePhotoUseCase updateProfilePhoto;

    @Mock private UpdateProfilePhotoFacade dependencies;
    @Mock private TalkerRepository repository;
    @Mock private S3Client s3Client;
    @Mock private SsmClient ssmClient;

    private final String endpoint = "endpoint";
    private final String bucketName = "bucket-name";

    @Test
    @DisplayName("Deve ser possível atualizar a foto de perfil")
    void should_be_possible_update_profile_photo() throws IOException {
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.dependencies.s3Client()).thenReturn(this.s3Client);
        when(this.dependencies.ssmClient()).thenReturn(this.ssmClient);
        when(this.dependencies.endpoint()).thenReturn(this.endpoint);
        when(this.dependencies.bucketName()).thenReturn(this.bucketName);

        var uuid = UUID.randomUUID();

        var file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("originalFilename.jpeg");
        when(file.getBytes()).thenReturn(new byte[] {});

        var requestBody = new UpdateProfilePhoto(file);

        var talker = new Talker();
        talker.setProfilePhoto("profilePhoto.jpeg");
        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));

        var parameter = Parameter.builder().value("JPG,JPEG,PNG,GIF").build();
        var response = GetParameterResponse.builder().parameter(parameter).build();
        when(this.ssmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        this.updateProfilePhoto.execute(uuid, requestBody);

        verify(this.ssmClient).getParameter(any(GetParameterRequest.class));
        verify(this.s3Client).deleteObject(any(DeleteObjectRequest.class));
        verify(this.s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(this.repository).save(talker);
    }

    @Test
    @DisplayName("Deve ser possível atualizar a foto de perfil quando atual nula")
    void should_be_possible_update_profile_photo_when_current_one_null() throws IOException {
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.dependencies.s3Client()).thenReturn(this.s3Client);
        when(this.dependencies.ssmClient()).thenReturn(this.ssmClient);
        when(this.dependencies.endpoint()).thenReturn(this.endpoint);
        when(this.dependencies.bucketName()).thenReturn(this.bucketName);

        var uuid = UUID.randomUUID();

        var file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("originalFilename.jpeg");
        when(file.getBytes()).thenReturn(new byte[] {});

        var requestBody = new UpdateProfilePhoto(file);

        var talker = mock(Talker.class);
        when(this.repository.findById(uuid)).thenReturn(Optional.of(talker));

        var parameter = Parameter.builder().value("JPG,JPEG,PNG,GIF").build();
        var response = GetParameterResponse.builder().parameter(parameter).build();
        when(this.ssmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        this.updateProfilePhoto.execute(uuid, requestBody);

        verify(this.ssmClient).getParameter(any(GetParameterRequest.class));
        verify(this.s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        verify(this.s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(this.repository).save(talker);
    }

    @Test
    @DisplayName("Deve lançar exceção quando talker não encontrado")
    void should_throw_exception_when_talker_not_found() {
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(TalkerNotFoundException.class,
                () -> this.updateProfilePhoto.execute(UUID.randomUUID(), mock(UpdateProfilePhoto.class))
        );

        verify(this.s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        verify(this.s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(this.repository, never()).save(any(Talker.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando extensão do arquivo inválida")
    void should_throw_exception_when_invalid_file_extension() {
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.dependencies.ssmClient()).thenReturn(this.ssmClient);

        var uuid = UUID.randomUUID();

        var file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("originalFilename.pdf");

        when(this.repository.findById(uuid)).thenReturn(Optional.of(mock(Talker.class)));

        var parameter = Parameter.builder().value("JPG,JPEG,PNG,GIF").build();
        var response = GetParameterResponse.builder().parameter(parameter).build();
        when(this.ssmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        var requestBody = new UpdateProfilePhoto(file);

        assertThrows(InvalidExtensionException.class, () -> this.updateProfilePhoto.execute(uuid, requestBody));

        verify(this.s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        verify(this.s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(this.repository, never()).save(any(Talker.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do arquivo nulo")
    void should_throw_exception_when_file_name_null() {
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.dependencies.ssmClient()).thenReturn(this.ssmClient);

        var uuid = UUID.randomUUID();

        var file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);

        when(this.repository.findById(uuid)).thenReturn(Optional.of(mock(Talker.class)));

        var parameter = Parameter.builder().value("JPG,JPEG,PNG,GIF").build();
        var response = GetParameterResponse.builder().parameter(parameter).build();
        when(this.ssmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        var requestBody = new UpdateProfilePhoto(file);

        assertThrows(InvalidExtensionException.class, () -> this.updateProfilePhoto.execute(uuid, requestBody));

        verify(this.s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        verify(this.s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(this.repository, never()).save(any(Talker.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro ao armazenar foto de perfil")
    void should_throw_exception_when_error_storing_profile_photo() throws IOException {
        when(this.dependencies.repository()).thenReturn(this.repository);
        when(this.dependencies.s3Client()).thenReturn(this.s3Client);
        when(this.dependencies.ssmClient()).thenReturn(this.ssmClient);
        when(this.dependencies.bucketName()).thenReturn(this.bucketName);

        var uuid = UUID.randomUUID();

        var file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("originalFilename.jpeg");

        doThrow(IOException.class).when(file).getBytes();

        var requestBody = new UpdateProfilePhoto(file);

        when(this.repository.findById(uuid)).thenReturn(Optional.of(mock(Talker.class)));

        var parameter = Parameter.builder().value("JPG,JPEG,PNG,GIF").build();
        var response = GetParameterResponse.builder().parameter(parameter).build();
        when(this.ssmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        assertThrows(CommonException.class, () -> this.updateProfilePhoto.execute(uuid, requestBody));

        verify(this.s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(this.s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
        verify(this.repository, never()).save(any(Talker.class));
    }
}