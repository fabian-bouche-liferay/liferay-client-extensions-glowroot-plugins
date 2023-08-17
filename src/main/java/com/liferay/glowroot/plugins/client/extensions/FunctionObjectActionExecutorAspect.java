package com.liferay.glowroot.plugins.client.extensions;

import org.glowroot.agent.plugin.api.Agent;
import org.glowroot.agent.plugin.api.MessageSupplier;
import org.glowroot.agent.plugin.api.OptionalThreadContext;
import org.glowroot.agent.plugin.api.TimerName;
import org.glowroot.agent.plugin.api.TraceEntry;
import org.glowroot.agent.plugin.api.weaving.BindParameter;
import org.glowroot.agent.plugin.api.weaving.BindThrowable;
import org.glowroot.agent.plugin.api.weaving.BindTraveler;
import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.OnReturn;
import org.glowroot.agent.plugin.api.weaving.OnThrow;
import org.glowroot.agent.plugin.api.weaving.Pointcut;
import org.glowroot.agent.plugin.api.weaving.Shim;

public class FunctionObjectActionExecutorAspect {

    @Shim("com.liferay.portal.kernel.util.UnicodeProperties")
    public interface UnicodePropertiesShim {
    }
    
    @Shim("com.liferay.portal.kernel.json.JSONObject")
    public interface JSONObjectShim {
    }
    
    private final static String TRANSACTION_TYPE = "client-extensions";
    
	@Pointcut(className = "com.liferay.object.internal.action.executor.FunctionObjectActionExecutorImpl",
			methodName = "execute",
            methodParameterTypes = {
            		"long",
            		"com.liferay.portal.kernel.util.UnicodeProperties",
        			"com.liferay.portal.kernel.json.JSONObject",
        			"long"},
            timerName = "Execute Remote Object Action")
    public static class ExecuteRemoteObjectActionAdvice {

        private static final TimerName timer = Agent.getTimerName(ExecuteRemoteObjectActionAdvice.class);

        @OnBefore
        public static TraceEntry onBefore(OptionalThreadContext context,
        		@BindParameter long companyId,
        		@BindParameter UnicodePropertiesShim parametersUnicodeProperties,
        		@BindParameter JSONObjectShim payloadJSONObject,
        		@BindParameter long userId) {
        	
        	StringBuilder messageBuilder = new StringBuilder();
        	messageBuilder.append("Execute Object");

        	String objectDefinitionId = getObjectDefinitionId(payloadJSONObject.toString());
        	if(objectDefinitionId != null) {
            	messageBuilder.append(" ");
            	messageBuilder.append(objectDefinitionId);
        	}
        	
        	messageBuilder.append(" Remote Action [CompanyId: ");
        	messageBuilder.append(companyId);
        	messageBuilder.append(", UserId: ");
        	messageBuilder.append(userId);

        	String objectEntryId = getObjectEntryId(payloadJSONObject.toString());
        	if(objectEntryId != null) {
            	messageBuilder.append(", ObjectEntryId: ");
            	messageBuilder.append(objectEntryId);
        	}
        	
        	messageBuilder.append("]");
        	
        	if(ClientExtensionsPluginProperties.captureObjectActionRequestsAsOuterTransaction()) {
        		context.setTransactionOuter();

            	StringBuilder transactionNameBuilder = new StringBuilder();
            	transactionNameBuilder.append("Execute Object");

            	if(objectDefinitionId != null) {
            		transactionNameBuilder.append(" ");
            		transactionNameBuilder.append(objectDefinitionId);
            	}

            	transactionNameBuilder.append(" Remote Action");

    			TraceEntry transaction = context.startTransaction(TRANSACTION_TYPE, transactionNameBuilder.toString(), MessageSupplier.create(messageBuilder.toString()), timer);

    			if(ClientExtensionsPluginProperties.captureObjectActionRequestsPayload()) {
        			context.addTransactionAttribute("JSON Payload", JSONUtil.prettyPrintJson(payloadJSONObject.toString()));
        		}
    			
            	return transaction;
        		
        	} else {
        		return context.startTraceEntry(MessageSupplier.create(messageBuilder.toString()), timer);
        	}
    		       		
        }

        @OnReturn
        public static void onReturn(@BindTraveler TraceEntry traceEntry) {
            traceEntry.end();
        }

        @OnThrow
        public static void onThrow(@BindThrowable Throwable throwable,
                @BindTraveler TraceEntry traceEntry) {
            traceEntry.endWithError(throwable);
        }
    }
	
	// Loading an external library for JSON is tricky
	private static String getObjectDefinitionId(String json) {

    	String[] splittedPayload = json.split("\"objectDefinitionId\":");
    	if(splittedPayload.length > 1) {
    		return splittedPayload[1].split("\"")[1];
    	}
    	return null;
    	
	}

	// Loading an external library for JSON is tricky
	private static String getObjectEntryId(String json) {

		String[] splittedPayload = json.split("\"objectEntryId\":");
    	if(splittedPayload.length > 1) {
    		StringBuilder objectEntryIdBuilder = new StringBuilder();
    		for(int i = 0; i < splittedPayload[1].length(); i++) {
        		char c = splittedPayload[1].charAt(i);
        		if(Character.isDigit(c)) {
        			objectEntryIdBuilder.append(c);
        		} else if(Character.isSpaceChar(c)) {
        		} else {
        			break;
        		}
    		}
    		return objectEntryIdBuilder.toString();
    	}
    	return null;
		
	}
}
