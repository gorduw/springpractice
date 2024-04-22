package testbean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CD {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(CD.class, args);
        ClassA classA = context.getBean(ClassA.class);
        classA.methodA();
    }
}


