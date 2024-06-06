package org.grnet.pidmr.entity.database.converters;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import jakarta.ws.rs.ServerErrorException;
import org.grnet.pidmr.enums.RoleChangeRequestStatus;


@Converter
public class RoleChangeStatusConverter implements AttributeConverter<RoleChangeRequestStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RoleChangeRequestStatus status) {

        if (status == null)
            return null;

        switch (status) {
            case PENDING:
                return 0;

            case APPROVED:
                return 1;

            case REJECTED:
                return 2;

            default:
                throw new ServerErrorException(status + " not supported.", 501);
        }
    }

    @Override
    public RoleChangeRequestStatus convertToEntityAttribute(Integer dbData) {

        if (dbData == null)
            return null;

        switch (dbData) {
            case 0:
                return RoleChangeRequestStatus.PENDING;
            case 1:
                return RoleChangeRequestStatus.APPROVED;
            case 2:
                return RoleChangeRequestStatus.REJECTED;

            default:
                throw new ServerErrorException(dbData + " not supported.", 501);
        }
    }
}
