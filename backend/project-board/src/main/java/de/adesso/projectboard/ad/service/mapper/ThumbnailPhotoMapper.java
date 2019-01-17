package de.adesso.projectboard.ad.service.mapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Map;

public class ThumbnailPhotoMapper extends BaseAttributeMapper<Map.Entry<String, byte[]>> {

    private final String idAttribute;

    public ThumbnailPhotoMapper(String idAttribute) {
        this.idAttribute = idAttribute;
    }

    @Override
    public Map.Entry<String, byte[]> mapFromAttributes(Attributes attributes) throws NamingException {
        var id = getSingleAttributeValue(attributes, idAttribute, String.class);
        var thumbnailPhoto = getSingleAttributeValue(attributes, "thumbnailPhoto", byte[].class);

        return Map.entry(id, thumbnailPhoto);
    }

}
