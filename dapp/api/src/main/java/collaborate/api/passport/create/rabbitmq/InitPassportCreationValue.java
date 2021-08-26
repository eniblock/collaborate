package collaborate.api.passport.create.rabbitmq;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InitPassportCreationValue implements Serializable {

  @NotNull
  private InitPassportCreationParams initPassportCreation;

}
