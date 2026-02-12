package sparta.paymentassignment;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;
import sparta.paymentassignment.domain.product.entity.Product;
import sparta.paymentassignment.domain.product.entity.ProductStatus;
import sparta.paymentassignment.domain.product.repository.ProductRepository;

@EnableJpaAuditing
@SpringBootApplication
public class PaymentAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentAssignmentApplication.class, args);
    }

    @Bean
    public CommandLineRunner initProducts(ProductRepository productRepository) {
        return args -> {

            if (productRepository.count() > 0) return; // 중복 방지

            productRepository.save(new Product(
                    "아이폰 15 Pro",
                    1550000L,
                    10,
                    "애플 최신 플래그십 스마트폰",
                    ProductStatus.ACTIVE,
                    "SMARTPHONE"
            ));

            productRepository.save(new Product(
                    "갤럭시 S25 Ultra",
                    1450000L,
                    20,
                    "삼성 최신 안드로이드 스마트폰",
                    ProductStatus.ACTIVE,
                    "SMARTPHONE"
            ));

            productRepository.save(new Product(
                    "맥북 프로 M3",
                    3200000L,
                    5,
                    "애플 실리콘 M3 탑재 노트북",
                    ProductStatus.ACTIVE,
                    "LAPTOP"
            ));

            productRepository.save(new Product(
                    "에어팟 프로 2세대",
                    350000L,
                    50,
                    "노이즈 캔슬링 무선 이어폰",
                    ProductStatus.ACTIVE,
                    "ACCESSORY"
            ));

            productRepository.save(new Product(
                    "아이패드 Air 6",
                    900000L,
                    15,
                    "태블릿 PC",
                    ProductStatus.ACTIVE,
                    "TABLET"
            ));

            System.out.println("초기 상품 데이터 삽입 완료");
        };
    }

}



