package testbean;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        context.registerBean(MyBean.class);
        context.refresh();

        MyBean myBean = context.getBean(MyBean.class);

        myBean.printTime();


        context.removeBeanDefinition("myBean");


        System.out.println("We are going to the end of life");

        context.close();

    }

    @Bean
    public MyBean myBeanInstance() {
        return new MyBean();
    }
}

class MyBean implements DisposableBean {
    public void printTime() {
        System.out.println(LocalTime.now());
    }


    @Override
    public void destroy() throws Exception {
        System.out.println("Bean is destroyed");
    }
}
