package de.adesso.projectboard.ad.service.mapper;

import org.junit.Before;
import org.junit.Test;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ThumbnailPhotoMapperTest {

    private final String ID_ATTR_VALUE = "sAMAccountName";

    private ThumbnailPhotoMapper mapper;

    @Before
    public void setUp() {
        this.mapper = new ThumbnailPhotoMapper(ID_ATTR_VALUE);
    }

    @Test
    public void mapFromAttributes() throws NamingException {
        // given
        var expectedId = "user-id";
        var expectedThumbnailPhoto = new byte[] {-2, 10, 39, 32};
        var expectedEntry = Map.entry(expectedId, expectedThumbnailPhoto);

        var idAttr = new BasicAttribute(ID_ATTR_VALUE, expectedId);
        var thumbnailPhotoAttr = new BasicAttribute("thumbnailPhoto", expectedThumbnailPhoto);

        var attributes = new BasicAttributes();
        attributes.put(idAttr);
        attributes.put(thumbnailPhotoAttr);

        // when
        var actualEntry = mapper.mapFromAttributes(attributes);

        // then
        assertThat(actualEntry).isEqualTo(expectedEntry);
    }

}