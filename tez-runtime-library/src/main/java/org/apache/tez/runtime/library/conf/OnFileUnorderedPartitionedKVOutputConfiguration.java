/*
 * *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.apache.tez.runtime.library.conf;

import java.io.IOException;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.tez.common.TezJobConfig;
import org.apache.tez.common.TezUtils;
import org.apache.tez.runtime.library.common.ConfigUtils;
import org.apache.tez.runtime.library.output.OnFileUnorderedPartitionedKVOutput;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class OnFileUnorderedPartitionedKVOutputConfiguration {
  /**
   * Configure parameters which are specific to the Output.
   */
  @InterfaceAudience.Private
  public static interface SpecificConfigurer<T> extends BaseConfigurer<T> {
    /**
     * Set the buffer size to use
     *
     * @param availableBufferSize the size of the buffer in MB
     * @return instance of the current builder
     */
    public T setAvailableBufferSize(int availableBufferSize);
  }

  @SuppressWarnings("rawtypes")
  @InterfaceAudience.Public
  @InterfaceStability.Evolving
  public static class SpecificBuilder<E extends HadoopKeyValuesBasedBaseConf.Builder> implements
      SpecificConfigurer<SpecificBuilder> {

    private final E edgeBuilder;
    private final Builder builder;

    SpecificBuilder(E edgeBuilder, Builder builder) {
      this.edgeBuilder = edgeBuilder;
      this.builder = builder;
    }

    @Override
    public SpecificBuilder<E> setAvailableBufferSize(int availableBufferSize) {
      builder.setAvailableBufferSize(availableBufferSize);
      return this;
    }

    @Override
    public SpecificBuilder setAdditionalConfiguration(String key, String value) {
      builder.setAdditionalConfiguration(key, value);
      return this;
    }

    @Override
    public SpecificBuilder setAdditionalConfiguration(Map<String, String> confMap) {
      builder.setAdditionalConfiguration(confMap);
      return this;
    }

    @Override
    public SpecificBuilder setFromConfiguration(Configuration conf) {
      builder.setFromConfiguration(conf);
      return this;
    }

    public E done() {
      return edgeBuilder;
    }
  }

  @InterfaceAudience.Private
  @VisibleForTesting
  Configuration conf;

  @InterfaceAudience.Private
  @VisibleForTesting
  OnFileUnorderedPartitionedKVOutputConfiguration() {
  }

  private OnFileUnorderedPartitionedKVOutputConfiguration(Configuration conf) {
    this.conf = conf;
  }

  /**
   * Get a byte array representation of the configuration
   * @return a byte array which can be used as the payload
   */
  public byte[] toByteArray() {
    try {
      return TezUtils.createUserPayloadFromConf(conf);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @InterfaceAudience.Private
  public void fromByteArray(byte[] payload) {
    try {
      this.conf = TezUtils.createConfFromUserPayload(payload);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Builder newBuilder(String keyClass, String valClass, String partitionerClassName,
                                   Configuration partitionerConf) {
    return new Builder(keyClass, valClass, partitionerClassName, partitionerConf);
  }

  @InterfaceAudience.Public
  @InterfaceStability.Evolving
  public static class Builder implements SpecificConfigurer<Builder> {

    private final Configuration conf = new Configuration(false);

    /**
     * Create a configuration builder for {@link org.apache.tez.runtime.library.output.OnFileUnorderedPartitionedKVOutput}
     *
     * @param keyClassName         the key class name
     * @param valueClassName       the value class name
     * @param partitionerClassName the partitioner class name
     * @param partitionerConf      configuration for the partitioner. This can be null
     */
    @InterfaceAudience.Private
    Builder(String keyClassName, String valueClassName, String partitionerClassName,
                   Configuration partitionerConf) {
      this();
      Preconditions.checkNotNull(keyClassName, "Key class name cannot be null");
      Preconditions.checkNotNull(valueClassName, "Value class name cannot be null");
      Preconditions.checkNotNull(partitionerClassName, "Partitioner class name cannot be null");
      setKeyClassName(keyClassName);
      setValueClassName(valueClassName);
      setPartitioner(partitionerClassName, partitionerConf);
    }

    @InterfaceAudience.Private
    Builder() {
      Map<String, String> tezDefaults = ConfigUtils
          .extractConfigurationMap(TezJobConfig.getTezRuntimeConfigDefaults(),
              OnFileUnorderedPartitionedKVOutput.getConfigurationKeySet());
      ConfigUtils.addConfigMapToConfiguration(this.conf, tezDefaults);
      ConfigUtils.addConfigMapToConfiguration(this.conf, TezJobConfig.getOtherConfigDefaults());
    }

    @InterfaceAudience.Private
    Builder setKeyClassName(String keyClassName) {
      Preconditions.checkNotNull(keyClassName, "Key class name cannot be null");
      this.conf.set(TezJobConfig.TEZ_RUNTIME_KEY_CLASS, keyClassName);
      return this;
    }

    @InterfaceAudience.Private
    Builder setValueClassName(String valueClassName) {
      Preconditions.checkNotNull(valueClassName, "Value class name cannot be null");
      this.conf.set(TezJobConfig.TEZ_RUNTIME_VALUE_CLASS, valueClassName);
      return this;
    }

    @InterfaceAudience.Private
    Builder setPartitioner(String partitionerClassName, Configuration partitionerConf) {
      Preconditions.checkNotNull(partitionerClassName, "Partitioner class name cannot be null");
      this.conf.set(TezJobConfig.TEZ_RUNTIME_PARTITIONER_CLASS, partitionerClassName);
      if (partitionerConf != null) {
        // Merging the confs for now. Change to be specific in the future.
        ConfigUtils.mergeConfsWithExclusions(this.conf, partitionerConf,
            TezJobConfig.getRuntimeConfigKeySet());
      }
      return this;
    }

    @Override
    public Builder setAvailableBufferSize(int availableBufferSize) {
      this.conf
          .setInt(TezJobConfig.TEZ_RUNTIME_UNORDERED_OUTPUT_BUFFER_SIZE_MB, availableBufferSize);
      return this;
    }

    @Override
    public Builder setAdditionalConfiguration(String key, String value) {
      Preconditions.checkNotNull(key, "Key cannot be null");
      if (ConfigUtils.doesKeyQualify(key,
          Lists.newArrayList(OnFileUnorderedPartitionedKVOutput.getConfigurationKeySet(),
              TezJobConfig.getRuntimeAdditionalConfigKeySet()),
          TezJobConfig.getAllowedPrefixes())) {
        if (value == null) {
          this.conf.unset(key);
        } else {
          this.conf.set(key, value);
        }
      }
      return this;
    }

    @Override
    public Builder setAdditionalConfiguration(Map<String, String> confMap) {
      Preconditions.checkNotNull(confMap, "ConfMap cannot be null");
      Map<String, String> map = ConfigUtils.extractConfigurationMap(confMap,
          Lists.newArrayList(OnFileUnorderedPartitionedKVOutput.getConfigurationKeySet(),
              TezJobConfig.getRuntimeAdditionalConfigKeySet()), TezJobConfig.getAllowedPrefixes());
      ConfigUtils.addConfigMapToConfiguration(this.conf, map);
      return this;
    }

    @Override
    public Builder setFromConfiguration(Configuration conf) {
      // Maybe ensure this is the first call ? Otherwise this can end up overriding other parameters
      Preconditions.checkArgument(conf != null, "Configuration cannot be null");
      Map<String, String> map = ConfigUtils.extractConfigurationMap(conf,
          Lists.newArrayList(OnFileUnorderedPartitionedKVOutput.getConfigurationKeySet(),
              TezJobConfig.getRuntimeAdditionalConfigKeySet()), TezJobConfig.getAllowedPrefixes());
      ConfigUtils.addConfigMapToConfiguration(this.conf, map);
      return this;
    }

    public Builder enableCompression(String compressionCodec) {
      this.conf.setBoolean(TezJobConfig.TEZ_RUNTIME_COMPRESS, true);
      if (compressionCodec != null) {
        this.conf
            .set(TezJobConfig.TEZ_RUNTIME_COMPRESS_CODEC, compressionCodec);
      }
      return this;
    }

    /**
     * Create the actual configuration instance.
     *
     * @return an instance of the Configuration
     */
    public OnFileUnorderedPartitionedKVOutputConfiguration build() {
      return new OnFileUnorderedPartitionedKVOutputConfiguration(this.conf);
    }
  }
}
