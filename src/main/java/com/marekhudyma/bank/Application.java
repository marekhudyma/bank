package com.marekhudyma.bank;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;


@OpenAPIDefinition(
        info = @Info(
                title = "Bank",
                version = "1.0",
                description = "My API",
                license = @License(name = "Apache 2.0", url = "https://apache.org/"),
                contact = @Contact(url = "http://marekhudyma.com", name = "Marek", email = "marek.hudyma[at]gmail.com")
        )
)
public class Application {

    public static void main(String[] args) {
        Database.startDatabase();
        Micronaut.run(Application.class);
    }
}