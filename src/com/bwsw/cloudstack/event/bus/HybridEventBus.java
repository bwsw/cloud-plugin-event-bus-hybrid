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

import com.cloud.utils.component.ManagerBase;
import org.apache.cloudstack.framework.events.Event;
import org.apache.cloudstack.framework.events.EventBus;
import org.apache.cloudstack.framework.events.EventBusException;
import org.apache.cloudstack.framework.events.EventSubscriber;
import org.apache.cloudstack.framework.events.EventTopic;

import javax.naming.ConfigurationException;
import java.util.Map;
import java.util.UUID;

public class HybridEventBus extends ManagerBase implements EventBus {

    private EventBus _internalEventBus;
    private EventBus _externalEventBus;

    public void setInternalEventBus(EventBus internalEventBus) {
        this._internalEventBus = internalEventBus;
    }

    public void setExternalEventBus(EventBus externalEventBus) {
        this._externalEventBus = externalEventBus;
    }

    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
        super.configure(name, params);
        if (_internalEventBus == null || _externalEventBus == null) {
            throw new ConfigurationException("Both internal and external event buses should be specified");
        }
        return true;
    }

    @Override
    public void publish(Event event) throws EventBusException {
        _internalEventBus.publish(event);
        _externalEventBus.publish(event);
    }

    @Override
    public UUID subscribe(EventTopic eventTopic, EventSubscriber eventSubscriber) throws EventBusException {
        return _internalEventBus.subscribe(eventTopic, eventSubscriber);
    }

    @Override
    public void unsubscribe(UUID uuid, EventSubscriber eventSubscriber) throws EventBusException {
        _internalEventBus.unsubscribe(uuid, eventSubscriber);
    }
}
