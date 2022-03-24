package io.factorialsystems.msscapiuser.security;

public class TenantContext {
    private static final ThreadLocal<Context> ctx = new ThreadLocal<>();

    public static String getToken() {
        return ctx.get().getToken();
    }

    public static void setContext(Context context ) {
       ctx.set(context);
    }

    public static void clear() {
        ctx.set(null);
    }
}
