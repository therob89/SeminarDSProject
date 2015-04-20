package projects.matchingSample.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import projects.matchingSample.nodes.messages.MSMessage;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Connections;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

public class MSNode extends Node {

	public static boolean isSending = true;
	public boolean isMarried;
	public Integer pointingNode; 
	boolean isEligibile;
	int interval;
	boolean isAllowed_To_Move;
	public boolean end_flag;
	Edge married_egde;
	double threshold_probability = 0.5;
	
	Logging myLog = Logging.getLogger("logAlgorithm1.txt");
	
	public boolean getEndFlag(){
		return this.end_flag;
	}
	public void setThresholdProbability(double k){
		myLog.logln("Node: "+this.ID+" Now threshold probability is: "+ k);
		this.threshold_probability = k;
	}
	public void clearState(){
		this.isMarried = false;
		this.pointingNode = -1;
		if(this.getColor()!=Color.BLACK){
			this.setColor(Color.BLACK);
		}
		this.end_flag = false;
		if(this.married_egde!=null){
			this.married_egde.defaultColor = Color.BLACK;
		}
		Tools.repaintGUI();
	}
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
	private int getMaxFromList(List<Integer> list){
		Collections.sort(list);
		return list.get(list.size()-1);
	}
	private List<Integer> getListOfUnMarriedWithGretherID(){
		List<Integer>list = new ArrayList<Integer>();
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			MSNode temp = (MSNode) it.next().endNode;
			if(temp.pointingNode == -1 && temp.ID>this.ID && !temp.isMarried){
				list.add(temp.ID);
			}
		}
		if(!list.isEmpty()){
			return list;
		}
		return null;
	}

	
	public Edge getEdgeByEndNode(Integer nodeID){
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
			myLog.logln("Node:ID "+this.ID +" married with "+ j);
			this.setColor(Color.GREEN);
			this.getNodeByID(j).setColor(Color.GREEN);
			Edge e = this.getEdgeByEndNode(j);
			if(e!=null){
				e.defaultColor = Color.GREEN;
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
		List<Integer>list;
		if((this.isMarried == this.PRmarried()) && this.pointingNode == -1 //&& this.checkNeighborForMarriage()==-1 
				//&& (j = this.checkMaxNeighborForMarriageWithNullPreference())!=-1){
				&& (list=this.getListOfUnMarriedWithGretherID())!=null){
			this.pointingNode = this.getMaxFromList(list);
			myLog.logln("Sed rule..now NodeID: "+this.ID+" pointing to "+this.pointingNode);
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
			Tools.repaintGUI();
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
		this.isMarried = false;
		this.pointingNode = -1 ;
		this.isEligibile = true;
		this.end_flag = false;
		this.married_egde = null;
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
			if(this.updateRules()){
				myLog.logln("NodeID:"+this.ID+"does update!!");
				return;
			}
			if(this.marriageRule()){
				myLog.logln("NodeID:"+this.ID+"does marriage!!");
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
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		myLog.logln("Pop-upMenu method for nodeID "+this.ID+" that have outgoingconnections "+this.outgoingConnections.size());
		while(it.hasNext()){
			it.next().defaultColor = Color.GREEN;
		}
	}

	

}
