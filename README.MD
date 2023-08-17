# Liferay Client Extensions Glowroot plugin

The aim of this project is to offer a Glowroot plugin capable of monitoring some of the new Liferay Client Extensions capabilities.

1/ There's a new beta feature in Liferay 7.4 which allows you to rely on an external Object Entry Manager instead of using Liferay as the persistence layer. In that case, you are going to write an external REST API using either Spring Boot or NodeJS for example. When you create a new object definition, you can make it target this external REST API. Whenever Liferay needs to read / write / update / delete entries, it is going to make calls to that API. This glowroot plugin captures timing information in order to help you identify performance issues and debug payloads if required.

2/ With Liferay Objects, you can create custom Object Actions instead of using the built in ones. Such object action is going to make a call to an external REST API which you can write using Spring Boot or NodeJS for example. This glowroot plugin is going to monitor those external calls in order to help you identify performance issues and debug payloads if required.

## Important

This plugin is for DXP 7.4 only and was last tested with Update 89.

In portal-ext.properties, the property
`module.framework.properties.org.osgi.framework.bootdelegation` must include this:

```
    org.glowroot.agent,\
    org.glowroot.agent.*,\
    com.liferay.glowroot.plugins.client.extensions
```

They must be appended at the end of the default value of the property.
Be careful, the value is different from one Liferay version to the other.

This is important because otherwise, you'll have some classloading issues.

## Deploy plugin

`liferay-client-extensions-glowroot-plugins.jar` must be deployed to `glowroot/plugins/`
Glowroot does not support hot deploy. JVM has to restart for plugins to
be applied.

## Warning - weight of trace entries

Configuration allows you to capture individual client extension transactions with optional full payloads. This can lead to expensive processing and disk usage.