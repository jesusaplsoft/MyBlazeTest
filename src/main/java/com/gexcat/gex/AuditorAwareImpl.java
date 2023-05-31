package com.gexcat.gex;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    public static String USER;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(USER);
    }
}
