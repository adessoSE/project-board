package de.adesso.projectboard.base.search;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

@Entity
@Indexed
public class IndexedEntity {

    @Field(name = "renamed_field")
    private String firstField;

    @Field
    private String secondField;

    @Field
    private Integer thirdField;

}
