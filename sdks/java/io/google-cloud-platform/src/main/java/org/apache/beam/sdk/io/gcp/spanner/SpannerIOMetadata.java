/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.gcp.spanner;

import java.util.concurrent.TimeUnit;
import org.apache.beam.sdk.extensions.gcp.util.GceMetadataUtil;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.base.Strings;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.base.Supplier;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.base.Suppliers;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Metadata class for SpannerIO. */
final class SpannerIOMetadata {

  private final @Nullable String beamJobId;

  static final Supplier<SpannerIOMetadata> INSTANCE =
      Suppliers.memoizeWithExpiration(() -> refreshInstance(), 5, TimeUnit.MINUTES);

  private SpannerIOMetadata(@Nullable String beamJobId) {
    this.beamJobId = beamJobId;
  }

  /**
   * Creates a SpannerIOMetadata. This will request metadata properly based on which runner is being
   * used.
   */
  public static SpannerIOMetadata create() {
    return INSTANCE.get();
  }

  private static SpannerIOMetadata refreshInstance() {
    String dataflowJobId = GceMetadataUtil.fetchDataflowJobId();
    if (Strings.isNullOrEmpty(dataflowJobId)) {
      return new SpannerIOMetadata(null);
    }

    return new SpannerIOMetadata(dataflowJobId);
  }

  /*
   * Returns the beam job id. Can be null if it is not running on Dataflow.
   */
  public @Nullable String getBeamJobId() {
    return this.beamJobId;
  }
}
