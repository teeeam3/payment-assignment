package sparta.paymentassignment.common.entity;

import jakarta.persistence.EntityListeners;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
}
