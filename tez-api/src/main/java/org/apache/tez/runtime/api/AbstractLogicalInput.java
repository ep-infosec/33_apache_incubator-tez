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
package org.apache.tez.runtime.api;

import java.util.List;

/**
 * The abstract implementation of {@link LogicalInput}. It includes default
 * implementations of few methods for the convenience.
 * 
 */
public abstract class AbstractLogicalInput implements LogicalInput {

  protected int numPhysicalInputs;
  protected TezInputContext inputContext;

  @Override
  public void setNumPhysicalInputs(int numInputs) {
    this.numPhysicalInputs = numInputs;
  }

  @Override
  public List<Event> initialize(TezInputContext _inputContext) throws Exception {
    this.inputContext = _inputContext;
    return initialize();
  }

  public abstract List<Event> initialize() throws Exception;

  public int getNumPhysicalInputs() {
    return numPhysicalInputs;
  }

  public TezInputContext getContext() {
    return inputContext;
  }

}
