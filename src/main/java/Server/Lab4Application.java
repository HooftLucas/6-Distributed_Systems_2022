package Server;

import Node.Node;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab4Application {

    /**
     * The main function is the entry point of the application. It creates a new SpringApplication object and calls the run
     * method
     */
    public static void main(String[] args) {
        SpringApplication springApp = new SpringApplication(Lab4Application.class);
        springApp.run(args);
        }
}


