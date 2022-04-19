package io.hotcloud.security.server.jwt;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
public class JwtBody implements Jwt {

    private final Map<String, Object> data;
    private final TimeUnit timeUnit;
    private final Integer time;

    public JwtBody(Map<String, Object> data,
                   TimeUnit timeUnit,
                   Integer time) {
        data = Objects.isNull(data) ? new HashMap<>() : data;
        this.data = data;
        this.timeUnit = timeUnit;
        this.time = time;
    }

    @Override
    public HeaderClaims header() {
        HeaderClaims headerClaims = new HeaderClaims();
        headerClaims.setType("JWT");
        return headerClaims;
    }

    @Override
    public PayloadClaims payload() {
        PayloadClaims payloadClaims = new PayloadClaims();
        payloadClaims.setAttributes(data);
        payloadClaims.setAudience(List.of("Client", "Web"));
        payloadClaims.setIssuer("Hot Cloud");
        payloadClaims.setId(UUID.randomUUID().toString());
        payloadClaims.setIssuedAt(new Date());
        payloadClaims.setSubject("Api Auth");

        if (timeUnit != null && time != null && time > 0) {
            switch (timeUnit) {
                case DAYS:
                    payloadClaims.expiredAfterDays(time);
                    break;
                case HOURS:
                    payloadClaims.expiredAfterHours(time);
                    break;
                case MINUTES:
                    payloadClaims.expiredAfterMinutes(time);
                    break;
                case SECONDS:
                    payloadClaims.expiredAfterSeconds(time);
                    break;
                default:
                    break;
            }
        }

        return payloadClaims;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
