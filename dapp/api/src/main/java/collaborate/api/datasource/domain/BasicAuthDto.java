package collaborate.api.datasource.domain;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class BasicAuthDto implements Serializable {
    private String user;
    @ToString.Exclude
    private String password;
}
