package smoketest.data.jpa;

import org.easypeelsecurity.springdog.autoconfigure.applier.SpringDogEnable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringDogEnable
@SpringBootApplication
public class SampleDataJpaApplication {

  public static void main(String[] args) {
    SpringApplication.run(SampleDataJpaApplication.class, args);
  }

}
