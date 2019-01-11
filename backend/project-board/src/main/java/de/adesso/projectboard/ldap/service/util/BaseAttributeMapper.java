package de.adesso.projectboard.ldap.service.util;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Abstract class implementing the {@link AttributesMapper} interface with convenience methods
 * to get a single value or multiple values of a {@link javax.naming.directory.Attribute}
 * with a given attribute ID.
 *
 * @param <T>  The type the attributes are mapped to.
 */
public abstract class BaseAttributeMapper<T> implements AttributesMapper<T> {

    /**
     * Retrieves a single value casted of the attribute with the given {@code attributeId} or {@code null}
     * if the the attribute does not exist or has no value.
     *
     * @param attributes
     *          The attributes to get the attribute value of, not null.
     *
     * @param attributeId
     *          The ID of the attribute to get the value of, not null.
     *
     * @param typeClass
     *          The class of the type of the attribute's value, not null.
     *
     * @param <S>
     *          The type of the attribute's value.
     *
     * @return
     *          The casted value of the attribute with the given {@code attributeId} or
     *          {@code null}, iff no attribute with the given {@code attributeId}
     *          exists or the attribute has no value.
     *
     * @throws NamingException
     *          When a naming exception is encountered while retrieving the attribute's value.
     *
     * @see #getAttributeById(Attributes, String)
     */
    protected <S> S getSingleAttributeValue(Attributes attributes, String attributeId, Class<S> typeClass) throws NamingException {
        requireNonNull(typeClass);

        var attributeOptional = getAttributeById(attributes, attributeId);

        if(attributeOptional.isPresent()) {
            var attributeWithId = attributeOptional.get();
            if(attributeWithId.size() > 0) {
                return typeClass.cast(attributeWithId.get());
            }
        }

        return null;
    }

    /**
     * Retrieves a list of all casted, non {@code null} attribute values of the attribute
     * with the given {@code attributeId}.
     *
     * @param attributes
     *          The attributes to get the attribute's values of, not null.
     *
     * @param attributeId
     *          The ID of the attribute to get the values of.
     *
     * @param typeClass
     *          The class of the type of the attribute's values, not null.
     *
     * @param <S>
     *          The type of the attribute's values.
     *
     * @return
     *          A list containing all casted, non {@code null} attribute values or an empty
     *          list if no attribute with the given {@code attributeId} was found.
     *
     * @throws NamingException
     *          When a naming exception is encountered while retrieving the attribute's values.
     *
     * @see #getSingleAttributeValue(Attributes, String, Class)
     */
    protected <S> List<S> getAllAttributeValues(Attributes attributes, String attributeId, Class<S> typeClass) throws NamingException {
        requireNonNull(typeClass);

        var attributeOptional = getAttributeById(attributes, attributeId);

        if(attributeOptional.isPresent()) {
           return castAttributeValues(attributeOptional.get(), typeClass);
        }

        return Collections.emptyList();
    }

    /**
     * Retrieves a attribute with a given {@code attributeId} from a {@code attributes} instance
     * and wraps inside of an optional.
     *
     * @param attributes
     *          The attributes to get the attribute of, not null.
     *
     * @param attributeId
     *          The ID of the attribute, not null.
     *
     * @return
     *          An Optional containing the attribute with the given {@code attributeId} or
     *          an empty optional of no attribute with that ID exists.
     */
    protected Optional<Attribute> getAttributeById(Attributes attributes, String attributeId) {
        requireNonNull(attributeId);
        requireNonNull(attributes);

        return Optional.ofNullable(attributes.get(attributeId));
    }

    /**
     * Retrieves a list of all casted, non {@code null} attribute values of the
     * given {@code attribute}.
     *
     * @param attribute
     *          The attribute to get the values of, not null.
     *
     * @param typeClass
     *          The class of the type of the attribute's values, not null.
     *
     * @param <S>
     *          The type of the the attribute's values.
     *
     * @return
     *          A list containing all casted, non {@code null} attribute values of the
     *          given {@code attribute}.
     *
     * @throws NamingException
     *          When a naming exception is encountered while retrieving the attribute's values.
     *
     */
    protected <S> List<S> castAttributeValues(Attribute attribute, Class<S> typeClass) throws NamingException {
        requireNonNull(attribute);
        requireNonNull(typeClass);

        var castedValues = new ArrayList<S>();
        var values = attribute.getAll();

        while(values.hasMore()) {
            var value = values.next();
            if(!isNull(value)) {
                castedValues.add(typeClass.cast(value));
            }
        }

        return castedValues;
    }

}
