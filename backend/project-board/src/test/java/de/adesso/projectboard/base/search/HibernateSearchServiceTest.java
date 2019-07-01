package de.adesso.projectboard.base.search;

import helper.base.search.IndexedEntity;
import helper.base.search.IndexedEntityWithoutFields;
import helper.base.search.NonIndexedEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HibernateSearchServiceTest {

    private HibernateSearchService hibernateSearchService;

    @Before
    public void setUp() {
        this.hibernateSearchService = new HibernateSearchService(Set.of(), Set.of("value"));
    }

    @Test
    public void constructorThrowsExceptionWhenStatusSetsNotDisjoint() {
        // given
        var statusWithLobConstraint = Set.of("value1");
        var statusWithoutLobConstraint = Set.of("value1");

        // when / then
        assertThatThrownBy(() -> new HibernateSearchService(statusWithLobConstraint, statusWithoutLobConstraint))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The status sets are not disjoint");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getNamesOfAnnotatedStringFieldsGetsNamesOfAnnotatedStringFieldsAndCachesResultWhenNotPresent() {
        // given
        var expectedFieldNames = List.of("renamed_field", "secondField");

        // when
        var actualFieldNames = hibernateSearchService.getNamesOfAnnotatedStringFields(IndexedEntity.class);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualFieldNames).containsExactlyInAnyOrderElementsOf(expectedFieldNames);
        softly.assertThat(hibernateSearchService.classIndexedFieldMap).containsExactly(
                Map.entry(IndexedEntity.class, expectedFieldNames)
        );

        softly.assertAll();
    }

    @Test
    public void getNamesOfAnnotatedStringFieldsReturnsCachedListWhenPresent() {
        // given
        var cachedFieldNames = List.of("field_1", "field_2");
        hibernateSearchService.classIndexedFieldMap.put(IndexedEntity.class, cachedFieldNames);

        // when
        var actualFieldNames = hibernateSearchService.getNamesOfAnnotatedStringFields(IndexedEntity.class);

        // then
        assertThat(actualFieldNames).containsExactlyInAnyOrderElementsOf(cachedFieldNames);
    }

    @Test
    public void isIndexedEntityReturnsFalseWhenNotAnEntity() {
        // given / when / then
        assertThat(hibernateSearchService.isIndexedEntity(String.class)).isFalse();
    }

    @Test
    public void isIndexedEntityReturnsFalseWhenEntityNotIndexed() {
        // given / when / then
        assertThat(hibernateSearchService.isIndexedEntity(NonIndexedEntity.class)).isFalse();
    }

    @Test
    public void isIndexedEntityReturnsTrueWhenEntityIsIndexed() {
        // given / when / then
        assertThat(hibernateSearchService.isIndexedEntity(IndexedEntity.class)).isTrue();
    }

    @Test
    public void getQuerySearchingForAllIndexedFieldsThrowsExceptionWhenNotAnEntity() {
        // given / when / then
        assertThatThrownBy(() -> hibernateSearchService.getQuerySearchingForAllIndexedFields(String.class, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given type is not an entity or is not indexed!");
    }

    @Test
    public void getQuerySearchingForAllIndexedFieldsThrowsExceptionWhenEntityNotIndexed() {
        // given / when / then
        assertThatThrownBy(() -> hibernateSearchService.getQuerySearchingForAllIndexedFields(NonIndexedEntity.class, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given type is not an entity or is not indexed!");
    }

    @Test
    public void getQuerySearchingForAllIndexedFieldsThrowsExceptionWhenNoStringFieldsAnnotated() {
        // given / when / then
        assertThatThrownBy(() -> hibernateSearchService.getQuerySearchingForAllIndexedFields(IndexedEntityWithoutFields.class, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No field of type String annotated with @Field!");
    }

}
