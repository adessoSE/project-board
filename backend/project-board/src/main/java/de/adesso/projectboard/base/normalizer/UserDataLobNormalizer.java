package de.adesso.projectboard.base.normalizer;

import de.adesso.projectboard.base.user.persistence.data.UserData;

import java.util.Set;

public class UserDataLobNormalizer extends FieldNormalizer<UserData> {

    protected UserDataLobNormalizer(Set<RootTermDistanceCalculator> lobDistanceCalculators) {
        super(lobDistanceCalculators);
    }

    @Override
    String getFieldValue(UserData fieldContainingObject) {
        return fieldContainingObject.getLob();
    }

    @Override
    UserData setNormalizedFieldValue(UserData fieldContainingObject, String normalizedValue) {
        return fieldContainingObject.setLob(normalizedValue);
    }

}
