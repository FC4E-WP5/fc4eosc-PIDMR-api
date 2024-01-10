package org.grnet.pidmr.entity.database.converters;


import org.grnet.pidmr.enums.ProviderStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.ws.rs.ServerErrorException;

@Converter
public class ProviderStatusAttributeConverter implements AttributeConverter<ProviderStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ProviderStatus status) {

        if (status == null)
            return null;

        switch (status) {
            case PENDING:
                return 0;

            case APPROVED:
                return 1;

            default:
                throw new ServerErrorException(status + " not supported.", 501);
        }
    }

    @Override
    public ProviderStatus convertToEntityAttribute(Integer dbData) {

        if (dbData == null)
            return null;

        switch (dbData) {
            case 0:
                return ProviderStatus.PENDING;

            case 1:
                return ProviderStatus.APPROVED;

            default:
                throw new ServerErrorException(dbData + " not supported.", 501);
        }
    }
}
