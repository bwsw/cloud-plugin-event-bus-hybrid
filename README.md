Apache CloudStack Plugin - Hybrid Event Bus
===========================================

This project provides hybrid event bus. This event bus allows to use different event buses for internal (inside Apache CloudStack) and external event notification.
The version of the plugin matches Apache CloudStack version that it is build for.

The plugin is developed and tested only with Apache CloudStack 4.11.1.

* [Installing into CloudStack](#installing-into-cloudstack)

# Installing into CloudStack

Download the plugin jar with dependencies file from OSS Nexus (https://oss.sonatype.org/content/groups/public/com/bwsw/cloud-plugin-event-bus-hybrid/) which corresponds to your ACS 
version (e.g. 4.11.1.0), put it to lib directory and restart Management server. In Ubuntu installation which is based on deb package:

```
cd /usr/share/cloudstack-management/lib/
wget --trust-server-names "https://oss.sonatype.org/service/local/artifact/maven/redirect?r=snapshots&g=com.bwsw&a=cloud-plugin-event-bus-hybrid&c=jar-with-dependencies&v=4.11.1.0-SNAPSHOT"
service cloudstack-management stop
service cloudstack-management start
```
