package remarema.client.api;


public interface Master {

	NodeConfiguration getConfigurationForNode(String nodeName);

	boolean canIhazSupernodePleaz(NodeConfiguration nodeConfiguration);

}
