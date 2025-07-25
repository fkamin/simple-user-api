package home.simple_user_api.users.domain;

import home.simple_user_api.users.dtos.requests.AuthenticationRequest;
import home.simple_user_api.users.dtos.requests.RegistrationRequest;
import home.simple_user_api.users.dtos.responses.JwtResponse;
import home.simple_user_api.users.dtos.responses.RegistrationResponse;

public class UserAuthFacade {
    private final UserRepository userRepository;

    public UserAuthFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest) {
        return null;
    }

    public JwtResponse authenticate(AuthenticationRequest authenticationRequest) {
        return null;
    }

}
