package de.adesso.projectboard.adapter.mail;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.validation.constraints.NotNull;
import java.io.StringWriter;
import java.util.Map;

/**
 * Service providing convenience methods for apache velocity templates.
 */
public class VelocityTemplateService {

    private final VelocityEngine velocityEngine;

    public VelocityTemplateService(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    /**
     *
     * @param templatePath
     *          The template path and name of the velocity template, not null.
     *
     * @param contextMap
     *          The key/value pairs to add to the velocity context storage, may be null.
     *
     * @return
     *          The result of merging to context and the template.
     */
    public String mergeTemplate(@NotNull String templatePath, Map<String, Object> contextMap) {
        var velocityTemplate = velocityEngine.getTemplate(templatePath);
        var velocityContext = new VelocityContext(contextMap);
        var writer = new StringWriter();
        velocityTemplate.merge(velocityContext, writer);

        return writer.toString();
    }

}
