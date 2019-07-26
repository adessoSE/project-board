package de.adesso.projectboard.base.normalizer;

import de.adesso.projectboard.base.project.persistence.Project;

import java.util.Set;

public class ProjectLobNormalizer extends FieldNormalizer<Project> {

    protected ProjectLobNormalizer(Set<RootTermDistanceCalculator> lobDistanceCalculators) {
        super(lobDistanceCalculators);
    }

    @Override
    String getFieldValue(Project fieldContainingObject) {
        return fieldContainingObject.getLob();
    }

    @Override
    Project setNormalizedFieldValue(Project fieldContainingObject, String normalizedValue) {
        return fieldContainingObject.setLob(normalizedValue);
    }

}
