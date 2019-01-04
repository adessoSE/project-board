package de.adesso.projectboard.base.user.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Getter
@EqualsAndHashCode
public class ParentChildWrapper<T> {

    private final T parent;

    private final List<ParentChildWrapper<T>> children;

    public ParentChildWrapper(T parent, List<ParentChildWrapper<T>> children) {
        this.parent = parent;
        this.children = children;
    }

    public ParentChildWrapper(T parent) {
        this(parent, Collections.emptyList());
    }

    public <S> ParentChildWrapper<S> map(Function<T, S> function) {
        return null;
    }

}
