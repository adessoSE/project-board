package de.adesso.projectboard.base.normalizer;

import de.adesso.projectboard.base.user.persistence.data.UserData;

import java.util.Set;

public class UserDataLobNormalizer extends LobNormalizer<UserData> {

    protected UserDataLobNormalizer(Set<RootTermDistanceCalculator> lobDistanceCalculators) {
        super(lobDistanceCalculators);
    }

    @Override
    String getLobOf(UserData lobContainingObject) {
        return lobContainingObject.getLob();
    }

    @Override
    UserData setNormalizedLob(UserData lobContainingObject, String normalizedLob) {
        return lobContainingObject.setLob(normalizedLob);
    }

}
