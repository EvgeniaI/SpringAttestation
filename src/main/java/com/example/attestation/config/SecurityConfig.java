package com.example.attestation.config;

import com.example.attestation.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder getPasswordEncode(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //Конфигурируем работу Spring Security

        http
                .authorizeHttpRequests() //указываем,что все страницы должны быть защищены аутетнтификацией
                .requestMatchers("/admin").hasRole("ADMIN") //страница admin доступна только админу
                .requestMatchers("/authentication", "/registration", "/error", "/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/product", "/product/info/{id}", "/product/search").permitAll()
                .anyRequest().hasAnyRole("USER", "ADMIN") //для остальных страниц нужен метод аутентификации
                .and() //указываем, что дальше настраивается аутентификация и соединяем еёс настройкой доступа
                .formLogin().loginPage("/authentication") //указываем какой url запрос будет отправляться при заходе на защищенные страницы
                .loginProcessingUrl("/process_login") //на какой url адрес будут отправляться данные из формы
                .defaultSuccessUrl("/person_acc", true) //указываем на какой url отправить пользователя после успешной аутентификации. true для того, чтобы в любом случае шло перенаправление посе успешной аутентификации
                .failureUrl("/authentication?error") //указываем куда перенаправить пользователя в случае неудачной аутентификации. В запрос передается объект error и после проверки его наличия на форме выводится сообщение "неправильный логин или пароль"
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/authentication");
        return http.build();

    }
    private final PersonDetailsService personDetailsService;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    //Настройка аутентификации в рамках приложения

    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncode());
    }

}
