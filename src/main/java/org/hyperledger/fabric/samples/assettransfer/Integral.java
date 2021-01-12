/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Integral {

    @Property()
    private final String integralId;

    @Property()
    private final String userId;

    @Property()
    private final String eventId;

    @Property()
    private final String type;

    @Property()
    private final int number;

    @Property()
    private final String createDate;

    public String getIntegralId() {
        return integralId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public String getCreateDate() {
        return createDate;
    }

    public Integral(
            @JsonProperty("integralId") final String integralId,
            @JsonProperty("userId") final String userId,
            @JsonProperty("eventId") final String eventId,
            @JsonProperty("type") final String type,
            @JsonProperty("number") final int number,
            @JsonProperty("createDate") final String createDate
    ) {
        this.integralId = integralId;
        this.userId = userId;
        this.eventId = eventId;
        this.type = type;
        this.number = number;
        this.createDate = createDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Integral that = (Integral) o;
        return eventId == that.eventId && number == that.number && Objects.equals(integralId, that.integralId) && Objects.equals(userId, that.userId) && Objects.equals(type, that.type) && Objects.equals(createDate, that.createDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(integralId, userId, eventId, type, number, createDate);
    }

    @Override
    public String toString() {
        return "AssetIntegral{"
                + "integralId='" + integralId + '\''
                + ", userId='" + userId + '\''
                + ", eventId=" + eventId
                + ", type='" + type + '\''
                + ", number=" + number
                + ", createDate='" + createDate + '\''
                + '}';
    }
}
