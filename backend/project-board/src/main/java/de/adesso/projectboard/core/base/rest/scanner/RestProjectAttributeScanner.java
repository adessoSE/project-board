package de.adesso.projectboard.core.base.rest.scanner;

import de.adesso.projectboard.core.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Used to scan the {@link AbstractProject project class} to map field names
 * to query names.
 *
 * @see QueryName
 * @see de.adesso.projectboard.core.base.rest.project.ProjectController
 */
@Component
public class RestProjectAttributeScanner {

    private final Map<String, String> queryNameAttributeMap;

    private final ProjectBoardConfigurationProperties properties;

    @Autowired
    public RestProjectAttributeScanner(ProjectBoardConfigurationProperties properties) throws FieldsWithSameQueryNameException {
        this.queryNameAttributeMap = new LinkedHashMap<>();
        this.properties = properties;

        this.scanProjectClassAttributes();
    }

    /**
     * Build a map that maps the query name of a field
     * to the real name of the field. Used for searching projects.
     *
     * @throws FieldsWithSameQueryNameException
     *          When a naming conflict occurs.
     */
    private void scanProjectClassAttributes() throws FieldsWithSameQueryNameException {
        Class<? extends AbstractProject> projectClass = properties.getProjectClass();

        for(Field field : projectClass.getDeclaredFields()) {
            String fieldName = field.getName();
            String queryName;

            if(field.isAnnotationPresent(QueryName.class)) {
                queryName = field.getAnnotation(QueryName.class).value();
            } else {
                queryName = fieldName;
            }

            if(queryNameAttributeMap.containsKey(queryName)) {
                String otherFieldName = queryNameAttributeMap.get(queryName);

                throw new FieldsWithSameQueryNameException(fieldName, otherFieldName);
            } else {
                queryNameAttributeMap.put(queryName, fieldName);
            }
        }
    }

    /**
     *
     * @param queryName
     *          The name of the query parameter.
     *
     * @return
     *          The result of {@link Map#containsKey(Object)}.
     */
    public boolean canQuery(String queryName) {
        return queryNameAttributeMap.containsKey(queryName);
    }

    /**
     *
     * @return
     *          A set of all valid query parameter names.
     */
    public Set<String> getQueryNames() {
        return queryNameAttributeMap.keySet();
    }

    /**
     *
     * @param queryName
     *          The query parameter name.
     *
     * @return
     *          The corresponding field's name.
     */
    public String getFieldNameByQueryName(String queryName) {
        return queryNameAttributeMap.get(queryName);
    }

}
