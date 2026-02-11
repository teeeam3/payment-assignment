package sparta.paymentassignment.domain.user.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.user.User;
import sparta.paymentassignment.domain.user.dto.*;
import sparta.paymentassignment.domain.user.UserRole;
import sparta.paymentassignment.exception.EmailDuplicationException;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.domain.user.repository.UserRepository;
import sparta.paymentassignment.exception.UserNotFoundException;
import sparta.paymentassignment.security.CustomUserDetails;
import sparta.paymentassignment.security.JwtTokenProvider;

import java.util.UUID;

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
                UserRole.USER,
                0L
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

    @Transactional(readOnly = true)
    public GetUserResponse getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
        );

        String rand = UUID.randomUUID().toString().substring(0, 6);
        String customerUid = "CUST_" + user.getId() + "_" + rand;

        return new GetUserResponse(
                user.getEmail(),
                customerUid,
                user.getName(),
                user.getPhone(),
                user.getPointBalance()
        );
    }

    public int updatePointByUserId(Long userId, BigDecimal point) {
      return userRepository.updatePointByUserId(userId, point);
    }
}
