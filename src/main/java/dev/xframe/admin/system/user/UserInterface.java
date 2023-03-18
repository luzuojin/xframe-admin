package dev.xframe.admin.system.user;

public interface UserInterface {

    /**
     * throw LogicException when failed
     */
    void validate(String username, String password);

    default String makePhone(String username) {
        return "10086";
    }
    default String makeEmail(String username) {
        return username + "@xframe.dev";
    }

}
