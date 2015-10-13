package uk.nhs.ciao.transport.dts;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.IdempotentRepository;

import com.google.common.base.Strings;

import uk.nhs.ciao.configuration.CIAOConfig;
import uk.nhs.ciao.dts.ControlFile;
import uk.nhs.ciao.transport.dts.address.DTSEndpointAddressHelper;
import uk.nhs.ciao.transport.dts.route.DTSDistributionEnvelopeSenderRoute;
import uk.nhs.ciao.transport.dts.route.DTSMessageReceiverRoute;
import uk.nhs.ciao.transport.dts.sequence.IdSequence;
import uk.nhs.ciao.transport.itk.ITKTransportRoutes;
import uk.nhs.ciao.transport.itk.address.EndpointAddressHelper;
import uk.nhs.ciao.transport.itk.route.DistributionEnvelopeSenderRoute;

public class DTSTransportRoutes extends ITKTransportRoutes {
	@Override
	public void addRoutesToCamelContext(CamelContext context) throws Exception {
		super.addRoutesToCamelContext(context);
		
		// Receivers
		addDTSMessageReceiverRoute(context);
	}
	
	@Override
	protected DistributionEnvelopeSenderRoute createDistributionEnvelopeSenderRoute(
			final CamelContext context, final CIAOConfig config) throws Exception {
		final DTSDistributionEnvelopeSenderRoute route = new DTSDistributionEnvelopeSenderRoute();
		
		route.setDTSMessageSenderUri("file://{{dts.rootFolder}}/OUT");
		route.setDTSMessageSendNotificationReceiverUri("file://{{dts.rootFolder}}/SENT");
		route.setDTSTemporaryFolder("{{dts.temporaryFolder}}");
		route.setDTSFilePrefix(Strings.nullToEmpty(config.getConfigValue("dts.filePrefix")));
		route.setIdempotentRepository(get(context, IdempotentRepository.class, "dtsSentIdempotentRepository"));
		route.setInProgressRepository(get(context, IdempotentRepository.class, "dtsSentInProgressRepository"));
		route.setIdSequence(get(context, IdSequence.class, "dtsIdSequence"));
		
		final ControlFile prototype = new ControlFile();
		prototype.setWorkflowId(config.getConfigValue("dts.workflowId"));
		prototype.setFromDTS(config.getConfigValue("dts.senderMailbox"));
		route.setPrototypeControlFile(prototype);
		
		return route;
	}
	
	@Override
	protected EndpointAddressHelper<?, ?> createEndpointAddressHelper() {
		return new DTSEndpointAddressHelper();
	}
	
	private void addDTSMessageReceiverRoute(final CamelContext context) throws Exception {
		final DTSMessageReceiverRoute route = new DTSMessageReceiverRoute();
		
		route.setDTSMessageReceiverUri("file://{{dts.rootFolder}}/IN");
		route.setPayloadDestinationUri(getDistributionEnvelopeReceiverUri());
		route.setIdempotentRepository(get(context, IdempotentRepository.class, "dtsReceiverIdempotentRepository"));
		route.setInProgressRepository(get(context, IdempotentRepository.class, "dtsReceiverInProgressRepository"));
		
		context.addRoutes(route);
	}
}
