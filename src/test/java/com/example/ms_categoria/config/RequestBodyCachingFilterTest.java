package com.example.ms_categoria.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.ServletRequest;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RequestBodyCachingFilterTest {

    private final RequestBodyCachingFilter filter = new RequestBodyCachingFilter();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    @DisplayName("RequestBodyCachingFilter - envuelve requests sin cache")
    void doFilterInternal_EnvuelveRequestSinCache() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(argThat(RequestBodyCachingFilterTest::isCachedRequest), same(response));
    }

    @Test
    @DisplayName("RequestBodyCachingFilter - reutiliza requests con cache")
    void doFilterInternal_ReutilizaRequestConCache() throws Exception {
        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(new MockHttpServletRequest());
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(same(request), same(response));
    }

    private static boolean isCachedRequest(ServletRequest request) {
        return request instanceof ContentCachingRequestWrapper;
    }
}
