package helper.adapter.mail;

import org.apache.velocity.VelocityContext;
import org.mockito.ArgumentMatcher;

import java.util.Map;
import java.util.Objects;

public class VelocityContextArgumentMatcher implements ArgumentMatcher<VelocityContext> {

    private final Map<String, Object> referenceMap;

    public VelocityContextArgumentMatcher(Map<String, Object> referenceMap) {
        this.referenceMap = referenceMap;
    }

    @Override
    public boolean matches(VelocityContext ctx) {
        return referenceMap.entrySet().stream()
                .allMatch(entry -> Objects.nonNull(ctx.internalGet(entry.getKey()))) &&
                ctx.internalGetKeys().length == referenceMap.entrySet().size();
    }

}
