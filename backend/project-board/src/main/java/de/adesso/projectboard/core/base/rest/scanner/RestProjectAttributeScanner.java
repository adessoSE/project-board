package de.adesso.projectboard.core.base.rest.scanner;

import de.adesso.projectboard.core.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public boolean canQuery(String queryName) {
        return queryNameAttributeMap.containsKey(queryName);
    }

    public Set<String> getQueryNames() {
        return queryNameAttributeMap.keySet();
    }

    public String getFieldNameByQueryName(String queryName) {
        return queryNameAttributeMap.get(queryName);
    }

}
