package io.github.ericdriggs.reportcard.config;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.Map;

@Configuration
public class SwaggerBeanConfig {

    public SwaggerBeanConfig(MappingJackson2HttpMessageConverter converter) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }

    @Bean
    public OpenApiCustomiser pathParameterDescriptions() {
        Map<String, String> descriptions = Map.ofEntries(
                Map.entry("company", "Company identifier (top-level namespace)"),
                Map.entry("org", "Organization within the company"),
                Map.entry("repo", "Repository name"),
                Map.entry("repoName", "Repository name (cross-org lookup, matches any org)"),
                Map.entry("branch", "Git branch name (e.g. 'main', 'feature/foo')"),
                Map.entry("jobId", "Internal numeric job ID. Use the /jobinfo/ path for human-readable key-value lookup."),
                Map.entry("jobInfo", "Comma-separated key=value pairs identifying the job (e.g. 'application=myapp,pipeline=nightly')"),
                Map.entry("stage", "Test stage name (e.g. 'unit', 'integration')"),
                Map.entry("sha", "Git commit SHA (full or abbreviated)"),
                Map.entry("runId", "Internal numeric run ID"),
                Map.entry("runCount", "The jobRunCount (build number) identifying a specific run within a job"),
                Map.entry("runReference", "UUID reference identifying a run")
        );
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(op -> {
                    if (op.getParameters() != null) {
                        op.getParameters().stream()
                                .filter(p -> "path".equals(p.getIn()) && descriptions.containsKey(p.getName()))
                                .filter(p -> p.getDescription() == null)
                                .forEach(p -> p.setDescription(descriptions.get(p.getName())));
                    }
                })
        );
    }
}
