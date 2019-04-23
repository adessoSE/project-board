package de.adesso.projectboard.util;

import helper.util.Car;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldTruncationUtilsTest {

    @Test
    public void doesNotTruncateFieldWhenStringShorterThanColumnLength() {
        // given
        var facilityName = "Dortmund";
        var modelName = "Cool Car";
        var ownerName = "Jane Doe";
        var productionDate = LocalDate.of(2019, 4, 23);
        var car = new Car(productionDate, facilityName, modelName, ownerName);

        // when
        var truncatedCar = FieldTruncationUtils.truncateStringsToColumnLengths(car);

        // then
        assertThat(truncatedCar).isEqualTo(car);
    }

    @Test
    public void doesNotTruncateFieldWhenStringNull() {
        // given
        var facilityName = "Dortmund";
        var modelName = "Cool Car";
        var productionDate = LocalDate.of(2019, 4, 23);
        var car = new Car(productionDate, facilityName, modelName, null);

        // when
        var truncatedCar = FieldTruncationUtils.truncateStringsToColumnLengths(car);

        // then
        assertThat(truncatedCar).isEqualTo(car);
    }

    @Test
    public void truncatesFieldToColumnAnnotationLengthWhenStringTooLong() {
        // given
        var facilityName = "Dortmund";
        var modelName = "";
        var productionDate = LocalDate.of(2019, 4, 23);
        var car = new Car(productionDate, facilityName, modelName, null);

        // when
        var truncatedCar = FieldTruncationUtils.truncateStringsToColumnLengths(car);

        // then
        assertThat(truncatedCar).isEqualTo(car);
    }

    @Test
    public void truncatesFieldWhenStringTooLong() {
        // given
        var facilityName = "Dortmund";
        var modelName = "Cool Car";
        var ownerName = "Very Longus Namus";
        var expectedOwnerName = "Very Longu";
        var productionDate = LocalDate.of(2019, 4, 23);
        var car = new Car(productionDate, facilityName, modelName, ownerName);
        var expectedCar = new Car(productionDate, facilityName, modelName, expectedOwnerName);

        // when
        var truncatedCar = FieldTruncationUtils.truncateStringsToColumnLengths(car);

        // then
        assertThat(truncatedCar).isEqualTo(expectedCar);
    }

    @Test
    public void getColumnLengthOfFieldReturnsColumnAnnotationLengthWhenPresent() throws NoSuchFieldException {
        // given
        var fieldCallBack = new FieldTruncationUtils.StringTruncationFieldCallback(null);
        var fieldName = "ownerName";
        var field = Car.class.getDeclaredField(fieldName);
        var expectedLength = 10;

        // when
        var actualLength = fieldCallBack.getColumnLengthOfField(field);

        // then
        assertThat(actualLength).isEqualTo(expectedLength);
    }

    @Test
    public void getColumnLengthOfFieldReturnsDefaultLengthWhenNoColumnAnnotationPresent() throws NoSuchFieldException {
        // given
        var fieldCallBack = new FieldTruncationUtils.StringTruncationFieldCallback(null);
        var fieldName = "modelName";
        var field = Car.class.getDeclaredField(fieldName);
        var expectedLength = FieldTruncationUtils.DEFAULT_COLUMN_LENGTH;

        // when
        var actualLength = fieldCallBack.getColumnLengthOfField(field);

        // then
        assertThat(actualLength).isEqualTo(expectedLength);
    }

    @Test
    public void truncateStringReturnsOriginalStringWhenColumnLengthGreater() {
        // given
        var fieldCallBack = new FieldTruncationUtils.StringTruncationFieldCallback(null);
        var originalString = "12345679";
        var columnLength = 20;

        // when
        var actualString = fieldCallBack.truncateString(originalString, columnLength);

        // then
        assertThat(actualString).isEqualTo(originalString);
    }

    @Test
    public void truncateStringReturnsOriginalStringWhenColumnLengthEqual() {
        // given
        var fieldCallBack = new FieldTruncationUtils.StringTruncationFieldCallback(null);
        var originalString = "12345679";
        var columnLength = 9;

        // when
        var actualString = fieldCallBack.truncateString(originalString, columnLength);

        // then
        assertThat(actualString).isEqualTo(originalString);
    }

    @Test
    public void truncateStringReturnsOriginalStringWhenColumnLengthSmaller() {
        // given
        var fieldCallBack = new FieldTruncationUtils.StringTruncationFieldCallback(null);
        var originalString = "12345679";
        var expectedString = "1234";
        var columnLength = 4;

        // when
        var actualString = fieldCallBack.truncateString(originalString, columnLength);

        // then
        assertThat(actualString).isEqualTo(expectedString);
    }

    @Test
    public void matchesReturnsTrueWhenFieldTypeIsStringAndNotFinal() throws NoSuchFieldException {
        // given
        var filter = new FieldTruncationUtils.StringFieldFilter();
        var fieldName = "modelName";
        var field = Car.class.getDeclaredField(fieldName);

        // when
        var actualMatches = filter.matches(field);

        // then
        assertThat(actualMatches).isTrue();
    }

    @Test
    public void matchesReturnsFalseWhenFieldTypeIsNotString() throws NoSuchFieldException {
        // given
        var filter = new FieldTruncationUtils.StringFieldFilter();
        var fieldName = "productionDate";
        var field = Car.class.getDeclaredField(fieldName);

        // when
        var actualMatches = filter.matches(field);

        // then
        assertThat(actualMatches).isFalse();
    }

    @Test
    public void matchesReturnsFalseWhenFieldTypeIsStringButFinal() throws NoSuchFieldException {
        // given
        var filter = new FieldTruncationUtils.StringFieldFilter();
        var fieldName = "facilityName";
        var field = Car.class.getDeclaredField(fieldName);

        // when
        var actualMatches = filter.matches(field);

        // then
        assertThat(actualMatches).isFalse();
    }

}
