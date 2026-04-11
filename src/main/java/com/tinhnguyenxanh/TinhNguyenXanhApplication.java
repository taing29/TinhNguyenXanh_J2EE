package com.tinhnguyenxanh;

import com.tinhnguyenxanh.config.FlexiblePortCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TinhNguyenXanhApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TinhNguyenXanhApplication.class);
        app.addInitializers(new FlexiblePortCustomizer());
        app.run(args);
    }
}
