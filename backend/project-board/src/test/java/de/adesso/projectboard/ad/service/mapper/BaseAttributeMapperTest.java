package de.adesso.projectboard.ad.service.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BaseAttributeMapperTest {

    private final String ATTRIBUTE_ID = "attr";

    @Mock
    private Attributes attributesMock;

    @Mock
    private Attribute attributeMock;

    @Mock
    private NamingEnumeration enumerationMock;

    private BaseAttributeMapper<String> baseAttributeMapper;

    @Before
    public void setUp() {
        this.baseAttributeMapper = new BaseAttributeMapper<>() {
            @Override
            public String mapFromAttributes(javax.naming.directory.Attributes attributes) {
                return "";
            }
        };
    }

    @Test
    public void getSingleAttrValueReturnsValue() throws NamingException {
        // given
        var expectedValue = "Test Value";

        given(attributeMock.size()).willReturn(1);
        given(attributeMock.get()).willReturn(expectedValue);

        given(attributesMock.get(ATTRIBUTE_ID)).willReturn(attributeMock);

        // when / then
        compareGetSingleAttrResult(attributesMock, expectedValue);
    }

    @Test
    public void getSingleAttrValueReturnsNullWhenNoValuePresent() throws NamingException {
        // given
        given(attributesMock.get(ATTRIBUTE_ID)).willReturn(attributeMock);

        // when / then
        compareGetSingleAttrResult(attributesMock, null);
    }

    @Test
    public void getSingleAttrValueReturnsNullWhenNoAttrPresent() throws NamingException {
        // given

        // when / then
        compareGetSingleAttrResult(attributesMock, null);
    }

    @Test
    public void getAllAttrValuesReturnsValues() throws NamingException {
        // given
        var firstExpectedValue = "Test";
        var secondExpectedValue = "Value";
        var expectedResult = Set.of(firstExpectedValue, secondExpectedValue);

        given(enumerationMock.hasMore()).willReturn(true, true, false);
        given(enumerationMock.next()).willReturn(firstExpectedValue, secondExpectedValue);

        given(attributeMock.getAll()).willReturn(enumerationMock);

        given(attributesMock.get(ATTRIBUTE_ID)).willReturn(attributeMock);

        // when / then
        compareGetAllAttrValuesResult(attributesMock, expectedResult);
    }

    @Test
    public void getAllAttrValuesReturnsOnlyNonNullValues() throws NamingException {
        // given
        var expectedValue = "Test";
        var expectedResult = Set.of(expectedValue);

        given(enumerationMock.hasMore()).willReturn(true, true, false);
        given(enumerationMock.next()).willReturn(null, expectedValue);

        given(attributeMock.getAll()).willReturn(enumerationMock);

        given(attributesMock.get(ATTRIBUTE_ID)).willReturn(attributeMock);

        // when / then
        compareGetAllAttrValuesResult(attributesMock, expectedResult);
    }

    @Test
    public void getAllAttrValuesReturnsEmptyListWhenNoValuesPresent() throws NamingException {
        // given
        var expectedResult = new ArrayList<String>();

        given(attributeMock.getAll()).willReturn(enumerationMock);

        given(attributesMock.get(ATTRIBUTE_ID)).willReturn(attributeMock);

        // when / then
        compareGetAllAttrValuesResult(attributesMock, expectedResult);
    }

    @Test
    public void getAllAttrValuesReturnsEmptyListWhenNoAttributePresent() throws NamingException {
        var expectedResult = new ArrayList<String>();

        // when / then
        compareGetAllAttrValuesResult(attributesMock, expectedResult);
    }

    @Test
    public void getAttrByIdReturnsPopulatedOptionalWhenAttrPresent() {
        // given
        given(attributesMock.get(ATTRIBUTE_ID)).willReturn(attributeMock);

        // when
        var actualAttrOptional = baseAttributeMapper.getAttributeById(attributesMock, ATTRIBUTE_ID);

        // then
        assertThat(actualAttrOptional)
                .isPresent()
                .contains(attributeMock);
    }

    @Test
    public void getAttrByIdReturnsEmptyOptionalWhenAttrNotPresent() {
        // given

        // when
        var actualAttrOptional = baseAttributeMapper.getAttributeById(attributesMock, ATTRIBUTE_ID);

        // then
        assertThat(actualAttrOptional)
                .isNotPresent();
    }

    @Test
    public void castAttrValuesReturnsOnlyNonNullValues() throws NamingException {
        // given
        var expectedValue = "Test";
        var expectedResult = Set.of(expectedValue);

        given(enumerationMock.hasMore()).willReturn(true, true, false);
        given(enumerationMock.next()).willReturn(null, expectedValue);

        given(attributeMock.getAll()).willReturn(enumerationMock);

        // when / then
        compareCastAttrValues(attributeMock, expectedResult);
    }

    @Test
    public void castAttrValuesReturnsValues() throws NamingException {
        // given
        var firstExpectedValue = "Test";
        var secondExpectedValue = "Value";
        var expectedResult = Set.of(firstExpectedValue, secondExpectedValue);

        given(enumerationMock.hasMore()).willReturn(true, true, false);
        given(enumerationMock.next()).willReturn(firstExpectedValue, secondExpectedValue);

        given(attributeMock.getAll()).willReturn(enumerationMock);

        // when / then
        compareCastAttrValues(attributeMock, expectedResult);
    }

    private void compareGetSingleAttrResult(Attributes attributes, String expectedResult) throws NamingException {
        // when
        var actualValue = baseAttributeMapper.getSingleAttributeValue(attributes, ATTRIBUTE_ID, String.class);

        // then
        assertThat(actualValue).isEqualTo(expectedResult);
    }

    private void compareGetAllAttrValuesResult(Attributes attributes, Collection<String> expectedResult) throws NamingException {
        // when
        var actualValue = baseAttributeMapper.getAllAttributeValues(attributes, ATTRIBUTE_ID, String.class);

        // then
        assertThat(actualValue).containsExactlyInAnyOrderElementsOf(expectedResult);
    }

    private void compareCastAttrValues(Attribute attribute, Collection<String> expectedResult) throws NamingException {
        // when
        var actualResult = baseAttributeMapper.castAttributeValues(attribute, String.class);

        // then
        assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult);
    }

}