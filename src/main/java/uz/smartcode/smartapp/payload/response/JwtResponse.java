package uz.smartcode.smartapp.payload.response;

import lombok.Data;
import uz.smartcode.smartapp.entity.Role;

import java.util.Set;
import java.util.UUID;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String username;
    private String email;
    private Set<Role> roles;

    public JwtResponse(String token, UUID id, String username, String email, Set<Role> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
