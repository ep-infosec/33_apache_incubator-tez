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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import org.apache.hadoop.mapred.JobConf;
import org.apache.tez.common.TezJobConfig;
import org.apache.tez.runtime.library.common.Constants;
import org.junit.Test;

public class TestDeprecatedKeys {

  @Test
  public void verifyReduceKeyTranslation() {
    JobConf jobConf = new JobConf();

    jobConf.setFloat(MRJobConfig.SHUFFLE_INPUT_BUFFER_PERCENT, 0.4f);
    jobConf.setLong(MRJobConfig.REDUCE_MEMORY_TOTAL_BYTES, 20000l);
    jobConf.setInt(MRJobConfig.IO_SORT_FACTOR, 2000);
    jobConf.setFloat(MRJobConfig.SHUFFLE_MEMORY_LIMIT_PERCENT, 0.55f);
    jobConf.setFloat(MRJobConfig.REDUCE_MEMTOMEM_THRESHOLD, 0.60f);
    jobConf.setFloat(MRJobConfig.SHUFFLE_MERGE_PERCENT, 0.22f);
    jobConf.setBoolean(MRJobConfig.REDUCE_MEMTOMEM_ENABLED, true);
    jobConf.setFloat(MRJobConfig.REDUCE_INPUT_BUFFER_PERCENT, 0.33f);

    MRHelpers.translateVertexConfToTez(jobConf);

    assertEquals(0.4f, jobConf.getFloat(
        TezJobConfig.TEZ_RUNTIME_SHUFFLE_INPUT_BUFFER_PERCENT, 0f), 0.01f);
    assertEquals(20000l, jobConf.getLong(Constants.TEZ_RUNTIME_TASK_MEMORY, 0));
    assertEquals(2000,
        jobConf.getInt(TezJobConfig.TEZ_RUNTIME_IO_SORT_FACTOR, 0));
    assertEquals(0.55f, jobConf.getFloat(
        TezJobConfig.TEZ_RUNTIME_SHUFFLE_MEMORY_LIMIT_PERCENT, 0), 0.01f);
    assertEquals(0.60f,
        jobConf.getFloat(TezJobConfig.TEZ_RUNTIME_SHUFFLE_MEMTOMEM_SEGMENTS, 0),
        0.01f);
    assertEquals(0.22f,
        jobConf.getFloat(TezJobConfig.TEZ_RUNTIME_SHUFFLE_MERGE_PERCENT, 0),
        0.01f);
    assertEquals(true, jobConf.getBoolean(
        TezJobConfig.TEZ_RUNTIME_SHUFFLE_ENABLE_MEMTOMEM, false));
    assertEquals(0.33f,
        jobConf.getFloat(TezJobConfig.TEZ_RUNTIME_INPUT_BUFFER_PERCENT, 0),
        0.01f);
  }

  @Test
  /**
   * Set of keys that can be overriden at tez runtime
   */
  public void verifyTezOverridenKeys() {
    JobConf jobConf = new JobConf();
    jobConf.setInt(MRJobConfig.IO_SORT_FACTOR, 2000);
    jobConf.setInt(MRJobConfig.IO_SORT_MB, 100);
    jobConf.setInt(MRJobConfig.COUNTERS_MAX_KEY, 100);
    
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_IO_SORT_FACTOR, 1000);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_IO_SORT_MB, 200);
    jobConf.setBoolean(TezJobConfig.TEZ_RUNTIME_IFILE_READAHEAD, true);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_IFILE_READAHEAD_BYTES, 20);
    jobConf.setFloat(TezJobConfig.TEZ_RUNTIME_SORT_SPILL_PERCENT, 0.2f);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_INDEX_CACHE_MEMORY_LIMIT_BYTES, 10);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_COMBINE_MIN_SPILLS, 20);
    jobConf.setInt(Constants.TEZ_RUNTIME_TASK_MEMORY, 10);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_PARALLEL_COPIES, 10);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_FETCH_FAILURES_LIMIT, 10);
    jobConf.setBoolean(TezJobConfig.TEZ_RUNTIME_SHUFFLE_NOTIFY_READERROR, true);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_CONNECT_TIMEOUT, 10);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_READ_TIMEOUT, 10);
    jobConf.setBoolean(TezJobConfig.TEZ_RUNTIME_SHUFFLE_ENABLE_SSL, true);
    jobConf.setFloat(TezJobConfig.TEZ_RUNTIME_SHUFFLE_INPUT_BUFFER_PERCENT, 10.0f);
    jobConf.setFloat(TezJobConfig.TEZ_RUNTIME_SHUFFLE_MEMORY_LIMIT_PERCENT, 10.0f);
    jobConf.setFloat(TezJobConfig.TEZ_RUNTIME_SHUFFLE_MERGE_PERCENT, 10.0f);
    jobConf.setInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_MEMTOMEM_SEGMENTS, 10);
    jobConf.setBoolean(TezJobConfig.TEZ_RUNTIME_SHUFFLE_ENABLE_MEMTOMEM, true);
    jobConf.setFloat(TezJobConfig.TEZ_RUNTIME_INPUT_BUFFER_PERCENT, 10.0f);
    jobConf.set(TezJobConfig.TEZ_RUNTIME_INTERNAL_SORTER_CLASS, "DefaultSorter");
    jobConf.set(TezJobConfig.TEZ_RUNTIME_GROUP_COMPARATOR_CLASS, "groupComparator");
    jobConf.set(TezJobConfig.TEZ_RUNTIME_KEY_SECONDARY_COMPARATOR_CLASS, "SecondaryComparator");
    
    jobConf.setBoolean(MRJobConfig.MAP_OUTPUT_COMPRESS, false);
    jobConf.setBoolean(TezJobConfig.TEZ_RUNTIME_COMPRESS, true);

    MRHelpers.translateVertexConfToTez(jobConf);

    assertEquals(1000, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_IO_SORT_FACTOR, 0));
    assertEquals(200, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_IO_SORT_MB, 100));
    assertEquals(true, jobConf.getBoolean(TezJobConfig.TEZ_RUNTIME_IFILE_READAHEAD, false));
    assertEquals(20, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_IFILE_READAHEAD_BYTES, 0));
    assertEquals(10, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_INDEX_CACHE_MEMORY_LIMIT_BYTES, 0));
    assertEquals(20, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_COMBINE_MIN_SPILLS, 0));
    assertEquals(10, jobConf.getInt(Constants.TEZ_RUNTIME_TASK_MEMORY, 0));
    assertEquals(10, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_PARALLEL_COPIES, 0));
    assertEquals(10, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_FETCH_FAILURES_LIMIT, 0));
    assertEquals(true, jobConf.getBoolean(TezJobConfig.TEZ_RUNTIME_SHUFFLE_NOTIFY_READERROR, false));
    assertEquals(10, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_CONNECT_TIMEOUT, 0));
    assertEquals(10, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_READ_TIMEOUT, 0));
    assertEquals(true, jobConf.getBoolean(TezJobConfig.TEZ_RUNTIME_SHUFFLE_ENABLE_SSL, false));
    assertEquals(10.0f, jobConf.getFloat(TezJobConfig.TEZ_RUNTIME_SHUFFLE_INPUT_BUFFER_PERCENT, 0.0f), 0.0f);
    assertEquals(10.0f, jobConf.getFloat(TezJobConfig.TEZ_RUNTIME_SHUFFLE_MEMORY_LIMIT_PERCENT, 0.0f), 0.0f);
    assertEquals(10.0f, jobConf.getFloat(TezJobConfig.TEZ_RUNTIME_SHUFFLE_MERGE_PERCENT, 0.0f), 0.0f);
    assertEquals(10, jobConf.getInt(TezJobConfig.TEZ_RUNTIME_SHUFFLE_MEMTOMEM_SEGMENTS, 0));
    assertEquals(true, jobConf.getBoolean(TezJobConfig.TEZ_RUNTIME_SHUFFLE_ENABLE_MEMTOMEM, false));
    assertEquals(10.0f, jobConf.getFloat(TezJobConfig.TEZ_RUNTIME_INPUT_BUFFER_PERCENT, 0.0f), 0.0f);
    assertEquals("DefaultSorter", jobConf.get(TezJobConfig.TEZ_RUNTIME_INTERNAL_SORTER_CLASS, ""));
    assertEquals("groupComparator", jobConf.get(TezJobConfig.TEZ_RUNTIME_GROUP_COMPARATOR_CLASS, ""));
    assertEquals("SecondaryComparator", jobConf.get(TezJobConfig.TEZ_RUNTIME_KEY_SECONDARY_COMPARATOR_CLASS, ""));
    assertEquals("DefaultSorter", jobConf.get(TezJobConfig.TEZ_RUNTIME_INTERNAL_SORTER_CLASS, ""));
    assertTrue(jobConf.getBoolean(TezJobConfig.TEZ_RUNTIME_COMPRESS, false));

    assertNull(jobConf.get(MRConfig.MAPRED_IFILE_READAHEAD));
    assertNull(jobConf.get(MRConfig.MAPRED_IFILE_READAHEAD_BYTES));
    assertNull(jobConf.get(MRJobConfig.RECORDS_BEFORE_PROGRESS));
    assertNull(jobConf.get(MRJobConfig.IO_SORT_FACTOR));
    assertNull(jobConf.get(MRJobConfig.IO_SORT_MB));
    assertNull(jobConf.get(MRJobConfig.SHUFFLE_READ_TIMEOUT));
    assertNull(jobConf.get(MRJobConfig.INDEX_CACHE_MEMORY_LIMIT));
    assertNull(jobConf.get(MRJobConfig.MAP_COMBINE_MIN_SPILLS));
    assertNull(jobConf.get(MRJobConfig.REDUCE_MEMORY_TOTAL_BYTES));
    assertNull(jobConf.get(MRJobConfig.SHUFFLE_PARALLEL_COPIES));
    assertNull(jobConf.get(MRJobConfig.SHUFFLE_FETCH_FAILURES));
    assertNull(jobConf.get(MRJobConfig.SHUFFLE_NOTIFY_READERROR));
    assertNull(jobConf.get(MRJobConfig.SHUFFLE_CONNECT_TIMEOUT));
    assertNull(jobConf.get(MRJobConfig.SHUFFLE_READ_TIMEOUT));
    assertNull(jobConf.get(MRConfig.SHUFFLE_SSL_ENABLED_KEY));
    assertNull(jobConf.get(MRJobConfig.SHUFFLE_INPUT_BUFFER_PERCENT));
    assertNull(jobConf.get(MRJobConfig.SHUFFLE_MEMORY_LIMIT_PERCENT));
    assertNull(jobConf.get(MRJobConfig.REDUCE_MEMTOMEM_THRESHOLD));
    assertNull(jobConf.get(MRJobConfig.REDUCE_MEMTOMEM_ENABLED));
    assertNull(jobConf.get(MRJobConfig.REDUCE_INPUT_BUFFER_PERCENT));
    assertNull(jobConf.get(MRJobConfig.GROUP_COMPARATOR_CLASS));
    assertNull(jobConf.get(MRJobConfig.GROUP_COMPARATOR_CLASS));
    assertNull(jobConf.get("map.sort.class"));
  }

}
