package helper.util;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.Objects;

public class Car {

    public Car(LocalDate productionDate, String facilityName, String modelName, String ownerName) {
        this.productionDate = productionDate;
        this.facilityName = facilityName;
        this.modelName = modelName;
        this.ownerName = ownerName;
    }

    LocalDate productionDate;

    final String facilityName;

    String modelName;

    @Column(length = 10)
    String ownerName;

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(productionDate, car.productionDate) &&
                Objects.equals(facilityName, car.facilityName) &&
                Objects.equals(modelName, car.modelName) &&
                Objects.equals(ownerName, car.ownerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productionDate, facilityName, modelName, ownerName);
    }

}
