package com.liferay.glowroot.plugins.client.extensions;

import org.glowroot.agent.plugin.api.Agent;
import org.glowroot.agent.plugin.api.MessageSupplier;
import org.glowroot.agent.plugin.api.OptionalThreadContext;
import org.glowroot.agent.plugin.api.TimerName;
import org.glowroot.agent.plugin.api.TraceEntry;
import org.glowroot.agent.plugin.api.weaving.BindParameterArray;
import org.glowroot.agent.plugin.api.weaving.BindThrowable;
import org.glowroot.agent.plugin.api.weaving.BindTraveler;
import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.OnReturn;
import org.glowroot.agent.plugin.api.weaving.OnThrow;
import org.glowroot.agent.plugin.api.weaving.Pointcut;
import org.glowroot.agent.plugin.api.weaving.Shim;

public class PortalCatapultAspect {
	
    @Shim("com.liferay.portal.kernel.util.Http$Method")
    public interface HttpMethodShim {
    }

	@Pointcut(className = "com.liferay.portal.catapult.internal.PortalCatapultImpl",
			methodName = "launch",
            methodParameterTypes = {".."},
            timerName = "Outbound HTTP")
    public static class OutboundHttpAdvice {

        private static final TimerName timer = Agent.getTimerName(OutboundHttpAdvice.class);

        @OnBefore
        public static TraceEntry onBefore(OptionalThreadContext context,
        		@BindParameterArray Object[] parameters) {

    		StringBuilder messageBuilder = new StringBuilder();
        	messageBuilder.append("Outbound HTTP");

        	try {
	        	long companyId = (long) parameters[0];
	        	HttpMethodShim method = (HttpMethodShim) parameters[1];
	        	String oAuth2ApplicationExternalReferenceCode = (String) parameters[2];
	        	String resourcePath = (String) parameters[4];
	        	long userId = (long) parameters[5];
	        	
	        	messageBuilder.append(" ");
	        	messageBuilder.append(method);
	        	messageBuilder.append(" ");
	        	messageBuilder.append(resourcePath);
	        	messageBuilder.append(" [CompanyId: ");
	        	messageBuilder.append(companyId);
	        	messageBuilder.append(", UserId: ");
	        	messageBuilder.append(userId);
	        	messageBuilder.append(", OAuth2Application: ");
	        	messageBuilder.append(oAuth2ApplicationExternalReferenceCode);
	        	messageBuilder.append("]");
	        	
        	} catch (ClassCastException e) {
        		// In case method signature would change
        	}

        	return context.startTraceEntry(MessageSupplier.create(messageBuilder.toString()), timer);
        	
        }

        @OnReturn
        public static void onReturn(OptionalThreadContext context, 
        		@BindTraveler TraceEntry traceEntry) {
            traceEntry.end();
        }

        @OnThrow
        public static void onThrow(@BindThrowable Throwable throwable,
        		OptionalThreadContext context,
                @BindTraveler TraceEntry traceEntry) {
        	traceEntry.endWithError(throwable);
        }
    }

}
