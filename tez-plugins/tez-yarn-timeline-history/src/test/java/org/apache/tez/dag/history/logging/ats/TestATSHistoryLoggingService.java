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

package org.apache.tez.dag.history.logging.ats;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.yarn.util.SystemClock;
import org.apache.tez.dag.api.TezConfiguration;
import org.apache.tez.dag.app.AppContext;
import org.apache.tez.dag.history.DAGHistoryEvent;
import org.apache.tez.dag.history.events.DAGStartedEvent;
import org.apache.tez.dag.records.TezDAGID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestATSHistoryLoggingService {

  private ATSHistoryLoggingService atsHistoryLoggingService;
  private AppContext appContext;
  private Configuration conf;
  private int atsInvokeCounter;
  private SystemClock clock = new SystemClock();

  @Before
  public void setup() throws Exception {
    appContext = mock(AppContext.class);
    atsHistoryLoggingService = new ATSHistoryLoggingService();
    atsHistoryLoggingService.setAppContext(appContext);
    conf = new Configuration(false);
    conf.setLong(TezConfiguration.YARN_ATS_EVENT_FLUSH_TIMEOUT_MILLIS,
        1000l);
    atsInvokeCounter = 0;
    atsHistoryLoggingService.init(conf);
    atsHistoryLoggingService.timelineClient = mock(TimelineClient.class);
    atsHistoryLoggingService.start();
    when(appContext.getClock()).thenReturn(clock);
    when(atsHistoryLoggingService.timelineClient.putEntities(any(TimelineEntity.class))).thenAnswer(
        new Answer<Object>() {
          @Override
          public Object answer(InvocationOnMock invocation) throws Throwable {
            ++atsInvokeCounter;
            try {
              Thread.sleep(500);
            } catch (InterruptedException e) {
              // do nothing
            }
            return null;
          }
        }
    );
  }

  @After
  public void teardown() {
    atsHistoryLoggingService.stop();
    atsHistoryLoggingService = null;
  }

  @Test(timeout=20000)
  public void testATSHistoryLoggingServiceShutdown() {
    TezDAGID tezDAGID = TezDAGID.getInstance(
        ApplicationId.newInstance(100l, 1), 1);
    DAGHistoryEvent historyEvent = new DAGHistoryEvent(tezDAGID,
        new DAGStartedEvent(tezDAGID, 1001l, "user1", "dagName1"));

    for (int i = 0; i < 20; ++i) {
      atsHistoryLoggingService.handle(historyEvent);
    }

    try {
      Thread.sleep(2500l);
    } catch (InterruptedException e) {
      // Do nothing
    }
    atsHistoryLoggingService.stop();

    Assert.assertTrue(atsInvokeCounter >= 4);
    Assert.assertTrue(atsInvokeCounter < 10);

  }

}
