package com.kpa.test.demo_jpa.enums.converters;

import com.kpa.test.demo_jpa.enums.MaritalStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/*
 * We've set the @Converter‘s value of autoApply to true so that JPA will automatically apply the conversion logic.
 * Otherwise, we'd have to put the @Converter annotation directly on the entity's field.
 */
@Converter(autoApply = true)
public class MaritalEnumConverted implements AttributeConverter<MaritalStatus, Character> {
    @Override
    public Character convertToDatabaseColumn(MaritalStatus maritalStatus) {
        return maritalStatus.shortName();
    }

    @Override
    public MaritalStatus convertToEntityAttribute(Character shortName) {
        return MaritalStatus.of(shortName);
    }
}
