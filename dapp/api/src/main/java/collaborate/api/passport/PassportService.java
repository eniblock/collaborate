package collaborate.api.passport;

import collaborate.api.passport.create.CreatePassportDTO;
import collaborate.api.passport.create.TagCreatePassportService;
import collaborate.api.tag.model.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassportService {

  private final TagCreatePassportService tagCreatePassportService;

  public Job create(CreatePassportDTO createPassportDTO) {
    return tagCreatePassportService.create(createPassportDTO);
  }

}
