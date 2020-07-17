package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* make sure controller are under main app package */
@SpringBootApplication
public class FpApplication {

    public static void main(String[] args) {
        SpringApplication.run(FpApplication.class, args);
    }

}