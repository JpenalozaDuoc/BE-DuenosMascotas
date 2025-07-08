package microvetcare.microvetcare.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Stream<GrantedAuthority> authorities = Stream.empty();

        Object realmAccessObj = jwt.getClaims().get("realm_access");
        Map<String, Object> realmAccess = null;
        if (realmAccessObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) realmAccessObj;
            realmAccess = casted;
        }
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof List<?>) {
                List<?> rolesList = (List<?>) rolesObj;
                List<String> realmRoles = rolesList.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .collect(Collectors.toList());
                authorities = Stream.concat(authorities, realmRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                );
            }
        }

        Object resourceAccessObj = jwt.getClaims().get("resource_access");
        Map<String, Object> resourceAccess = null;
        if (resourceAccessObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) resourceAccessObj;
            resourceAccess = casted;
        }
        if (resourceAccess != null) {
            Object clientAccessObj = resourceAccess.get("vetcare-app");
            Map<String, Object> clientAccess = null;
            if (clientAccessObj instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> castedClient = (Map<String, Object>) clientAccessObj;
                clientAccess = castedClient;
            }
            if (clientAccess != null && clientAccess.containsKey("roles")) {
                Object clientRolesObj = clientAccess.get("roles");
                if (clientRolesObj instanceof List<?>) {
                    List<?> clientRolesList = (List<?>) clientRolesObj;
                    List<String> clientRoles = clientRolesList.stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .collect(Collectors.toList());
                    authorities = Stream.concat(authorities, clientRoles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    );
                }
            }
        }
        
        return authorities.collect(Collectors.toList());
    }



}
