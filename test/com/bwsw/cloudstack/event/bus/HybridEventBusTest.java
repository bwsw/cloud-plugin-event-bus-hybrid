// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.bwsw.cloudstack.event.bus;

import com.cloud.event.EventCategory;
import com.cloud.event.EventTypes;
import org.apache.cloudstack.framework.events.Event;
import org.apache.cloudstack.framework.events.EventBus;
import org.apache.cloudstack.framework.events.EventBusException;
import org.apache.cloudstack.framework.events.EventSubscriber;
import org.apache.cloudstack.framework.events.EventTopic;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HybridEventBusTest {

    private static final String NAME = "hybridEventBus";
    private static final Map<String, Object> PARAMS = new HashMap<>();
    private static final UUID SUBSCRIBER_UUID = UUID.randomUUID();

    @Rule
    public ExpectedException _expectedException = ExpectedException.none();

    @Mock
    private EventBus _internalEventBus;

    @Mock
    private EventBus _externalEventBus;

    @Mock
    private Event _event;

    @Mock
    private EventSubscriber _eventSubscriber;

    @InjectMocks
    private HybridEventBus _hybridEventBus = new HybridEventBus();

    @Test
    public void testConfigure() throws ConfigurationException {
        boolean result = _hybridEventBus.configure(NAME, PARAMS);

        assertTrue(result);
        assertEquals(NAME, _hybridEventBus.getName());
        assertEquals(PARAMS, _hybridEventBus.getConfigParams());
    }

    @Test
    public void testConfigureNullInternalEventBus() throws ConfigurationException {
        testConfigureNullEventBus(getHybridEventBus(null, _externalEventBus));
    }

    @Test
    public void testConfigureNullExternalEventBus() throws ConfigurationException {
        testConfigureNullEventBus(getHybridEventBus(_internalEventBus, null));
    }

    @Test
    public void testPublish() throws EventBusException {
        _hybridEventBus.publish(_event);

        verify(_internalEventBus, only()).publish(_event);
        verify(_externalEventBus, only()).publish(_event);
    }

    @Test
    public void testPublishInternalEventBusException() throws EventBusException {
        EventBusException exception = new EventBusException("internal bus");
        expectException(exception);
        doThrow(exception).when(_internalEventBus).publish(_event);

        _hybridEventBus.publish(_event);

        verify(_internalEventBus, only()).publish(_event);
        verifyZeroInteractions(_externalEventBus);
    }

    @Test
    public void testPublishExternalEventBusException() throws EventBusException {
        EventBusException exception = new EventBusException("external bus");
        expectException(exception);
        doThrow(exception).when(_externalEventBus).publish(_event);

        _hybridEventBus.publish(_event);

        verify(_internalEventBus, only()).publish(_event);
        verify(_externalEventBus, only()).publish(_event);
    }

    @Test
    public void testSubscribe() throws EventBusException {
        EventTopic eventTopic = new EventTopic(EventCategory.ACTION_EVENT.getName(), EventTypes.EVENT_VM_CREATE, null, null, null);
        when(_internalEventBus.subscribe(eventTopic, _eventSubscriber)).thenReturn(SUBSCRIBER_UUID);

        UUID result = _hybridEventBus.subscribe(eventTopic, _eventSubscriber);

        assertSame(SUBSCRIBER_UUID, result);
        verifyZeroInteractions(_externalEventBus);
    }

    @Test
    public void testUnsubscribe() throws EventBusException {
        doNothing().when(_internalEventBus).unsubscribe(SUBSCRIBER_UUID, _eventSubscriber);

        _hybridEventBus.unsubscribe(SUBSCRIBER_UUID, _eventSubscriber);

        verify(_internalEventBus, only()).unsubscribe(SUBSCRIBER_UUID, _eventSubscriber);
        verifyZeroInteractions(_externalEventBus);
    }

    private void testConfigureNullEventBus(HybridEventBus eventBus) throws ConfigurationException {
        _expectedException.expect(ConfigurationException.class);

        eventBus.configure(NAME, PARAMS);
    }

    private HybridEventBus getHybridEventBus(EventBus internalEventBus, EventBus externalEventBus) {
        HybridEventBus eventBus = new HybridEventBus();
        eventBus.setInternalEventBus(internalEventBus);
        eventBus.setExternalEventBus(externalEventBus);
        return eventBus;
    }

    private void expectException(Exception exception) {
        _expectedException.expect(exception.getClass());
        _expectedException.expectMessage(exception.getMessage());
    }
}
