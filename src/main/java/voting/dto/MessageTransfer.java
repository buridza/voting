package voting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageTransfer {
   private String successfulMessage;
   private Integer statusCode;
   private String errorMessage;

    public MessageTransfer(String successfulMessage, Integer statusCode) {
        this.successfulMessage = successfulMessage;
        this.statusCode = statusCode;
    }

    public MessageTransfer(Integer statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
