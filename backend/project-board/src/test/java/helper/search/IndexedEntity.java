package helper.search;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

@Entity
@Indexed
public class IndexedEntity {

    @Field(name = "renamed_field")
    String firstField;

    @Field
    String secondField;

    @Field
    Integer thirdField;

}
