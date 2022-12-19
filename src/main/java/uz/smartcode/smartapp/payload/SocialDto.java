package uz.smartcode.smartapp.payload;

import lombok.Data;
import uz.smartcode.smartapp.entity.Social;

import java.util.Set;
import java.util.UUID;

@Data
public class SocialDto {
    private final Set<Social> socials;
    private final UUID userId;
}
