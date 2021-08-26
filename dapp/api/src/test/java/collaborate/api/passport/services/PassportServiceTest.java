package collaborate.api.passport.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import collaborate.api.passport.PassportService;
import collaborate.api.passport.create.CreatePassportDTO;
import collaborate.api.passport.create.TagCreatePassportService;
import collaborate.api.tag.model.Job;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassportServiceTest {

  @Mock private TagCreatePassportService tagCreatePassportService;

  @InjectMocks private PassportService passportService;

  @Test
  void create_Ok() {
    //GIVEN
    Job mock = initSomeJob();
    when(tagCreatePassportService.create(any(CreatePassportDTO.class))).thenReturn(mock);
    //WHEN
    Job actual = passportService.create(initPassport());
    //THEN
    assertThat(actual.getId()).isEqualTo(mock.getId());
    assertThat(actual.getStatus()).isEqualTo(mock.getStatus());
  }

  private CreatePassportDTO initPassport(){
    return CreatePassportDTO.builder()
        .vehicleOwnerMail("alice@theblockchainxdev.com")
        .vin("LE_VIN")
        .datasourceUUID(UUID.fromString("ab357d94-04da-4695-815e-24c569fd3a49"))
        .build();
  }

  private Job initSomeJob(){
    Job job = new Job();
    job.setId(1);
    job.setStatus(Job.Status.CREATED);
    return job;
  }
}