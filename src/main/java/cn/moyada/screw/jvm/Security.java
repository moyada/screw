package cn.moyada.screw.jvm;

/**
 * @author xueyikang
 * @since 1.0
 **/
public interface Security {
    // -Djava.security.policy==src/main/resources/my.policy

    default void usePolicy(String url) {
        System.setProperty("java.security.policy", url);
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
    }
}
