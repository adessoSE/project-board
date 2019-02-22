package de.adesso.projectboard.base.user.persistence.data;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.*;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Indexed
@Entity
@Table(name = "PB_USER_DATA")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @IndexedEmbedded(
            includeEmbeddedObjectId = true,
            prefix = "user_"
    )
    @OneToOne
    @JoinColumn(
            name = "USER_ID",
            nullable = false
    )
    User user;

    @Field
    @Column(name = "FIRST_NAME")
    @NotEmpty
    String firstName;

    @Field
    @Column(name = "LAST_NAME")
    @NotEmpty
    String lastName;

    @Column(name = "EMAIL")
    @NotEmpty
    String email;

    @Column(name = "LOB")
    String lob;

    @Lob
    @Column(name = "PICTURE", length = 102400)
    @Basic(fetch = FetchType.LAZY)
    byte[] picture;

    @Column(name = "IS_PICTURE_INITIALIZED")
    boolean pictureInitialized;

    public UserData(User user, String firstName, String lastName, String email, String lob) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.lob = lob;
    }

    public UserData(User user, String firstName, String lastName, String email, String lob, byte[] picture) {
        this(user, firstName, lastName, email, lob);

        this.picture = picture;
        this.pictureInitialized = true;
    }

    /**
     *
     * @return
     *          The full name in a {@code [first name]-[last name]} form.
     */
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

}
