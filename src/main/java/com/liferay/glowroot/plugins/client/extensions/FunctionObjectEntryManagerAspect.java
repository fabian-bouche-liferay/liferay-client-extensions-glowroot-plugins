package com.liferay.glowroot.plugins.client.extensions;

import org.glowroot.agent.plugin.api.Agent;
import org.glowroot.agent.plugin.api.MessageSupplier;
import org.glowroot.agent.plugin.api.OptionalThreadContext;
import org.glowroot.agent.plugin.api.TimerName;
import org.glowroot.agent.plugin.api.TraceEntry;
import org.glowroot.agent.plugin.api.weaving.BindParameter;
import org.glowroot.agent.plugin.api.weaving.BindReturn;
import org.glowroot.agent.plugin.api.weaving.BindThrowable;
import org.glowroot.agent.plugin.api.weaving.BindTraveler;
import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.OnReturn;
import org.glowroot.agent.plugin.api.weaving.OnThrow;
import org.glowroot.agent.plugin.api.weaving.Pointcut;
import org.glowroot.agent.plugin.api.weaving.Shim;

public class FunctionObjectEntryManagerAspect {

    @Shim("com.liferay.portal.vulcan.dto.converter.DTOConverterContext")
    public interface DTOConverterContextShim {
    	public long getUserId();
    }

    @Shim("com.liferay.object.model.ObjectDefinition")
    public interface ObjectDefinitionShim {
    	public String getExternalReferenceCode();
    }
    
    @Shim("com.liferay.object.rest.dto.v1_0.ObjectEntry")
    public interface ObjectEntryShim {
    	public String getExternalReferenceCode();
    }

    @Shim("com.liferay.portal.kernel.search.Sort")
    public interface SortShim {
    	public String getFieldName();
    	public boolean isReverse();
    }

    @Shim("com.liferay.portal.vulcan.pagination.Pagination")
    public interface PaginationShim {
    	public int getStartPosition();
    	public int getEndPosition();
    }

    @Shim("com.liferay.portal.vulcan.aggregation.Aggregation")
    public interface AggregationShim {
    }
    
    private final static String TRANSACTION_TYPE = "client-extensions";
    
	@Pointcut(className = "com.liferay.object.rest.internal.manager.v1_0.FunctionObjectEntryManagerImpl",
			methodName = "addObjectEntry",
            methodParameterTypes = {
            		"com.liferay.portal.vulcan.dto.converter.DTOConverterContext",
            		"com.liferay.object.model.ObjectDefinition",
        			"com.liferay.object.rest.dto.v1_0.ObjectEntry",
        			"java.lang.String"},
            timerName = "Add Proxy Object Entry")
    public static class AddProxyObjectEntryAdvice {

        private static final TimerName timer = Agent.getTimerName(AddProxyObjectEntryAdvice.class);

        @OnBefore
        public static TraceEntry onBefore(OptionalThreadContext context,
        		@BindParameter DTOConverterContextShim dtoConverterContext,
        		@BindParameter ObjectDefinitionShim objectDefinition,
        		@BindParameter ObjectEntryShim objectEntry,
        		@BindParameter String scopeKey) {
        	
        	StringBuilder messageBuilder = new StringBuilder();
        	messageBuilder.append("Add Proxy Object Entry [ObjectDefinition: ");
        	messageBuilder.append(objectDefinition.getExternalReferenceCode());
        	messageBuilder.append(", ObjectEntry: ");
        	messageBuilder.append(objectEntry.getExternalReferenceCode());
        	messageBuilder.append(", UserId: ");
        	messageBuilder.append(dtoConverterContext.getUserId());
        	messageBuilder.append("]");
        	
        	if(ClientExtensionsPluginProperties.captureProxyObjectRequestsAsOuterTransaction()) {
        		context.setTransactionOuter();

            	StringBuilder transactionNameBuilder = new StringBuilder();
            	transactionNameBuilder.append("Add Proxy Object Entry ");
            	transactionNameBuilder.append(objectDefinition.getExternalReferenceCode());
            	
        		TraceEntry transaction = context.startTransaction(TRANSACTION_TYPE, transactionNameBuilder.toString(), MessageSupplier.create(messageBuilder.toString()), timer);

        		if(ClientExtensionsPluginProperties.captureProxyObjectRequestsDetails()) {
        			context.addTransactionAttribute("Object Entry", JSONUtil.prettyPrintJson(objectEntry.toString()));
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
	
	@Pointcut(className = "com.liferay.object.rest.internal.manager.v1_0.FunctionObjectEntryManagerImpl",
			methodName = "deleteObjectEntry",
            methodParameterTypes = {
            		"long",
            		"com.liferay.portal.vulcan.dto.converter.DTOConverterContext",
					"java.lang.String",
            		"com.liferay.object.model.ObjectDefinition",
        			"java.lang.String"},
            timerName = "Delete Proxy Object Entry")
    public static class DeleteProxyObjectEntryAdvice {

        private static final TimerName timer = Agent.getTimerName(DeleteProxyObjectEntryAdvice.class);

        @OnBefore
        public static TraceEntry onBefore(OptionalThreadContext context,
        		@BindParameter long companyId,
        		@BindParameter DTOConverterContextShim dtoConverterContext,
        		@BindParameter String externalReferenceCode,
        		@BindParameter ObjectDefinitionShim objectDefinition,
        		@BindParameter String scopeKey) {
        	
        	StringBuilder messageBuilder = new StringBuilder();
        	messageBuilder.append("Delete Proxy Object Entry [ObjectDefinition: ");
        	messageBuilder.append(objectDefinition.getExternalReferenceCode());
        	messageBuilder.append(", ObjectEntry: ");
        	messageBuilder.append(externalReferenceCode);
        	messageBuilder.append(", UserId: ");
        	messageBuilder.append(dtoConverterContext.getUserId());
        	messageBuilder.append("]");        	
        	
        	if(ClientExtensionsPluginProperties.captureProxyObjectRequestsAsOuterTransaction()) {

            	StringBuilder transactionNameBuilder = new StringBuilder();
            	transactionNameBuilder.append("Delete Proxy Object Entry ");
            	transactionNameBuilder.append(objectDefinition.getExternalReferenceCode());
            	
        		context.setTransactionOuter();

        		TraceEntry transaction = context.startTransaction(TRANSACTION_TYPE, transactionNameBuilder.toString(), MessageSupplier.create(messageBuilder.toString()), timer);

        		if(ClientExtensionsPluginProperties.captureProxyObjectRequestsDetails()) {
        			// Adding transaction attributes here
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
	
	@Pointcut(className = "com.liferay.object.rest.internal.manager.v1_0.FunctionObjectEntryManagerImpl",
			methodName = "getObjectEntries",
            methodParameterTypes = {
            		"long",
            		"com.liferay.object.model.ObjectDefinition",
					"java.lang.String",
					"com.liferay.portal.vulcan.aggregation.Aggregation",
            		"com.liferay.portal.vulcan.dto.converter.DTOConverterContext",
					"java.lang.String",
					"com.liferay.portal.vulcan.pagination.Pagination",
        			"java.lang.String",
        			"com.liferay.portal.kernel.search.Sort[]"},
            timerName = "Get Proxy Object Entries")
    public static class GetProxyObjectEntriesAdvice {

        private static final TimerName timer = Agent.getTimerName(GetProxyObjectEntriesAdvice.class);

        @OnBefore
        public static TraceEntry onBefore(OptionalThreadContext context,
        		@BindParameter long companyId,
        		@BindParameter ObjectDefinitionShim objectDefinition,
        		@BindParameter String scopeKey,
        		@BindParameter AggregationShim aggregation,
        		@BindParameter DTOConverterContextShim dtoConverterContext,
        		@BindParameter String filterString,
        		@BindParameter PaginationShim pagination,
        		@BindParameter String search,
        		@BindParameter SortShim[] sorts) {
        	
        	StringBuilder messageBuilder = new StringBuilder();
        	messageBuilder.append("Get Proxy Object Entries [ObjectDefinition: ");
        	messageBuilder.append(objectDefinition.getExternalReferenceCode());
        	messageBuilder.append(", UserId: ");
        	messageBuilder.append(dtoConverterContext.getUserId());
        	messageBuilder.append(", search: ");
        	messageBuilder.append(search);
        	messageBuilder.append(", filter: ");
        	messageBuilder.append(filterString);
        	messageBuilder.append(", range: ");
        	messageBuilder.append(pagination.getStartPosition());
        	messageBuilder.append(" - ");
        	messageBuilder.append(pagination.getEndPosition());
        	messageBuilder.append("]");

        	if(ClientExtensionsPluginProperties.captureProxyObjectRequestsAsOuterTransaction()) {
        		
            	StringBuilder transactionNameBuilder = new StringBuilder();
            	transactionNameBuilder.append("Get Proxy Object Entries ");
            	transactionNameBuilder.append(objectDefinition.getExternalReferenceCode());
        		
        		context.setTransactionOuter();

        		TraceEntry transaction = context.startTransaction(TRANSACTION_TYPE, transactionNameBuilder.toString(), MessageSupplier.create(messageBuilder.toString()), timer);

        		if(ClientExtensionsPluginProperties.captureProxyObjectRequestsDetails()) {
        			// Adding transaction attributes here
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

	@Pointcut(className = "com.liferay.object.rest.internal.manager.v1_0.FunctionObjectEntryManagerImpl",
			methodName = "getObjectEntry",
            methodParameterTypes = {
            		"long",
            		"com.liferay.portal.vulcan.dto.converter.DTOConverterContext",
					"java.lang.String",
            		"com.liferay.object.model.ObjectDefinition",
        			"java.lang.String"},
            timerName = "Get Proxy Object Entry")
    public static class GetProxyObjectEntryAdvice {

        private static final TimerName timer = Agent.getTimerName(GetProxyObjectEntryAdvice.class);

        @OnBefore
        public static TraceEntry onBefore(OptionalThreadContext context,
        		@BindParameter long companyId,
        		@BindParameter DTOConverterContextShim dtoConverterContext,
        		@BindParameter String externalReferenceCode,
        		@BindParameter ObjectDefinitionShim objectDefinition,
        		@BindParameter String scopeKey) {
        	
        	StringBuilder messageBuilder = new StringBuilder();
        	messageBuilder.append("Get Proxy Object Entry [ObjectDefinition: ");
        	messageBuilder.append(objectDefinition.getExternalReferenceCode());
        	messageBuilder.append(", ObjectEntry: ");
        	messageBuilder.append(externalReferenceCode);
        	messageBuilder.append(", UserId: ");
        	messageBuilder.append(dtoConverterContext.getUserId());
        	messageBuilder.append("]"); 
        	
        	if(ClientExtensionsPluginProperties.captureProxyObjectRequestsAsOuterTransaction()) {

            	StringBuilder transactionNameBuilder = new StringBuilder();
            	transactionNameBuilder.append("Get Proxy Object Entry ");
            	transactionNameBuilder.append(objectDefinition.getExternalReferenceCode());

        		context.setTransactionOuter();

        		TraceEntry transaction = context.startTransaction(TRANSACTION_TYPE, transactionNameBuilder.toString(), MessageSupplier.create(messageBuilder.toString()), timer);

        		if(ClientExtensionsPluginProperties.captureProxyObjectRequestsDetails()) {
        			// Adding transaction attributes here
        		}

            	return transaction;
        		
        	} else {
        		return context.startTraceEntry(MessageSupplier.create(messageBuilder.toString()), timer);
        	}
        }

        @OnReturn
        public static void onReturn( @BindReturn ObjectEntryShim objectEntry, 
        		OptionalThreadContext context, 
        		@BindTraveler TraceEntry traceEntry) {
        	if(ClientExtensionsPluginProperties.captureProxyObjectRequestsAsOuterTransaction()) {
        		if(ClientExtensionsPluginProperties.captureProxyObjectRequestsDetails()) {
        			context.addTransactionAttribute("Object Entry", JSONUtil.prettyPrintJson(objectEntry.toString()));
        		}
        	}
            traceEntry.end();
        }

        @OnThrow
        public static void onThrow(@BindThrowable Throwable throwable,
                @BindTraveler TraceEntry traceEntry) {
            traceEntry.endWithError(throwable);
        }
    }
	
	@Pointcut(className = "com.liferay.object.rest.internal.manager.v1_0.FunctionObjectEntryManagerImpl",
			methodName = "updateObjectEntry",
            methodParameterTypes = {
            		"long",
            		"com.liferay.portal.vulcan.dto.converter.DTOConverterContext",
					"java.lang.String",
            		"com.liferay.object.model.ObjectDefinition",
        			"com.liferay.object.rest.dto.v1_0.ObjectEntry",
        			"java.lang.String"},
            timerName = "Update Proxy Object Entry")
    public static class UpdateProxyObjectEntryAdvice {

        private static final TimerName timer = Agent.getTimerName(UpdateProxyObjectEntryAdvice.class);

        @OnBefore
        public static TraceEntry onBefore(OptionalThreadContext context,
        		@BindParameter long companyId,
        		@BindParameter DTOConverterContextShim dtoConverterContext,
        		@BindParameter String externalReferenceCode,
        		@BindParameter ObjectDefinitionShim objectDefinition,
        		@BindParameter ObjectEntryShim objectEntry,
        		@BindParameter String scopeKey) {
        	
        	StringBuilder messageBuilder = new StringBuilder();
        	messageBuilder.append("Update Proxy Object Entry [ObjectDefinition: ");
        	messageBuilder.append(objectDefinition.getExternalReferenceCode());
        	messageBuilder.append(", ObjectEntry: ");
        	messageBuilder.append(externalReferenceCode);
        	messageBuilder.append(", UserId: ");
        	messageBuilder.append(dtoConverterContext.getUserId());
        	messageBuilder.append("]");         	
        	
        	if(ClientExtensionsPluginProperties.captureProxyObjectRequestsAsOuterTransaction()) {
        		context.setTransactionOuter();

            	StringBuilder transactionNameBuilder = new StringBuilder();
            	transactionNameBuilder.append("Update Proxy Object Entry ");
            	transactionNameBuilder.append(objectDefinition.getExternalReferenceCode());
            	
        		TraceEntry transaction = context.startTransaction(TRANSACTION_TYPE, transactionNameBuilder.toString(), MessageSupplier.create(messageBuilder.toString()), timer);

        		if(ClientExtensionsPluginProperties.captureProxyObjectRequestsDetails()) {
        			context.addTransactionAttribute("Object Entry", JSONUtil.prettyPrintJson(objectEntry.toString()));
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
