package uz.smartcode.smartapp.payload.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class MessageResponse {
    private int code;
    private HttpStatus status;
    private String message;

    public MessageResponse(HttpStatus status, String message) {
        this.code = status.value();
        this.status = status;
        this.message = message;
    }

    public MessageResponse(int code, String message) {
        this.code = code;
        this.status = HttpStatus.resolve(code);
        this.message = message;
    }
}
