package com.covrsecurity.io.greendao.model.database;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Lenovo on 16.05.2017.
 */

public class RecordTypeConverter implements PropertyConverter<RecordType, String> {

    @Override
    public RecordType convertToEntityProperty(String databaseValue) {
        return RecordType.valueOf(databaseValue);
    }

    @Override
    public String convertToDatabaseValue(RecordType entityProperty) {
        return entityProperty.name();
    }
}
