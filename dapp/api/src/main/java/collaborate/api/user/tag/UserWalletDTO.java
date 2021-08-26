package collaborate.api.user.tag;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletDTO implements Serializable {

    /**
     * The vault id used to store the publicKeyHash
     */
    private String userId;

    @JsonProperty("account")
    private String address;

}
