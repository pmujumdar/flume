/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.flume.source;

import java.util.Map;

import org.apache.flume.Channel;
import org.apache.flume.Source;
import org.apache.flume.SourceFactory;
import org.apache.flume.lifecycle.LifecycleState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultSourceFactory {

  private SourceFactory sourceFactory;

  @Before
  public void setUp() {
    sourceFactory = new DefaultSourceFactory();
  }

  @Test
  public void testDuplicateCreate()  {

    Source avroSource1 = sourceFactory.create("avroSource1", "avro");
    Source avroSource2 = sourceFactory.create("avroSource2", "avro");

    Assert.assertNotNull(avroSource1);
    Assert.assertNotNull(avroSource2);
    Assert.assertNotSame(avroSource1, avroSource2);
    Assert.assertTrue(avroSource1 instanceof AvroSource);
    Assert.assertTrue(avroSource2 instanceof AvroSource);

    Source s1 = sourceFactory.create("avroSource1", "avro");
    Source s2 = sourceFactory.create("avroSource2", "avro");

    Assert.assertSame(avroSource1, s1);
    Assert.assertSame(avroSource2, s2);

  }

  private void verifySourceCreation(String name, String type,
      Class<?> typeClass) throws Exception {
    Source src = sourceFactory.create(name, type);
    Assert.assertNotNull(src);
    Assert.assertTrue(typeClass.isInstance(src));
  }

  @Test
  public void testSourceCreation() throws Exception {
    verifySourceCreation("seq-src", "seq", SequenceGeneratorSource.class);
    verifySourceCreation("netcat-src", "netcat", NetcatSource.class);
    verifySourceCreation("exec-src", "exec", ExecSource.class);
    verifySourceCreation("avro-src", "avro", AvroSource.class);
    verifySourceCreation("custom-src", MockSource.class.getCanonicalName(),
        MockSource.class);
  }

  @Test
  public void testSourceRegistry() throws Exception {
    Source s1 = sourceFactory.create("s1", "avro");
    Map<Class<?>, Map<String, Source>> sr =
        ((DefaultSourceFactory) sourceFactory).getRegistryClone();

    Assert.assertEquals(1, sr.size());

    Map<String, Source> srMap = sr.get(AvroSource.class);
    Assert.assertNotNull(srMap);
    Assert.assertEquals(1, srMap.size());

    Source src = srMap.get("s1");
    Assert.assertNotNull(src);
    Assert.assertSame(s1, src);
  }
}
