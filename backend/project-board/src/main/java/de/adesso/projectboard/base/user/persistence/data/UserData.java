package de.adesso.projectboard.base.user.persistence.data;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "USER_DATA")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(
            name = "USER_ID",
            nullable = false
    )
    User user;

    @NotEmpty
    String firstName;

    @NotEmpty
    String lastName;

    @NotEmpty
    String email;

    @NotEmpty
    String lob;

    /**
     *
     * @param user
     *          The {@link User} this instance belongs to.
     *
     * @param firstName
     *          The first name of the user.
     *
     * @param lastName
     *          The last name of the user.
     *
     * @param email
     *          The email of the user.
     *
     * @param lob
     *          The LoB of the user.
     */
    public UserData(User user, String firstName, String lastName, String email, String lob) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.lob = lob;
    }

}
