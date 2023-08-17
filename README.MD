# Liferay Glowroot plugin

##Important

In portal-ext.properties, the property
`module.framework.properties.org.osgi.framework.bootdelegation` must include this:

```
    org.glowroot.agent,\
    org.glowroot.agent.*,\
    com.liferay.fbo.glowroot.plugins.templates
```

They must be appended at the end of the default value of the property.
Be careful, the value is different from one Liferay version to the other.

This is important because otherwise, you'll have some classloading issues.

## Deploy plugin

`liferay-fbo-glowroot-plugins.jar` must be deployed to `glowroot/plugins/`
Glowroot does not support hot deploy. JVM has to restart for plugins to
be applied.