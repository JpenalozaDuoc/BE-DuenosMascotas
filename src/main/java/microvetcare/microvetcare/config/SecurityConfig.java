package microvetcare.microvetcare.config;

import java.util.Arrays; // Importa Arrays
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Importa SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter; // <-- ¡Nueva importación!

@Configuration // <-- ¡Cambio a @Configuration!
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize en tus controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs REST
            .authorizeHttpRequests(authorize -> authorize
                // Puedes permitir rutas públicas aquí si es necesario (ej. /actuator/health)
                // .requestMatchers("/public/**", "/actuator/health").permitAll()
                // Todas las demás peticiones requieren autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Configura el conversor de roles
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // APIs REST no mantienen estado de sesión
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Usa tu bean de CORS

        return http.build();
    }

    // --- Converter para extraer roles de Keycloak del JWT y mapearlos a Spring Security ---
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter()); // Usará la clase que crearemos
        return converter;
    }

    // --- Configuración de CORS (tu bean actual es bueno, solo lo ajustamos para que se use explícitamente) ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes permitidos - Ajusta esto para tu frontend real en producción
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // O Collections.singletonList("http://localhost:4200");
        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Encabezados permitidos - Importante incluir Authorization para JWTs
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With")); // Agregué X-Requested-With
        // Permite el envío de credenciales (como el encabezado Authorization)
        configuration.setAllowCredentials(true);
        // Tiempo que los resultados de preflight pueden ser cacheados
        configuration.setMaxAge(3600L); // 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a todas las rutas
        return source;
    }

    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactivar CSRF para APIs REST
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // APIs REST usan STATELESS
            .cors(Customizer.withDefaults()) // <--- ¡AQUÍ LA CLAVE! Conecta tu CorsConfigurationSource bean
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // Por ahora, permitimos todo en /api/** para CORS
                                                      // Después puedes refinar con hasRole, authenticated, etc.
                .anyRequest().authenticated() // El resto requiere autenticación
            );

        // Si tu configuración de Keycloak es más específica y necesitas filtros custom:
        // KeycloakConfiguration suele añadir filtros automáticamente, pero si necesitas
        // un control más fino, aquí se añadirían los filtros de Keycloak,
        // por ejemplo, para proteger ciertos endpoints con Keycloak.
        // Asegúrate de que los filtros de Keycloak estén en el orden correcto
        // y que no anulen tu configuración de CORS si no tienen su propia.

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes permitidos - Asegúrate que sean EXACTOS
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Métodos permitidos - Incluye OPTIONS para preflight requests
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Encabezados permitidos - Importante incluir Authorization para JWTs de Keycloak
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        // Permite credenciales (necesario si envías Authorization headers o cookies)
        configuration.setAllowCredentials(true);
        // Tiempo que los resultados de preflight pueden ser cacheaddos
        configuration.setMaxAge(3600L); // 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuración CORS a todas las rutas.
        // Si solo quieres /api/**, cámbialo a "/api/**"
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Si estás usando Keycloak, también podrías necesitar beans para AuthenticationManager,
    // o para configurar el adaptador de Keycloak si no lo hace automáticamente con @KeycloakConfiguration.
    // Esto dependerá de tu versión y configuración de Keycloak.
    // Ejemplo (puede variar):
    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    //    return authConfig.getAuthenticationManager();
    // }
    */
}