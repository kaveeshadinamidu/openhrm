package com.openhrm.common.tenant;

import java.util.UUID;

// Request-scoped holder for the current organization id, populated by TenantFilter after auth.
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_ORG = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(UUID organizationId) {
        CURRENT_ORG.set(organizationId);
    }

    public static UUID get() {
        UUID organizationId = CURRENT_ORG.get();
        if (organizationId == null) {
            throw new IllegalStateException("No tenant set on the current request");
        }
        return organizationId;
    }

    public static void clear() {
        CURRENT_ORG.remove();
    }
}
