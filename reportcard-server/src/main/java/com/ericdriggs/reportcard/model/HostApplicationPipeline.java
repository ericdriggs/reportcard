package com.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.jooq.tools.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class HostApplicationPipeline {
    @NonNull
    private String host;
    private String application;
    private String pipeline;

    @JsonIgnore
    public boolean isValid(){
        return getValidationErrors().isEmpty();
    }

    @JsonIgnore
    public boolean hasErrors(){
        return !isValid();
    }

    @JsonIgnore
    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isEmpty(host)) {
            errors.add("host is empty. ");
        }
        if (StringUtils.isEmpty(application) && !StringUtils.isEmpty(pipeline)) {
            errors.add("application is empty but pipeline is not");
        }
        return errors;
    }
}
