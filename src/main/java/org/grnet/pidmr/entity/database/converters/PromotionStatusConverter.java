package org.grnet.pidmr.entity.database.converters;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.ws.rs.ServerErrorException;
import org.grnet.pidmr.enums.PromotionRequestStatus;
import org.grnet.pidmr.enums.ProviderStatus;


@Converter
public class PromotionStatusConverter implements AttributeConverter<PromotionRequestStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PromotionRequestStatus status) {

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
    public PromotionRequestStatus convertToEntityAttribute(Integer dbData) {

        if (dbData == null)
            return null;

        switch (dbData) {
            case 0:
                return PromotionRequestStatus.PENDING;

            case 1:
                return PromotionRequestStatus.APPROVED;

            default:
                throw new ServerErrorException(dbData + " not supported.", 501);
        }
    }
}
