package io.hotcloud.security.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author yaolianhua789@gmail.com
 **/
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Result<String> error = Result.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), httpServletRequest.getRequestURI());

        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.writeValue(httpServletResponse.getWriter(), error);

    }
}
