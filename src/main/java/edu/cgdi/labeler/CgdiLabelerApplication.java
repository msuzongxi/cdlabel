package edu.cgdi.labeler;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("edu.cgdi.labeler.mapper")
public class CgdiLabelerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CgdiLabelerApplication.class, args);
    }
}
