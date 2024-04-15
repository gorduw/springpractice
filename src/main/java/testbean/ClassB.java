package testbean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ClassB {

    private ClassA classA;

    @Autowired
    public ClassB(@Lazy ClassA classA) {
        this.classA = classA;
    }

    public void methodB() {
        System.out.println("Class B");
    }
}
