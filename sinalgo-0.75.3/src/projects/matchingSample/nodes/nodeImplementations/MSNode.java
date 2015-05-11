package projects.matchingSample.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javafx.util.Pair;
import projects.matchingSample.nodes.messages.MSMessage;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Connections;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

import javax.swing.*;

public class MSNode extends Node {

	public boolean isMarried;
	public Integer pointingNode;
	boolean isEligibile;
	boolean isAllowed_To_Move;
	public boolean end_flag;
	Edge married_egde;
	double threshold_probability = 0.5;
	Logging myLog = Logging.getLogger("logAlgorithm1.txt");


	public void setFaultState(){
        myLog.logln("Node: " + this.ID + " Faulting the state");
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
            Node n = it.next().endNode;
            list.add(n.ID);
        }
        this.pointingNode = list.get(Tools.getRandomNumberGenerator().nextInt(list.size()));
        this.setColor(Color.RED);
    }
	
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
	public Edge getEdgeStartAndEnd(Node start, Node end){
		for(Iterator<Edge>it = start.outgoingConnections.iterator();it.hasNext();){
			Edge e = it.next();
			if(e.endNode.ID == end.ID){
				return e;
			}
		}
		return null;
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
	private List<Integer> getListOfUnMarriedWithGreaterID(){
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
	private void setColorToEdgeAndNodes(Color color, Node i, Node j){
		i.setColor(color);
		j.setColor(color);
		Edge e = this.getEdgeStartAndEnd(i, j);
		if(e.defaultColor != color){
			e.defaultColor = color;

		}
		e = this.getEdgeStartAndEnd(j, i);
		if(e.defaultColor != color){
			e.defaultColor = color;
		}
		Tools.repaintGUI();

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
		int j;
		if((this.isMarried == this.PRmarried()) && this.pointingNode == -1 && (j=this.checkNeighborForMarriage())!=-1){
			this.pointingNode = j;
			myLog.logln("Node:ID "+this.ID +" married with "+ j);
			Edge e = this.getEdgeByEndNode(j);
			this.married_egde = e;
			this.setColorToEdgeAndNodes(Color.GREEN, this, Tools.getNodeByID(j));
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
				&& (list=this.getListOfUnMarriedWithGreaterID())!=null){
			this.pointingNode = this.getMaxFromList(list);
			myLog.logln("Sed rule..now NodeID: " + this.ID + " pointing to " + this.pointingNode);
			return true;
		}
		return false;
	}
	/**
	 * Check if this process must abandon 
	 * @return True if this rule can be activate, false otherwise
	 */
	public boolean abandonmentRule(){
		MSNode temp;
        myLog.logln("State for nodeID="+this.ID+" is p = "+this.pointingNode);
		if(this.isMarried == this.PRmarried() && this.pointingNode!=-1 
				&& (temp= this.getNodeByID(this.pointingNode)).pointingNode!=this.ID
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
	public void postStep() {
		// TODO Auto-generated method stub
		myLog.logln("Node: "+this.ID+" POST_STEP ");
		myLog.logln("---------------------------------------------------------------------");
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
			myLog.logln("***WARN: *** Node: "+this.ID+"Scheduler doesn't allow the execution...try next round!!");
			myLog.logln("---------------------------------------------------------------------");
		}
	}
/*
	public void listOfMoves(){
		if(!this.isMarried){
			myLog.logln("Single Node: "+this.ID+" current state at the beginning of the moves:"+this.printTheStateOfNode());
			this.singleNodeRoutine();
			myLog.logln("Single Node: "+this.ID+" current state at the end of the moves:"+this.printTheStateOfNode());
			myLog.logln("---------------------------------------------------------------------");
		}
		else{
			myLog.logln("Married Node: "+this.ID+" current state at the beginning of the moves:"+this.printTheStateOfNode());
			this.updateRoutine();
			this.matchFirst();
			this.matchSecond();
			this.resetMatch();
			myLog.logln("Married Node: "+this.ID+" current state at the end of the moves:"+this.printTheStateOfNode());
			myLog.logln("---------------------------------------------------------------------");

		}

	}
	@Override
	public void postStep() {

		if(this.findTheOptimum && this.isAllowed_To_Move) {
			this.listOfMoves();
		}else{
			myLog.logln("***WARN: *** Node: "+this.ID+"Scheduler doesn't allow the execution...try next round!!");
			myLog.logln("---------------------------------------------------------------------");
		}

	}*/

	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		// TODO Auto-generated method stub
		super.drawNodeAsDiskWithText(g, pt, highlight, Integer.toString(this.ID), 14, Color.WHITE);
	}
	@NodePopupMethod(menuText="Send_Message")
	public void myPopupMethod() {
		myLog.logln("Node:: " + this.ID + " Pressed popup menu");
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
		myLog.logln("Pop-upMenu method for nodeID " + this.ID + " that have outgoingconnections " + this.outgoingConnections.size());
		while(it.hasNext()){
			it.next().defaultColor = Color.GREEN;
		}
	}

	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// TODO Auto-generated method stub
		
	}

}
