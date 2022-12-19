package uz.smartcode.smartapp.payload.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginRequest {
    @NotNull
    @Size(min = 4, max = 60)
    private String username;

    @NotNull
    @Size(min = 7)
    private String password;
}
