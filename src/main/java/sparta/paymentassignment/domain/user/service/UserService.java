package sparta.paymentassignment.domain.user.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparta.paymentassignment.domain.membership.MembershipPolicy;
import sparta.paymentassignment.domain.membership.rpository.MembershipPolicyRepository;
import sparta.paymentassignment.domain.point.Point;
import sparta.paymentassignment.domain.point.exception.InsufficientPointException;
import sparta.paymentassignment.domain.user.User;
import sparta.paymentassignment.domain.user.UserMembership;
import sparta.paymentassignment.domain.user.dto.*;
import sparta.paymentassignment.domain.user.UserRole;
import sparta.paymentassignment.domain.user.repository.UserMembershipRepository;
import sparta.paymentassignment.exception.EmailDuplicationException;
import sparta.paymentassignment.exception.ErrorCode;
import sparta.paymentassignment.domain.user.repository.UserRepository;
import sparta.paymentassignment.exception.MembershipPolicyNotFoundException;
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
    private final MembershipPolicyRepository membershipPolicyRepository;
    private final UserMembershipRepository userMembershipRepository;

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
                BigDecimal.ZERO
        );
        userRepository.save(user);

        // 멤버쉽 기본 등급 자동 생성
        // 정책 테이블에서 NORMAL 정책 조회
        MembershipPolicy membershipPolicy = membershipPolicyRepository.findByGrade("NORMAL").orElseThrow(
                () -> new MembershipPolicyNotFoundException(ErrorCode.MEMBERSHIP_POLICY_NOT_FOUND)
        );

        UserMembership userMembership = new UserMembership(
                user, membershipPolicy.getGrade(), BigDecimal.ZERO
        );

        userMembershipRepository.save(userMembership);
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

    @Transactional
    public int updatePointByUserId(Long userId, BigDecimal point) {
      return userRepository.incrementPoint(userId, point);
    }

  @Transactional
  public int retrievePoint(Long userId, BigDecimal points) {
    int updatedRows = userRepository.decrementPoint(userId, points);
    if (updatedRows == 0) {
      // 잔액이 부족하거나 유저가 없는 경우
      throw new InsufficientPointException("포인트 잔액이 부족하여 회수할 수 없습니다.");
    }
    return updatedRows;
  }

}
