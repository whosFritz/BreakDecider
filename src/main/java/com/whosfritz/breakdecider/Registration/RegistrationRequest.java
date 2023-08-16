package com.whosfritz.breakdecider.Registration;

import com.whosfritz.breakdecider.Data.Entities.AppUserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    private String username;
    private String password;
    private AppUserRole appUserRole;

}
