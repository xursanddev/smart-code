package uz.smartcode.smartapp.payload;

import lombok.Data;
import uz.smartcode.smartapp.entity.Social;
import uz.smartcode.smartapp.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link User} entity
 */
@Data
public class UserDto implements Serializable {
    @NotNull
    @Size(min = 7)
    private final String username;
    @NotNull
    private final String firstname;
    private final String lastname;
    @Email
    private final String email;
    private final String enabled;
    private final Integer levelId;
    private final Integer specialtyId;
    private final String password;
    @Size(max = 500)
    private final String bio;
}