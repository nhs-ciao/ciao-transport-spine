package uk.nhs.ciao.transport.dts.route;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;

import uk.nhs.ciao.camel.BaseRouteBuilder;
import uk.nhs.ciao.camel.CamelUtils;

/**
 * Example class showing how incoming pairs of DTS *.ctl and *.dat files are handled.
 * <p>
 * Incoming payload is logged to the console with details of propagated DTS headers. The file management
 * of IN / error directory is performed by the route.
 */
public class DTSIncomingFileExample implements RoutesBuilder {	
	public static void main(final String[] args) throws Exception {
		final SimpleRegistry registry = new SimpleRegistry();
		final CamelContext context = new DefaultCamelContext(registry);
		try {
			context.addRoutes(new DTSIncomingFileExample(registry));
			context.start();
			new CountDownLatch(1).await();
		} finally {
			CamelUtils.stopQuietly(context);
		}
	}
	
	private final SimpleRegistry registry;
	
	public DTSIncomingFileExample(final SimpleRegistry registry) {
		this.registry = registry;
	}
	
	@Override
	public void addRoutesToCamelContext(final CamelContext context) throws Exception {
		addDTSMessageReceiver(context);
		addPayloadDestination(context);
	}
	
	private void addDTSMessageReceiver(final CamelContext context) throws Exception {
		final DTSMessageReceiverRoute route = new DTSMessageReceiverRoute();
		
		registry.put("idempotentRepository", new MemoryIdempotentRepository());
		route.setIdempotentRepositoryId("idempotentRepository");
		
		registry.put("inProgressRepository", new MemoryIdempotentRepository());
		route.setInProgressRepositoryId("inProgressRepository");
		
		route.setErrorFolder("error");
		route.setDTSMessageReceiverUri("file://./target/example");
		route.setPayloadDestinationUri("seda:payload-destination");
		route.setWorflowIds(Arrays.asList("workflow-1", "workflow-2"));
		
		context.addRoutes(route);
	}
	
	private void addPayloadDestination(final CamelContext context) throws Exception {
		context.addRoutes(new PayloadDestinationRoute());
	}
	
	private class PayloadDestinationRoute extends BaseRouteBuilder {
		@Override
		public void configure() throws Exception {
			from("seda:payload-destination").id("payload-destination")
				.log("Got payload from DTS - headers: fromDTS=${header.dtsFromDTS}, toDTS=${header.dtsFromDTS}, workflowId=${header.dtsWorkflowId}\n${body}")
			.end();
		}
	}
}
