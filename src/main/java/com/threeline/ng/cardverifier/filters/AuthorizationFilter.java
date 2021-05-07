package com.threeline.ng.cardverifier.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.threeline.ng.cardverifier.responses.ErrorResponse;
import com.threeline.ng.cardverifier.utilities.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuthorizationFilter extends BasicAuthenticationFilter {

  final String DEFAULT_APP_KEY = "test_20191123132233", DEFAULT_TIMESTAMP= "1617953042",
          DEFAULT_HASH = "4n+F7tDHDaFCoPkDDCtHMX6fvNIolyzMLFONT5c4XSYBg7VYFg1uMDYW7b3wDOs+rKL4QjaY2A100Jufsg1XFA==";

  public AuthorizationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String authorization = request.getHeader(Constants.AUTHORIZATION_HEADER_NAME);
    String timestamp = request.getHeader(Constants.AUTHORIZATION_TIMESTAMP_NAME);
    String appKey = request.getHeader(Constants.AUTHORIZATION_APP_KEY_NAME);

    if (authorization == null
        || authorization.isEmpty()
        || timestamp == null
        || timestamp.isEmpty()
        || appKey == null
        || appKey.isEmpty()){
      throwBadRequest("invalid message request", response);
      return;
    }

    String hashed = authorization.replace(Constants.TOKEN_PREFIX,"");

    if (!hashed.equals(DEFAULT_HASH) || !appKey.equals(DEFAULT_APP_KEY) || !timestamp.equals(DEFAULT_TIMESTAMP)){
      throwBadRequest("invalid authorization key", response);
      return;
    }

    chain.doFilter(request, response);
  }


  public void throwBadRequest(String message, HttpServletResponse response){
    ObjectMapper objectMapper = new ObjectMapper();
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setStatus(HttpStatus.FORBIDDEN.toString());
    errorResponse.setCode(HttpStatus.FORBIDDEN.value());
    errorResponse.setMessage(message);
    errorResponse.setTimestamp(LocalDateTime.now().toString());
    try {
      objectMapper.writeValue(response.getWriter(), errorResponse);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
