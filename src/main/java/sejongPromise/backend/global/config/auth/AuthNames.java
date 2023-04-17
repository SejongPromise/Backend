package sejongPromise.backend.global.config.auth;

public class AuthNames {
    public static final String ROLE_STUDENT = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static String combine(String... names){
        return String.join(",", names);
    }
}
