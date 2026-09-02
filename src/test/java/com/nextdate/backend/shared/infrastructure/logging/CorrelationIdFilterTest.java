package com.nextdate.backend.shared.infrastructure.logging;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorrelationIdFilterTest {

  private final CorrelationIdFilter filter = new CorrelationIdFilter();

  @Test
  void shouldGenerateCorrelationIdWhenHeaderIsMissing() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    FilterChain chain =
        (req, res) -> {
          String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY);
          assertNotNull(correlationId);
          assertFalse(correlationId.isEmpty());
        };

    filter.doFilter(request, response, chain);

    String responseHeader = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
    assertNotNull(responseHeader);
    assertFalse(responseHeader.isEmpty());
    // MDC should be cleaned up after request
    assertNull(MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY));
  }

  @Test
  void shouldPropagateExistingCorrelationIdFromHeader() throws ServletException, IOException {
    String existingCorrelationId = "custom-trace-id-12345";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, existingCorrelationId);
    MockHttpServletResponse response = new MockHttpServletResponse();

    FilterChain chain =
        (req, res) -> {
          assertEquals(existingCorrelationId, MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY));
        };

    filter.doFilter(request, response, chain);

    assertEquals(
        existingCorrelationId, response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER));
    assertNull(MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY));
  }
}
