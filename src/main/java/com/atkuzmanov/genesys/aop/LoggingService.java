package com.atkuzmanov.genesys.aop;

import com.atkuzmanov.genesys.rest.ResponseDetails;
import com.atkuzmanov.genesys.rest.ResponseDetailsBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.logstash.logback.argument.StructuredArguments.kv;

public class LoggingService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /*----------------[Request logging]----------------*/

    public void logContentCachingRequest(ContentCachingRequestWrapper requestWrapper, String originClass, String originMethod) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        Map<String, String> requestLogMap = new HashMap<>();
        requestLogMap.put("uri", requestWrapper.getRequestURI());
        requestLogMap.put("url", requestWrapper.getRequestURL().toString());
        requestLogMap.put("path", requestWrapper.getServletPath());
        requestLogMap.put("requestMethod", requestWrapper.getMethod());
        requestLogMap.put("requestScheme", requestWrapper.getScheme());
        requestLogMap.put("request Protocol", requestWrapper.getProtocol());
        requestLogMap.put("request LocalName", requestWrapper.getLocalName());
        requestLogMap.put("request Locale", requestWrapper.getLocale().toString());
        requestLogMap.put("request Locales", Collections.list(requestWrapper.getLocales()).toString());
        requestLogMap.put("request QueryString", requestWrapper.getQueryString());
        requestLogMap.put("request RemoteHost ", requestWrapper.getRemoteHost());
        requestLogMap.put("request ServerName ", requestWrapper.getServerName());
        requestLogMap.put("request ServerPort ", String.valueOf(requestWrapper.getServerPort()));
        requestLogMap.put("originClass", originClass);
        requestLogMap.put("originMethod", originMethod);
        requestLogMap.values().removeIf(value -> value == null || value.trim().length() == 0);

        JsonNode jNode = JsonNodeFactory.instance.nullNode();
        try {
            String mapAsJSONStr = mapper.writeValueAsString(requestLogMap);
            jNode = mapper.readTree(mapAsJSONStr);
            rootNode = jNode.deepCopy();
            rootNode.put("queryParameters", requestWrapper.getParameterMap().toString());
            rootNode.put("requestBody", extractRequestPayload(requestWrapper));
            rootNode.put("headers", extractRequestHeaders(requestWrapper).toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("INCOMING_REQUEST",
                kv("requestDetails", rootNode));
    }

    private Map<String, String> extractRequestParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            parameters.put(paramName, request.getParameter(paramName));
        }
        return parameters;
    }

    private Map<String, String> extractRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }

    private String extractRequestPayload(HttpServletRequest request) {
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
                buffer.append(System.lineSeparator());
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
        return buffer.toString();
    }

    /*----------------[Response logging]----------------*/

    public void logContentCachingResponse(ContentCachingResponseWrapper responseWrapper, String originClass, String originMethod) {
        ObjectMapper mapper = new ObjectMapper();

        HttpStatus responseStatus = HttpStatus.valueOf(responseWrapper.getStatus());
        HttpHeaders responseHeaders = new HttpHeaders();
        for (String headerName : responseWrapper.getHeaderNames()) {
            responseHeaders.add(headerName, responseWrapper.getHeader(headerName));
        }

        String responseBody = null;
        try {
            responseBody = IOUtils.toString(responseWrapper.getContentInputStream(), UTF_8);

            if (!this.log.isDebugEnabled() && isValidJSON(responseBody)) {
                JsonNode jNode = mapper.readTree(responseBody);
                if (jNode.has("responseMessage")) {
                    responseBody = jNode.path("responseMessage").asText();
                }
            } else {
                responseBody = mapper.writeValueAsString(responseBody);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        ResponseDetailsBuilder rdb = ResponseDetails.builder()
                .status(responseStatus.value())
                .originClass(originClass)
                .originMethod(originMethod)
                .headers(responseHeaders)
                .responseBody(responseBody);

        log.info("OUTGOING_RESPONSE", kv("responseDetails", rdb.build()));
    }

    public static boolean isValidJSON(final String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        boolean valid = true;
        try {
            mapper.readTree(json);
        } catch (JsonProcessingException e) {
            valid = false;
        }
        return valid;
    }

    /*----------------[ResponseEntity<?> logging]----------------*/

    public void logResponseEntity(ResponseEntity<?> responseEntity, String originClass, String originMethod) {
        if (responseEntity.hasBody()) {
            if (responseEntity.getBody() instanceof ResponseDetails) {
                logResponseWithResponseDetails(responseEntity);
            } else {
                logResponseWithJSONBody(responseEntity, originClass, originMethod);
            }
        } else {
            log.info("OUTGOING_RESPONSE", kv("responseDetails",
                    buildResponseDetailsForLogging(responseEntity, originClass, originMethod)));
        }
    }

    private void logResponseWithResponseDetails(ResponseEntity<?> responseEntity) {
        ResponseDetails responseDetails = (ResponseDetails) responseEntity.getBody();
        if (!this.log.isDebugEnabled()) {
            responseDetails.setThrowable(null);
        }
        log.info("OUTGOING_RESPONSE", kv("responseDetails", responseDetails));
    }

    private void logResponseWithJSONBody(ResponseEntity<?> responseEntity, String originClass, String originMethod) {
        String bodyJSONstr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            bodyJSONstr = objectMapper.writeValueAsString(responseEntity.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("OUTGOING_RESPONSE", kv("responseDetails",
                buildResponseDetailsForLogging(responseEntity, bodyJSONstr, originClass, originMethod)));
    }

    private ResponseDetails buildResponseDetailsForLogging(ResponseEntity<?> responseObj, String originClass, String originMethod) {
        return buildResponseDetailsForLogging(responseObj, null, originClass, originMethod);
    }

    private ResponseDetails buildResponseDetailsForLogging(ResponseEntity<?> responseObj, String body, String originClass, String originMethod) {
        ResponseDetailsBuilder rdb = ResponseDetails.builder()
                .status(responseObj.getStatusCodeValue())
                .originClass(originClass)
                .originMethod(originMethod)
                .headers(responseObj.getHeaders())
                .responseBody(body);

        return rdb.build();
    }

    private String getContentAsString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0) {
            return "";
        }

        try {
            int length = Math.min(buf.length, 1000);

            return new String(buf, 0, length, charsetName);
        } catch (UnsupportedEncodingException ex) {
            return "Unsupported Encoding";
        }
    }

    /*----------------[Exception logging]----------------*/

    public void logException(Exception e, String originClass, String originMethod) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("Exception in class", originClass);
        rootNode.put("Exception in method", originMethod);
        rootNode.put("Exception message", e.getMessage());
        if (this.log.isDebugEnabled()) {
            rootNode.put("Exception stacktrace ", Arrays.toString(e.getStackTrace()));
        }
        log.error("Exception occurred",
                kv("exception", rootNode),
                kv("originClass", originClass),
                kv("originMethod", originMethod));
    }
}
