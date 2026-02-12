package sparta.paymentassignment.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sparta.paymentassignment.domain.user.dto.UserMembershipResponse;
import sparta.paymentassignment.domain.user.service.UserMembershipService;

@RestController
@RequiredArgsConstructor
public class UserMembershipController {
    private final UserMembershipService userMembershipService;

    @PostMapping("/membership/{userId}")
    public void updateMembership(@PathVariable Long userId) {
        userMembershipService.updateMembership(userId);
    }

    @GetMapping("/membership/{userId}")
    public ResponseEntity<UserMembershipResponse> getMyMembership(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userMembershipService.getMyMembership(userId));
    }
}
