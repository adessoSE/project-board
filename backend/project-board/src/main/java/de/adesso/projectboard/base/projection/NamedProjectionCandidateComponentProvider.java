package de.adesso.projectboard.base.projection;

import org.springframework.stereotype.Component;

/**
 * {@link InterfaceCandidateComponentProvider} to find interfaces annotated with the
 * {@link NamedProjection} annotation.
 */
@Component
public class NamedProjectionCandidateComponentProvider extends InterfaceCandidateComponentProvider<NamedProjection> {

    public NamedProjectionCandidateComponentProvider() {
        super(NamedProjection.class);
    }

}
