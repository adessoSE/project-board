package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test-data")
@Component
public class UserMockData {

    private final UserService userService;

    @Autowired
    public UserMockData(UserService userService) {
        this.userService = userService;

        addUsers();
    }

    private void addUsers() {
        // creating mock-users for test-reasons
        SuperUser jacobs = new SuperUser("jacobs");
        jacobs.setFirstName("Lottie");
        jacobs.setLastName("Jacobs");
        jacobs.setEmail("lottie.jacobs@adesso.de");
        jacobs.setLob("LOB Banking");
        userService.save(jacobs);

        User good = new User("good", jacobs);
        good.setFirstName("Knapp");
        good.setLastName("Good");
        good.setEmail("good.knapp@adesso.de");
        good.setLob("LOB Banking");
        userService.save(good);

        User holt = new User("holt", jacobs);
        holt.setFirstName("Willie");
        holt.setLastName("Holt");
        holt.setEmail("willie.holt@adesso.de");
        holt.setLob("LOB Banking");
        userService.save(holt);

        User roy = new User("roy", jacobs);
        roy.setFirstName("Viola");
        roy.setLastName("Roy");
        roy.setEmail("viola.roy@adesso.de");
        roy.setLob("LOB Banking");
        userService.save(roy);

        User lambert = new User("lambert", jacobs);
        lambert.setFirstName("Leta");
        lambert.setLastName("Lambert");
        lambert.setEmail("leta.lambert@adesso.de");
        lambert.setLob("LOB Cross Industries");
        userService.save(lambert);

        User hoffman = new User("hoffman", jacobs);
        hoffman.setFirstName("Shannon");
        hoffman.setLastName("Hoffman");
        hoffman.setEmail("shannon.hoffman@adesso.de");
        hoffman.setLob("LOB Automotive");
        userService.save(hoffman);

        User key = new User("key", jacobs);
        key.setFirstName("Rosanna");
        key.setLastName("Key");
        key.setEmail("rosanna.key@adesso.de");
        key.setLob("LOB Banking");
        userService.save(key);

        User wiley = new User("wiley", jacobs);
        wiley.setFirstName("Jimenez");
        wiley.setLastName("Wiley");
        wiley.setEmail("jimenez.wiley@adesso.de");
        wiley.setLob("LOB Banking");
        userService.save(wiley);

        User clements = new User("clements", jacobs);
        clements.setFirstName("Carmela");
        clements.setLastName("Clements");
        clements.setEmail("carmela.clements@adesso.de");
        clements.setLob("LOB Banking");
        userService.save(clements);

        User cox = new User("cox", jacobs);
        cox.setFirstName("Alexandria");
        cox.setLastName("Cox");
        cox.setEmail("alexandria.cox@adesso.de");
        cox.setLob("LOB Banking");
        userService.save(cox);
    }

}
