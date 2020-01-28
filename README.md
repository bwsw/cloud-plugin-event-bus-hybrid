Apache CloudStack Plugin - Hybrid Event Bus
===========================================

This project provides hybrid event bus. This event bus allows to use different event buses for internal (inside Apache CloudStack) and external event notification.
All events are published to both internal and external event buses. Subscribe/unsubscribe actions are executed on internal event bus only. 
The version of the plugin matches Apache CloudStack version that it is build for.

The plugin is developed and tested only with Apache CloudStack 4.13.

* [Installing into CloudStack](#installing-into-cloudstack)

# Installing into CloudStack

Download the plugin jar with dependencies file from OSS Nexus (https://oss.sonatype.org/content/groups/public/com/bwsw/cloud-plugin-event-bus-hybrid/) which corresponds to your ACS 
version (e.g. 4.13), put it to lib directory, add [event bus configuration](http://docs.cloudstack.apache.org/projects/cloudstack-administration/en/4.13/events.html) and restart Management server. 

An example of hybrid event bus configuration:
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">

    <bean id="eventNotificationBus" class="com.bwsw.cloudstack.event.bus.HybridEventBus">
        <property name="name" value="eventNotificationBus"/>
        <property name="internalEventBus">
            <bean class="org.apache.cloudstack.mom.inmemory.InMemoryEventBus">
                <property name="name" value="internalEventNotificationBus"/>
            </bean>
        </property>
        <property name="externalEventBus">
            <bean class="org.apache.cloudstack.mom.rabbitmq.RabbitMQEventBus">
                <property name="name" value="externalEventNotificationBus"/>
                <property name="server" value="127.0.0.1"/>
                <property name="port" value="5672"/>
                <property name="username" value="guest"/>
                <property name="password" value="guest"/>
                <property name="exchange" value="cloudstack-events"/>
            </bean>
        </property>
    </bean>
</beans>
```
The configuration file `spring-event-bus-context.xml` with the content like specified above should be in `/etc/cloudstack/management/META-INF/cloudstack/core` directory.

To add plugin in Ubuntu installation which is based on deb package:
```
cd /usr/share/cloudstack-management/lib/
wget --trust-server-names "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.bwsw&a=cloud-plugin-event-bus-hybrid&c=jar-with-dependencies&v=4.11.2.0-SNAPSHOT"
service cloudstack-management stop
service cloudstack-management start
```
