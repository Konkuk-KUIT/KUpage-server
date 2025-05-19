package com.kuit.kupage.domain.project.entity.converter;

import com.kuit.kupage.domain.project.entity.AppField;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

@Converter
public class AppFieldListStringConverter implements AttributeConverter<List<AppField>, String> {
    @Override
    public String convertToDatabaseColumn(List<AppField> attribute) {
        System.out.println(attribute.stream().peek(a -> System.out.println(a.name())));
        return String.join(", ", attribute.stream().map(Enum::name).toList());
    }

    @Override
    public List<AppField> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(", ")).map(AppField::valueOf).toList();
    }
}
