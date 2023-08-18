package com.whosfritz.breakdecider.Registration;

import com.whosfritz.breakdecider.Data.Entities.AppUserRole;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    private String username;
    private String password;
    private AppUserRole appUserRole;
    private String secret_token;

}
