package de.adesso.projectboard.base.normalizer;

import de.adesso.projectboard.base.project.persistence.Project;

import java.util.Set;

public class ProjectLobNormalizer extends LobNormalizer<Project> {

    protected ProjectLobNormalizer(Set<RootTermDistanceCalculator> lobDistanceCalculators) {
        super(lobDistanceCalculators);
    }

    @Override
    String getLobOf(Project lobContainingObject) {
        return lobContainingObject.getLob();
    }

    @Override
    Project setNormalizedLob(Project lobContainingObject, String normalizedLob) {
        return lobContainingObject.setLob(normalizedLob);
    }

}
