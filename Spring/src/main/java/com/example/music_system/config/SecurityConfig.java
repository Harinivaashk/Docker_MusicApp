package com.example.music_system.config;


import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import com.example.music_system.config.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {


   private final JwtAuthenticationFilter jwtAuthenticationFilter;


   @Autowired
   public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
       this.jwtAuthenticationFilter = jwtAuthenticationFilter;
   }


   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http.csrf(csrf -> csrf.disable())
       .cors(Customizer.withDefaults()) 
           .authorizeHttpRequests(auth -> auth
               .requestMatchers("/api/registerUser", "/api/loginUser").permitAll()
               .anyRequest().authenticated()
           )
           .formLogin(Customizer.withDefaults())
           .httpBasic(Customizer.withDefaults())
           .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

       return http.build();
   }


   @Bean
public CorsConfigurationSource corsConfigurationSource() {
   CorsConfiguration configuration = new CorsConfiguration();
   configuration.setAllowedOrigins(List.of("http://localhost:5173"));
   configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
   configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
   configuration.setExposedHeaders(List.of("Authorization"));
   configuration.setAllowCredentials(true);


   UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
   source.registerCorsConfiguration("/api/**", configuration);
   return source;
}
}
