package sparta.paymentassignment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.user.User;
import sparta.paymentassignment.dto.user.LoginRequest;
import sparta.paymentassignment.dto.user.LoginResponse;
import sparta.paymentassignment.dto.user.RegisterRequest;
import sparta.paymentassignment.dto.user.RegisterResponse;
import sparta.paymentassignment.domain.user.UserRole;
import sparta.paymentassignment.exception.EmailDuplicationException;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.repository.UserRepository;
import sparta.paymentassignment.security.CustomUserDetails;
import sparta.paymentassignment.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        boolean existence = userRepository.existsByEmail(request.getEmail());
        if (existence) throw new EmailDuplicationException(ErrorCode.DUPLICATE_EMAIL);

        User user = new User(
                request.getName(),
                request.getPhone(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.getRole(request.getRole())
        );
        userRepository.save(user);
        return new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getRole().getRoleName()
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.createToken(authentication);

        return new LoginResponse(
                customUserDetails.getId(),
                customUserDetails.getEmail(),
                token
        );
    }
}
