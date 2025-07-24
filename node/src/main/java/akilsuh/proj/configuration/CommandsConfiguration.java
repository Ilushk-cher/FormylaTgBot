package akilsuh.proj.configuration;

import akilsuh.proj.service.commands.AddChildCommand;
import akilsuh.proj.service.commands.RegistrationCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandsConfiguration {
    @Bean
    public RegistrationCommand registrationCommand() {
        return new RegistrationCommand();
    }

    @Bean
    public AddChildCommand addChildCommand() {
        return new AddChildCommand();
    }
}
