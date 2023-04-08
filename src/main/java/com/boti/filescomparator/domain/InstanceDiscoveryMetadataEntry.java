// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.boti.filescomparator.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

@Accessors(fluent = true)
@Getter(AccessLevel.PACKAGE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
class InstanceDiscoveryMetadataEntry {

    @JsonProperty("preferred_network")
    String preferredNetwork;

    @JsonProperty("preferred_cache")
    String preferredCache;

    @JsonProperty("aliases")
    Set<String> aliases;
}
