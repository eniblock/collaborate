package collaborate.api.config.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorModel{
    private String fieldName;
    private Object rejectedValue;
    private String messageError;
}