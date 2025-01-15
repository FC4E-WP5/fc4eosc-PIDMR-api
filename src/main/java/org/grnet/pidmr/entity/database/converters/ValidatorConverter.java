package org.grnet.pidmr.entity.database.converters;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.ws.rs.ServerErrorException;
import org.grnet.pidmr.enums.Validator;

@Converter
public class ValidatorConverter implements AttributeConverter<Validator, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Validator status) {

        if (status == null)
            return null;

        switch (status) {
            case NONE:
                return 0;
            case ISBN:
                return 1;
            default:
                throw new ServerErrorException(status + " not supported.", 501);
        }
    }

    @Override
    public Validator convertToEntityAttribute(Integer dbData) {

        if (dbData == null)
            return null;

        switch (dbData) {
            case 0:
                return Validator.NONE;
            case 1:
                return Validator.ISBN;
            default:
                throw new ServerErrorException(dbData + " not supported.", 501);
        }
    }
}
