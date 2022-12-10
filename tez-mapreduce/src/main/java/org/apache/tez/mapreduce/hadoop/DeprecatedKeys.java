/**
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

package org.apache.tez.mapreduce.hadoop;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.tez.common.TezJobConfig;
import org.apache.tez.dag.api.TezConfiguration;
import org.apache.tez.runtime.library.common.Constants;

public class DeprecatedKeys {

  
  
  // This could be done via deprecation.
  /**
   * Keys used by the DAG - mainly the AM. 
   */
  private static Map<String, String> mrParamToDAGParamMap = new HashMap<String, String>();

  /**
   * Keys used by the Tez Runtime.
   */
  private static Map<String, String> mrParamToTezRuntimeParamMap =
      new HashMap<String, String>();

  
 
  static {
    populateMRToTezRuntimeParamMap();
    populateMRToDagParamMap();
    addDeprecatedKeys();
  }

  private static void populateMRToDagParamMap() {
    // TODO Default value handling.
    mrParamToDAGParamMap.put(MRJobConfig.MR_AM_TASK_LISTENER_THREAD_COUNT,
        TezConfiguration.TEZ_AM_TASK_LISTENER_THREAD_COUNT);
    
    mrParamToDAGParamMap.put(MRJobConfig.MAX_TASK_FAILURES_PER_TRACKER,
        TezConfiguration.TEZ_AM_MAX_TASK_FAILURES_PER_NODE);
    mrParamToDAGParamMap.put(MRJobConfig.MR_AM_JOB_NODE_BLACKLISTING_ENABLE,
        TezConfiguration.TEZ_AM_NODE_BLACKLISTING_ENABLED);
    mrParamToDAGParamMap.put(
        MRJobConfig.MR_AM_IGNORE_BLACKLISTING_BLACKLISTED_NODE_PERECENT,
        TezConfiguration.TEZ_AM_NODE_BLACKLISTING_IGNORE_THRESHOLD);

    mrParamToDAGParamMap.put(MRJobConfig.QUEUE_NAME,
      TezConfiguration.TEZ_QUEUE_NAME);

    // Counter replacement will work in this manner, as long as TezCounters
    // extends MRCounters and is used directly by the Mapper/Reducer.
    // When these counters are eventually translated over to MRCounters, this
    // may break.
    // Framework counters, like FILESYSTEM will likely be incompatible since
    // they enum key belongs to a different package.
    mrParamToDAGParamMap.put(MRJobConfig.COUNTERS_MAX_KEY,
      TezJobConfig.TEZ_RUNTIME_COUNTERS_MAX_KEY);
    mrParamToDAGParamMap.put(MRJobConfig.COUNTER_GROUP_NAME_MAX_KEY,
      TezJobConfig.TEZ_RUNTIME_COUNTER_GROUP_NAME_MAX_KEY);
    mrParamToDAGParamMap.put(MRJobConfig.COUNTER_GROUP_NAME_MAX_KEY,
      TezJobConfig.TEZ_RUNTIME_COUNTER_NAME_MAX_KEY);
    mrParamToDAGParamMap.put(MRJobConfig.COUNTER_GROUPS_MAX_KEY,
      TezJobConfig.TEZ_RUNTIME_COUNTER_GROUPS_MAX_KEY);
  }

  // TODO TEZAM4 Sometime, make sure this gets loaded by default. Insteaf of the current initialization in MRAppMaster, TezChild.
  // Maybe define in an TEZConfiguration / TEZ JobConf variant.
  
  public static void init() {
  }
  
  private static void populateMRToTezRuntimeParamMap() {

    registerMRToRuntimeKeyTranslation(MRConfig.MAPRED_IFILE_READAHEAD, TezJobConfig.TEZ_RUNTIME_IFILE_READAHEAD);

    registerMRToRuntimeKeyTranslation(MRConfig.MAPRED_IFILE_READAHEAD_BYTES, TezJobConfig.TEZ_RUNTIME_IFILE_READAHEAD_BYTES);

    registerMRToRuntimeKeyTranslation(MRJobConfig.RECORDS_BEFORE_PROGRESS, TezJobConfig.TEZ_RUNTIME_RECORDS_BEFORE_PROGRESS);

    registerMRToRuntimeKeyTranslation(MRJobConfig.IO_SORT_FACTOR, TezJobConfig.TEZ_RUNTIME_IO_SORT_FACTOR);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.MAP_SORT_SPILL_PERCENT, TezJobConfig.TEZ_RUNTIME_SORT_SPILL_PERCENT);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.IO_SORT_MB, TezJobConfig.TEZ_RUNTIME_IO_SORT_MB);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.INDEX_CACHE_MEMORY_LIMIT, TezJobConfig.TEZ_RUNTIME_INDEX_CACHE_MEMORY_LIMIT_BYTES);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.MAP_COMBINE_MIN_SPILLS, TezJobConfig.TEZ_RUNTIME_COMBINE_MIN_SPILLS);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.REDUCE_MEMORY_TOTAL_BYTES, Constants.TEZ_RUNTIME_TASK_MEMORY);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.SHUFFLE_PARALLEL_COPIES, TezJobConfig.TEZ_RUNTIME_SHUFFLE_PARALLEL_COPIES);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.SHUFFLE_FETCH_FAILURES, TezJobConfig.TEZ_RUNTIME_SHUFFLE_FETCH_FAILURES_LIMIT);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.SHUFFLE_NOTIFY_READERROR, TezJobConfig.TEZ_RUNTIME_SHUFFLE_NOTIFY_READERROR);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.SHUFFLE_CONNECT_TIMEOUT, TezJobConfig.TEZ_RUNTIME_SHUFFLE_CONNECT_TIMEOUT);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.SHUFFLE_READ_TIMEOUT, TezJobConfig.TEZ_RUNTIME_SHUFFLE_READ_TIMEOUT);
    
    registerMRToRuntimeKeyTranslation(MRConfig.SHUFFLE_SSL_ENABLED_KEY, TezJobConfig.TEZ_RUNTIME_SHUFFLE_ENABLE_SSL);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.SHUFFLE_INPUT_BUFFER_PERCENT, TezJobConfig.TEZ_RUNTIME_SHUFFLE_INPUT_BUFFER_PERCENT);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.SHUFFLE_MEMORY_LIMIT_PERCENT, TezJobConfig.TEZ_RUNTIME_SHUFFLE_MEMORY_LIMIT_PERCENT);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.SHUFFLE_MERGE_PERCENT, TezJobConfig.TEZ_RUNTIME_SHUFFLE_MERGE_PERCENT);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.REDUCE_MEMTOMEM_THRESHOLD, TezJobConfig.TEZ_RUNTIME_SHUFFLE_MEMTOMEM_SEGMENTS);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.REDUCE_MEMTOMEM_ENABLED, TezJobConfig.TEZ_RUNTIME_SHUFFLE_ENABLE_MEMTOMEM);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.REDUCE_INPUT_BUFFER_PERCENT, TezJobConfig.TEZ_RUNTIME_INPUT_BUFFER_PERCENT);
    
    registerMRToRuntimeKeyTranslation("map.sort.class", TezJobConfig.TEZ_RUNTIME_INTERNAL_SORTER_CLASS);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.GROUP_COMPARATOR_CLASS, TezJobConfig.TEZ_RUNTIME_GROUP_COMPARATOR_CLASS);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.GROUP_COMPARATOR_CLASS, TezJobConfig.TEZ_RUNTIME_KEY_SECONDARY_COMPARATOR_CLASS);
    
    registerMRToRuntimeKeyTranslation(MRJobConfig.MAPREDUCE_JOB_CREDENTIALS_BINARY, TezJobConfig.TEZ_CREDENTIALS_PATH);

    registerMRToRuntimeKeyTranslation(MRJobConfig.KEY_COMPARATOR, TezJobConfig.TEZ_RUNTIME_KEY_COMPARATOR_CLASS);

    registerMRToRuntimeKeyTranslation(MRJobConfig.MAP_OUTPUT_KEY_CLASS, TezJobConfig.TEZ_RUNTIME_KEY_CLASS);

    registerMRToRuntimeKeyTranslation(MRJobConfig.MAP_OUTPUT_VALUE_CLASS, TezJobConfig.TEZ_RUNTIME_VALUE_CLASS);

    registerMRToRuntimeKeyTranslation(MRJobConfig.MAP_OUTPUT_COMPRESS, TezJobConfig.TEZ_RUNTIME_COMPRESS);

    registerMRToRuntimeKeyTranslation(MRJobConfig.MAP_OUTPUT_COMPRESS_CODEC, TezJobConfig.TEZ_RUNTIME_COMPRESS_CODEC);

  }
  
  private static void addDeprecatedKeys() {
  }

  private static void registerMRToRuntimeKeyTranslation(String mrKey,
      String tezKey) {
    mrParamToTezRuntimeParamMap.put(mrKey, tezKey);
  }
  
  @SuppressWarnings("unused")
  private static void _(String mrKey, String tezKey) {
    Configuration.addDeprecation(mrKey, tezKey);
  }

  public static Map<String, String> getMRToDAGParamMap() {
    return Collections.unmodifiableMap(mrParamToDAGParamMap);
  }

  public static Map<String, String> getMRToTezRuntimeParamMap() {
    return Collections.unmodifiableMap(mrParamToTezRuntimeParamMap);
  }
}
