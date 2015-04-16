package projects.matchingSample.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

import projects.matchingSample.nodes.messages.MSMessage;
import projects.matchingSample.nodes.timers.MSTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Connections;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;
//			t.startRelative(Tools.getRandomNumberGenerator().nextDouble(), this);

public class MSNode extends Node {

	public static boolean isSending = true;
	boolean isMarried;
	Integer pointingNode; 
	boolean isEligibile;
	int interval;
	
	Logging myLog = Logging.getLogger("myLog.txt");
	
	
	/**
	 * Check if neighbor was pointing to me
	 * @return The id of neighbor such that neighbor.ID == self,  -1 otherwise
	 */
	private int checkNeighborForMarriage(){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			MSNode temp = (MSNode) it.next().endNode;
			if(temp.pointingNode == this.ID){
				return temp.ID;
			}
		}
		return -1;
		
		
		
	}
	/**
	 * Check trough neighbor (maxID) to find one that can be available with a certain condition
	 * @return The id of neighbor such that neighbor.ID == self,  -1 otherwise
	 */
	private int checkMaxNeighborForMarriageWithNullPreference(){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		Integer t = -1;
		while(it.hasNext()){
			MSNode temp = (MSNode)it.next().endNode;
			if(temp.pointingNode == -1 && temp.ID > this.ID && temp.isMarried == false){
				if(temp.ID > t){
					t = temp.ID;
				}
			}
		}
		return t;
	}
	
	private MSNode getNodeByID(int id){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			MSNode temp = (MSNode)it.next().endNode;
			if(temp.ID == id){
				return temp;
			}
		}
		return null;
	}
	public boolean PRmarried(){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			MSNode temp = (MSNode) it.next().endNode;
			if(temp.pointingNode == this.ID && this.pointingNode == temp.ID){
				return true;
			}
		}
		return false;
	}
	/**
	 *  Four mutual exclusive guarded rules
	 * @return
	 */
	
	
	/**
	 * Check if the value of isMarried is equal to predicate
	 * @return True if this rule can be activate, false otherwise
	 */
	public boolean updateRules(){
		boolean flag = this.PRmarried();
		if(this.isMarried != flag){
			this.isMarried = flag;
			return true;
		}
		return false;
	}
	/**
	 * Check if this process can be married with 1 of his neighbors
	 * @return True if this rule can be activate, false otherwise
	 */
	public boolean marriageRule(){
		int j = -1;
		if((this.isMarried == this.PRmarried()) && this.pointingNode == -1 && (j=this.checkNeighborForMarriage())!=-1){
			this.pointingNode = j;
			this.setColor(Color.GREEN);
			return true;
		}
		return false;
	}
	/**
	 * Check if this process can seduce with 1 of his neighbors
	 * @return True if this rule can be activate, false otherwise
	 */
	public boolean seductionRule(){
		int j;
		if((this.isMarried == this.PRmarried()) && this.pointingNode == -1 && this.checkNeighborForMarriage()==-1 
				&& (j = this.checkMaxNeighborForMarriageWithNullPreference())!=-1){
			this.pointingNode = j;
			myLog.logln("Sed rule..now NodeID: "+this.ID+" pointing to "+j);
			return true;
		}
		return false;
	}
	/**
	 * Check if this process must abandon 
	 * @return True if this rule can be activate, false otherwise
	 */
	public boolean abandonmentRule(){
		MSNode temp = this.getNodeByID(this.pointingNode);
		if(this.isMarried == this.PRmarried() && this.pointingNode!=-1 
				&& temp.pointingNode!=this.ID 
				&& (temp.isMarried || temp.ID <= this.ID)){
			this.pointingNode = -1;
			this.setColor(Color.BLACK);
			return true;
		}
		return false;
	}
	@Override
	public void handleMessages(Inbox inbox) {
		// TODO Auto-generated method stub
		if(inbox.hasNext()) {
			MSMessage m = (MSMessage) inbox.next();
			myLog.logln("Node: "+this.ID+" has received this message "+m.getPayload());
		}
		
	}

	@Override
	public void preStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		this.isMarried = false;
		this.pointingNode = -1 ;
		this.isEligibile = true;
	}

	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// TODO Auto-generated method stub
		MSTimer timer = new MSTimer(this,Tools.getRandomNumberGenerator().nextDouble());
		Iterator<Edge> it = this.outgoingConnections.iterator();
		myLog.logln("Init method for nodeID "+this.ID+" that have outgoingconnections "+this.outgoingConnections.size());
		while(it.hasNext()){
			MSMessage m = new MSMessage(MSMessage.MARRIAGE_MEX);
			myLog.logln("Sending Marry mex to nodeID: "+it.next().endNode.ID);
			this.send(m, it.next().endNode);
		}
		timer.startRelative(Tools.getRandomNumberGenerator().nextDouble(), this);
		
	}
	
	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		// TODO Auto-generated method stub
		super.drawNodeAsSquareWithText(g, pt, highlight, Integer.toString(this.ID), 16, Color.WHITE);
	}
	@NodePopupMethod(menuText="Send_Message")
	public void myPopupMethod() {
		myLog.logln("Node:: "+this.ID+" Pressed popup menu");
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		myLog.logln("Pop-upMenu method for nodeID "+this.ID+" that have outgoingconnections "+this.outgoingConnections.size());
		while(it.hasNext()){
			MSMessage m = new MSMessage("hello from "+this.ID);
			this.send(m, it.next().endNode);
			
		}
	}
	
	@NodePopupMethod(menuText="Change_Color")
	public void myPopupMethod2(){
		this.setColor(Color.GREEN);
	}

	

}
