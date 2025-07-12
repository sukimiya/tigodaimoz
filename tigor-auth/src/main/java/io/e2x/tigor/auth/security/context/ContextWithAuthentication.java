package io.e2x.tigor.auth.security.context;

import org.springframework.security.core.Authentication;

public interface ContextWithAuthentication {

    Authentication getAuthentication();
}
