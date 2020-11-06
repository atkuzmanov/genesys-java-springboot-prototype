package com.atkuzmanov.genesys.aop;

import com.atkuzmanov.genesys.rest.ResponseDetails;
import com.atkuzmanov.genesys.rest.ResponseDetailsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.fields;
import static net.logstash.logback.argument.StructuredArguments.kv;

public class LoggingService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /*----------------[Request logging]----------------*/


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
        log.info("OUTGOING_RESPONSE", fields(
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

    public void logException(Exception e, String originMethod, String originClass) {
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
                kv("originMethod", originMethod),
                kv("originClass", originClass));
    }

}
