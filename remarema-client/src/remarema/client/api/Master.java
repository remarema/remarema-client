package remarema.client.api;


public interface Master {

	NodeConfiguration getConfigurationForNode(String nodeName);

	NodeConfiguration setSuperNodeForCluster(String clusterName);

	NodeConfiguration getConfigurationForParentCluster(String clustername);

	NodeConfiguration getSupernodeFromParentCluster(String clustername);
}
	