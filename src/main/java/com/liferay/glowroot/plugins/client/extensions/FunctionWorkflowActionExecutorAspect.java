package com.liferay.glowroot.plugins.client.extensions;

import java.io.Serializable;
import java.util.Map;

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

public class FunctionWorkflowActionExecutorAspect {

    @Shim("com.liferay.portal.workflow.kaleo.model.KaleoAction")
    public interface KaleoActionShim {
    	public long getCompanyId();
    	public long getUserId();
    	public String getKaleoNodeName();
    	public String getName();
    	public String getDescription();
    }
    
    @Shim("com.liferay.portal.workflow.kaleo.runtime.ExecutionContext")
    public interface ExecutionContextShim {
    	public Map<String, Serializable> getWorkflowContext();
    }
    
    private final static String TRANSACTION_TYPE = "client-extensions";
    
	@Pointcut(className = "com.liferay.portal.workflow.kaleo.runtime.internal.action.executor.FunctionActionExecutorImpl",
			methodName = "execute",
            methodParameterTypes = {
            		"com.liferay.portal.workflow.kaleo.model.KaleoAction",
        			"com.liferay.portal.workflow.kaleo.runtime.ExecutionContext"},
            timerName = "Execute Workflow Action")
    public static class ExecuteWorkflowActionAdvice {

        private static final TimerName timer = Agent.getTimerName(ExecuteWorkflowActionAdvice.class);

        @OnBefore
        public static TraceEntry onBefore(OptionalThreadContext context,
        		@BindParameter KaleoActionShim kaleoAction,
        		@BindParameter ExecutionContextShim executionContext) {

        	long companyId = kaleoAction.getCompanyId();
        	long userId = kaleoAction.getUserId();

        	StringBuilder messageBuilder = new StringBuilder();
        	messageBuilder.append("Execute Workflow Action ");
        	messageBuilder.append(kaleoAction.getName());
        	messageBuilder.append(" [CompanyId: ");
        	messageBuilder.append(companyId);
        	messageBuilder.append(", UserId: ");
        	messageBuilder.append(userId);
        	messageBuilder.append(", KaleoNodeName: ");
        	messageBuilder.append(kaleoAction.getKaleoNodeName());
        	messageBuilder.append("]");
        	
        	if(ClientExtensionsPluginProperties.captureWorkflowActionRequestsAsOuterTransaction()) {
        		context.setTransactionOuter();

            	StringBuilder transactionNameBuilder = new StringBuilder();
            	transactionNameBuilder.append("Execute Workflow Action ");
            	transactionNameBuilder.append(kaleoAction.getName());

    			TraceEntry transaction = context.startTransaction(TRANSACTION_TYPE, transactionNameBuilder.toString(), MessageSupplier.create(messageBuilder.toString()), timer);

    			context.addTransactionAttribute("Description", kaleoAction.getDescription());
    			
    			if(ClientExtensionsPluginProperties.captureWorkflowActionContext()) {
    				executionContext.getWorkflowContext().keySet().forEach(key -> {
    					String value = executionContext.getWorkflowContext().get(key).toString();
            			context.addTransactionAttribute("Workflow Context [" + key + "]", value);
    				});
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

}
