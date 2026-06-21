package tn.platform.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tn.platform.user.security.jwt.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class AletheiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AletheiaApplication.class, args);
    }

}
