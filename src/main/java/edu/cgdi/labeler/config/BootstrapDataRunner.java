package edu.cgdi.labeler.config;

import edu.cgdi.labeler.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(10)
@ConditionalOnProperty(name = "cgdi.bootstrap.enabled", havingValue = "true")
public class BootstrapDataRunner implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${cgdi.bootstrap.admin-password:Temp12345!}")
    private String adminPassword;

    @Value("${cgdi.bootstrap.labeler1-password:johndoor}")
    private String labeler1Password;

    @Value("${cgdi.bootstrap.labeler2-password:marykey}")
    private String labeler2Password;

    @Value("${cgdi.bootstrap.labeler3-password:dummy}")
    private String labeler3Password;

    @Value("${cgdi.bootstrap.supervisor-password:donglai}")
    private String supervisorPassword;

    public BootstrapDataRunner(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userMapper.insertIfAbsent("admin", passwordEncoder.encode(adminPassword), "ADMIN");
        userMapper.insertIfAbsent("johndoor", passwordEncoder.encode(labeler1Password), "LABELER");
        userMapper.insertIfAbsent("marykey", passwordEncoder.encode(labeler2Password), "LABELER");
        userMapper.insertIfAbsent("dummy", passwordEncoder.encode(labeler3Password), "LABELER");
        userMapper.insertIfAbsent("donglai", passwordEncoder.encode(supervisorPassword), "SUPERVISOR");
    }
}
