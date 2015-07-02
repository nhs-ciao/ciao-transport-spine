package uk.nhs.ciao.transport.spine.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.ciao.CIPRoutes;
import uk.nhs.ciao.camel.CamelApplication;
import uk.nhs.ciao.configuration.CIAOConfig;

/**
 * Configures multiple camel CDA builder routes determined by properties specified
 * in the applications registered {@link CIAOConfig}.
 */
@SuppressWarnings("unused")
public class SpineTransportRoutes extends CIPRoutes {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpineTransportRoutes.class);
	
	/**
	 * The root property 
	 */
	public static final String ROOT_PROPERTY = "cdaBuilderRoutes";
	
	/**
	 * Creates multiple document parser routes
	 * 
	 * @throws RuntimeException If required CIAO-config properties are missing
	 */
	@Override
	public void configure() {
		super.configure();
		
		final CIAOConfig config = CamelApplication.getConfig(getContext());
		// TODO: Complete routes
		
		/*
		 * Documents to send are stored on a JMS queue
		 * Prior to sending the document needs to be converted into
		 * a multi-part request with associated ebXml, hl7, and ITK parts
		 * This multi-part message needs to persist until an ebXml ack is received
		 *  -> a transaction and blocking thread can be used to handle retrys and failover
		 * 
		 * It might be be best to take the original document off the original queue, transform it
		 * into the outgoing message (generating associated tracking / message IDs), and then add
		 * it to a 'sending' queue - this would ensure that an identical outgoing message it sent
		 * during retries (including metadata such as creation time etc)
		 */
		
//		try {
//			
//		} catch (CIAOConfigurationException e) {
//			throw new RuntimeException("Unable to build routes from CIAOConfig", e);
//		}
	}
}