package de.adesso.projectboard.base.normalizer;

import java.util.List;

public interface Normalizer<T> {

    List<T> normalize(List<T> objectsToNormalize);

}
