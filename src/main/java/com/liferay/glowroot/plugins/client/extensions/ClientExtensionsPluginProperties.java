package com.liferay.glowroot.plugins.client.extensions;

import org.glowroot.agent.plugin.api.Agent;
import org.glowroot.agent.plugin.api.config.ConfigListener;
import org.glowroot.agent.plugin.api.config.ConfigService;

public class ClientExtensionsPluginProperties {

	private static final ConfigService configService = Agent.getConfigService("liferay-client-extensions-plugin");
	
	private static boolean captureProxyObjectRequestsAsOuterTransaction;
	private static boolean captureProxyObjectRequestsDetails;
	
	private static boolean captureObjectActionRequestsAsOuterTransaction;
	private static boolean captureObjectActionRequestsPayload;
	
	private static boolean captureWorkflowActionRequestsAsOuterTransaction;
	private static boolean captureWorkflowActionContext;
	
	static {
        configService.registerConfigListener(new ClientExtensionsPluginConfigListener());
    }
    
    public static boolean captureProxyObjectRequestsAsOuterTransaction() {
        return captureProxyObjectRequestsAsOuterTransaction;
    }
    
    public static boolean captureProxyObjectRequestsDetails() {
        return captureProxyObjectRequestsDetails;
    }

    public static boolean captureObjectActionRequestsAsOuterTransaction() {
        return captureObjectActionRequestsAsOuterTransaction;
    }
    
    public static boolean captureObjectActionRequestsPayload() {
        return captureObjectActionRequestsPayload;
    }
    
	public static boolean captureWorkflowActionRequestsAsOuterTransaction() {
		return captureWorkflowActionRequestsAsOuterTransaction;
	}

	public static boolean captureWorkflowActionContext() {
		return captureWorkflowActionContext;
	}	
	
    private static class ClientExtensionsPluginConfigListener implements ConfigListener {

        @Override
        public void onChange() {
            recalculateProperties();
        }

        private static void recalculateProperties() {
        	
        	captureProxyObjectRequestsAsOuterTransaction =
                    configService.getBooleanProperty("captureProxyObjectRequestsAsOuterTransaction").value();

        	captureProxyObjectRequestsDetails =
                    configService.getBooleanProperty("captureProxyObjectRequestsDetails").value();

        	captureObjectActionRequestsAsOuterTransaction =
                    configService.getBooleanProperty("captureObjectActionRequestsAsOuterTransaction").value();

        	captureObjectActionRequestsPayload =
                    configService.getBooleanProperty("captureObjectActionRequestsPayload").value();
        
        	captureWorkflowActionRequestsAsOuterTransaction =
                    configService.getBooleanProperty("captureWorkflowActionRequestsAsOuterTransaction").value();
        	
        	captureWorkflowActionContext =
                    configService.getBooleanProperty("captureWorkflowActionContext").value();
        }
 
    }

}
