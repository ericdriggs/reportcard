package com.github.ericdriggs.reportcard.client;

import lombok.Data;

@Data
public class HostApplicationPipeline {
    private String host;
    private String application;
    private String pipeline;
}
