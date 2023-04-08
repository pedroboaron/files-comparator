// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.boti.filescomparator.domain;

import java.util.concurrent.ExecutorService;

class ServiceBundle {

    private ExecutorService executorService;
    private TelemetryManager telemetryManager;
    private IHttpClient httpClient;
    private ServerSideTelemetry serverSideTelemetry;

    ServiceBundle(ExecutorService executorService, IHttpClient httpClient,
                  TelemetryManager telemetryManager) {
        this.executorService = executorService;
        this.telemetryManager = telemetryManager;
        this.httpClient = httpClient;

        serverSideTelemetry = new ServerSideTelemetry();
    }

    ExecutorService getExecutorService() {
        return executorService;
    }

    TelemetryManager getTelemetryManager() {
        return telemetryManager;
    }

    IHttpClient getHttpClient() {
        return httpClient;
    }

    ServerSideTelemetry getServerSideTelemetry() {
        return serverSideTelemetry;
    }
}