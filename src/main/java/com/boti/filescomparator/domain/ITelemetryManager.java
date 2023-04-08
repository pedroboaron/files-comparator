// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.boti.filescomparator.domain;

interface ITelemetryManager {
    String generateRequestId();

    TelemetryHelper createTelemetryHelper(String requestId,
                                          String clientId,
                                          Event event,
                                          Boolean shouldFlush);
}
