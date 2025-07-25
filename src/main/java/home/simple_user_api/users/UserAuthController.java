package home.simple_user_api.users;

import home.simple_user_api.users.domain.UserAuthFacade;
import home.simple_user_api.users.dtos.requests.AuthenticationRequest;
import home.simple_user_api.users.dtos.requests.RegistrationRequest;
import home.simple_user_api.users.dtos.responses.JwtResponse;
import home.simple_user_api.users.dtos.responses.RegistrationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserAuthController {
    private final UserAuthFacade userAuthFacade;

    public UserAuthController(UserAuthFacade userAuthFacade) {
        this.userAuthFacade = userAuthFacade;
    }

    @PostMapping("register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        RegistrationResponse registrationResponse = userAuthFacade.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(registrationResponse);
    }

    @PostMapping("login")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        JwtResponse jwtResponse = userAuthFacade.authenticate(authenticationRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
