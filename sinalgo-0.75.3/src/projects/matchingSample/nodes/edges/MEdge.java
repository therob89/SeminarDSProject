package projects.matchingSample.nodes.edges;

import sinalgo.nodes.edges.BidirectionalEdge;

public class MEdge extends BidirectionalEdge {

	private Long secondID;
	
	public void setSecondID(Long new_ID){
		this.secondID = new_ID;
	}
	public Long getSecondID(){
		return this.secondID;
	}
}
