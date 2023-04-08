// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.boti.filescomparator.domain;

interface ITelemetry {
    void startEvent(String requestId, Event eventToStart);

    void stopEvent(String requestId, Event eventToEnd);

    void flush(String requestId, String clientId);
}
