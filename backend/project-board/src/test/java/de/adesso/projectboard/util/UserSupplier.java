package de.adesso.projectboard.util;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.Getter;

@Getter
public class UserSupplier {

    private User firstUser;

    private User secondUser;

    private SuperUser firstSuperUser;

    private SuperUser secondSuperUser;

    public UserSupplier() {
        setUpUsers();
    }

    public void resetUsers() {
        setUpUsers();
    }

    private void setUpUsers() {
        this.firstSuperUser = new SuperUser("first-super-user");
        this.firstSuperUser.setFullName("First Super Test", "User");
        this.firstSuperUser.setEmail("first-super-test-user@test.com");
        this.firstSuperUser.setLob("LOB Test");

        this.firstUser = new User("first-user", firstSuperUser);
        this.firstUser.setFullName("First Test", "User");
        this.firstUser.setEmail("first-test-user@test.com");
        this.firstUser.setLob("LOB Test");

        this.secondSuperUser = new SuperUser("second-super-user");
        this.secondSuperUser.setFullName("Second Super Test", "User");
        this.secondSuperUser.setEmail("second-super-test-user@test.com");
        this.secondSuperUser.setLob("LOB Test");

        this.secondUser = new User("second-user", secondSuperUser);
        this.secondUser.setFullName("First Test", "User");
        this.secondUser.setEmail("first-test-user@test.com");
        this.secondUser.setLob("LOB Test");
    }

}
