package io.github.ericdriggs.reportcard.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Builder
@Jacksonized
@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseDetails {
    int httpStatus;
    String detail;
    String problemType;
    String problemInstance;
    String stackTrace;
    Map<String,String> createdUrls;

    public static ResponseDetails ok() {
        return ResponseDetails.builder().httpStatus(200).build();
    }

    public static ResponseDetails created(Map<String,String> createdUrls) {
        return ResponseDetails.builder()
                .httpStatus(201)
                .createdUrls(createdUrls)
                .build();
    }

    public static ResponseDetails fromException(Exception ex) {
        final int httpStatus = httpStatusFromException(ex);
        ResponseDetailsBuilder builder = ResponseDetails.builder()
                .httpStatus(httpStatus);

        if (isError(httpStatus)) {
            builder.problemType("https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#" + httpStatus);
            builder.detail(ex.getMessage());
            builder.stackTrace(ExceptionUtils.getStackTrace(ex));
        }
        return builder.build();
    }

    @JsonIgnore
    public boolean isError() {
        return isError(httpStatus);
    }

    static int httpStatusFromException(Exception ex) {
        final int defaultFailHttpStatus = 500;

        if (ex == null) {
            return defaultFailHttpStatus;
        }

        for (Map.Entry<Class<?>, Integer> entry : exceptionHttpStatusMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            if (ex.getClass().isInstance(clazz)) {
                return entry.getValue();
            }
        }
        return defaultFailHttpStatus;
    }

    private final static Map<Class<?>, Integer> exceptionHttpStatusMap = new LinkedHashMap<>();
    final static int HTTP_STATUS_BAD_REQUEST = 400;
    final static int HTTP_STATUS_INTERNAL_ERROR = 500;
    final static int HTTP_STATUS_PARSE_ERROR = 500;

    static {
        exceptionHttpStatusMap.put(IllegalArgumentException.class, HTTP_STATUS_BAD_REQUEST);
        exceptionHttpStatusMap.put(JsonProcessingException.class, HTTP_STATUS_PARSE_ERROR);
        exceptionHttpStatusMap.put(SAXException.class, HTTP_STATUS_PARSE_ERROR);
        exceptionHttpStatusMap.put(SAXParseException.class, HTTP_STATUS_PARSE_ERROR);
        exceptionHttpStatusMap.put(JAXBException.class, HTTP_STATUS_PARSE_ERROR);
        exceptionHttpStatusMap.put(ParserConfigurationException.class, HTTP_STATUS_INTERNAL_ERROR);
        exceptionHttpStatusMap.put(IOException.class, HTTP_STATUS_INTERNAL_ERROR);
    }

    @JsonIgnore
    static boolean isError(int httpStatus) {
        return httpStatus >= 400;
    }

}
