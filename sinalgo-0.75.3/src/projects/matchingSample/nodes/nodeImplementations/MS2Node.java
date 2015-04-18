package projects.matchingSample.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Connections;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

public class MS2Node extends Node {

	Integer pointingNode; 
	boolean isAllowed_To_Move;
	public boolean end_flag;
	Edge married_egde;
	double threshold_probability = 0.5;
	
	Logging myLog = Logging.getLogger("logAlgorithm2.txt");
	
	private Edge getEdgeByEndNode(Integer nodeID){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			Edge e = it.next();
			if(e.endNode.ID == nodeID){
				return e;
			}
		}
		return null;
	}
	public boolean getEndFlag(){
		return this.end_flag; // this.end_flag;
	}
	public void setThresholdProbability(double k){
		this.threshold_probability = k;
	}
	public int getPointingNode(){
		return this.pointingNode;
	}	
	@Override
	public void handleMessages(Inbox inbox) {
		// TODO Auto-generated method stub
	}

	@Override
	public void preStep() {
		// TODO Auto-generated method stub
		myLog.logln("Node: "+this.ID+" PRE_STEP ");
		if(Tools.getRandomNumberGenerator().nextDouble()<=this.threshold_probability){
			this.isAllowed_To_Move = true;
		}else{
			this.isAllowed_To_Move = false;
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		this.pointingNode = -1 ;
		this.end_flag = false;
		this.married_egde = null;
		this.setColor(Color.BLUE);

	}

	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		myLog.logln("Node: "+this.ID+" POST_STEP ");
		if(this.isAllowed_To_Move){
			myLog.logln("Node: "+this.ID+"Scheduler decides that i can run");
			if(this.matchingRule()){
				myLog.logln("NodeID:"+this.ID+"does matching!!");
				return;
			}
			if(this.seductionRule()){
				myLog.logln("NodeID:"+this.ID+"does seduction rule!!");
				return;
			}
			if(this.abandonmentRule()){
				myLog.logln("NodeID:"+this.ID+"does abandonment rule!!");
				return;
			}
			this.end_flag = true;
			myLog.logln("--------------------NodeID:"+this.ID+".....cannot move anymore!!");
		}else{
			myLog.logln("Node: "+this.ID+"Cannot execute...Try to next round!!");
		}

	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		// TODO Auto-generated method stub
		super.drawNodeAsDiskWithText(g, pt, highlight, Integer.toString(this.ID), 14, Color.WHITE);
		//this.setColor(Color.BLUE);
		//Tools.repaintGUI();
	}
	private MS2Node checkForMarriageNeighbor(){
		Iterator<Edge> it = this.outgoingConnections.iterator();
		while(it.hasNext()){
			MS2Node n = (MS2Node)it.next().endNode;
			if(n.getPointingNode()==this.ID){
				return n;
			}
		}
		return null;
	}
	private MS2Node getMarriableNode(){
		Iterator<Edge> it = this.outgoingConnections.iterator();
		while(it.hasNext()){
			MS2Node n = (MS2Node)it.next().endNode;
			if(n.getPointingNode()==-1){
				return n;
			}
		}
		return null;
	}
	
	private MS2Node getNodeByID(int id){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			MS2Node temp = (MS2Node)it.next().endNode;
			if(temp.ID == id){
				return temp;
			}
		}
		return null;
	}
	/**
	 *  three mutual exclusive guarded rules
	 * @return
	 */
	public boolean matchingRule(){
		MS2Node j;
		if(this.pointingNode == -1 && (j=this.checkForMarriageNeighbor())!=null){
			this.pointingNode = j.ID;
			myLog.logln("******* NOW NodeID "+this.ID+" is married with: "+j.ID);
			this.setColor(Color.GREEN);
			j.setColor(Color.GREEN);
			Edge e = this.getEdgeByEndNode(j.ID);
			if(e!=null){
				e.defaultColor = Color.GREEN;
			}
			else{
				myLog.logln("??????????? NOW NodeID "+this.ID+" EDGE NULL");
			}
			this.married_egde = e;
			Tools.repaintGUI();
			return true;
		}
		return false;
	}
	
	/**
	 * Check if this process can seduce with 1 of his neighbors
	 * @return True if this rule can be activate, false otherwise
	 */
	public boolean seductionRule(){
		MS2Node n;
		if(this.pointingNode == -1 && this.checkForMarriageNeighbor()==null && (n = this.getMarriableNode())!=null){
			this.pointingNode = n.ID;
			return true;
		}
		return false;
	}
	
	/**
	 * Check if this process must abandon 
	 * @return True if this rule can be activate, false otherwise
	 */
	public boolean abandonmentRule(){
		MS2Node temp = this.getNodeByID(this.pointingNode);
		if(this.pointingNode!=-1 && temp.getPointingNode()!=this.ID && temp.getPointingNode()!=-1){
			this.pointingNode = -1;
			this.setColor(Color.BLUE);
			Tools.repaintGUI();
			return true;
		}
		return false;
	}
	
	

}
