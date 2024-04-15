package testbean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class CD {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(CD.class, args);
        ClassA classA = context.getBean(ClassA.class);
        classA.methodA();
    }
}


