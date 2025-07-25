package home.simple_user_api.users.domain;

import home.simple_user_api.commons.JwtService;
import home.simple_user_api.users.dtos.exceptions.InvalidLoginOrPasswordException;
import home.simple_user_api.users.dtos.exceptions.UserAlreadyExistsException;
import home.simple_user_api.users.dtos.requests.AuthenticationRequest;
import home.simple_user_api.users.dtos.requests.RegistrationRequest;
import home.simple_user_api.users.dtos.responses.JwtResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserAuthFacade {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public UserAuthFacade(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    public void register(RegistrationRequest registrationRequest) {
        if (userRepository.existsByLogin((registrationRequest.login()))) throw new UserAlreadyExistsException();

        User userToSave = new User(
                registrationRequest.login(),
                passwordEncoder.encode(registrationRequest.password())
        );
        userRepository.save(userToSave);
    }

    public JwtResponse authenticate(AuthenticationRequest authenticationRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.login());

        if (!passwordEncoder.matches(authenticationRequest.password(), userDetails.getPassword())) throw new InvalidLoginOrPasswordException();

        String token = jwtService.generateJwtToken(userDetails).getTokenValue();
        return new JwtResponse(token);
    }
}
