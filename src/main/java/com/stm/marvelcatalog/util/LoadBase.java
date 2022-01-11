package com.stm.marvelcatalog.util;

import com.stm.marvelcatalog.model.Customer;
import com.stm.marvelcatalog.repository.CustomerRepository;
import org.bson.types.Binary;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class LoadBase {
//    @Bean
    CommandLineRunner initLoadBase(CustomerRepository repository) {

        return args -> {
            var s = Files.readAllBytes(new ClassPathResource("test.jpg").getFile().toPath());
            repository.save(new Customer("test", "ext", new Binary(s)));
            repository.save(new Customer("abc", "cbd", new Binary(s)));
            repository.save(new Customer("abc", "ddd", new Binary(s)));
            repository.save(new Customer("abc", "sss", new Binary(s)));
            repository.save(new Customer("abc", "zzz", new Binary(s)));
        };

    }

}
