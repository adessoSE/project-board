package de.adesso.projectboard.util;

import org.assertj.core.api.SoftAssertions;

public class TestHelper {

    public static void assertEqualsAndHashCodeEquals(Object object, Object otherObject) {
        // when
        boolean actualEquals = object.equals(otherObject);

        int objectHash = object.hashCode();
        int otherObjectHash = otherObject.hashCode();

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(actualEquals).isTrue();
        softly.assertThat(objectHash).isEqualTo(otherObjectHash);

        softly.assertAll();
    }

}
