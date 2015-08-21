package remarema.client.api;

public class NodeConfiguration {

	private String nodeName;
	private String clusterName;
	private String supernodeURL;

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getSupernodeURL() {
		return supernodeURL;
	}

	public void setSupernode(String supernodeURL) {
		this.supernodeURL = supernodeURL;
	}

}
