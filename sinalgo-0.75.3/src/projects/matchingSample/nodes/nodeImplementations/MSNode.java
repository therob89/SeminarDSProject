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


	public boolean marriedPredicate;
    public boolean isMarried;
    public final Color defaultColor = Color.BLACK;
	public Integer pointingNode;
	boolean isEligibile;
	boolean isAllowed_To_Move;
	public boolean end_flag;
	Edge married_egde;
	double threshold_probability = 0.5;

	Logging myLog = Logging.getLogger("logAlgorithm1.txt");

	public boolean isMarriedPredicate() {
		return marriedPredicate;
	}

    public Edge getMarried_egde() {
        return married_egde;
    }

	public Integer getPointingNode() {
		return pointingNode;
	}

	public boolean isEnd_flag() {
		return end_flag;
	}

    public boolean isMarried() {
        return isMarried;
    }

    public void setFaultState(){
        myLog.logln("Node: " + this.ID + " Faulting the state");
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
            Node n = it.next().endNode;
            list.add(n.ID);
        }
        this.pointingNode = list.get(Tools.getRandomNumberGenerator().nextInt(list.size()));
		if(Tools.getRandomNumberGenerator().nextDouble()>0.5){
            this.marriedPredicate = false;
        }else{
            this.marriedPredicate = true;
        }
        this.setColor(Color.RED);
    }

	public void setThresholdProbability(double k){
		myLog.logln("Node: "+this.ID+" Now threshold probability is: "+ k);
		this.threshold_probability = k;
	}
	public void clearState(){
		this.isMarried = false;
        this.marriedPredicate = false;
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
	protected Edge getEdgeStartAndEnd(Node start, Node end){
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
	protected int checkNeighborForMarriage(){
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

	private List<Integer> getListOfUnMarriedWithGreaterID(){
        List<Integer> list = new ArrayList<Integer>();
		for(Iterator<Edge> it = outgoingConnections.iterator();it.hasNext();){
			MSNode temp = (MSNode) it.next().endNode;
			if(temp.pointingNode == -1 && temp.ID>this.ID && !temp.isMarriedPredicate()){
				list.add(temp.ID);
			}
		}
		if(!list.isEmpty()){
			return list;
		}
		return null;
	}

	public void checkIfWeAreInFault(){
        if(this.getColor()==Color.RED){
            this.setColor(defaultColor);
            Tools.repaintGUI();
        }
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
	public void setColorToEdgeAndNodes(Color color, Node j){
		this.setColor(color);
		j.setColor(color);
		Edge e = this.getEdgeStartAndEnd(this, j);
		if(e.defaultColor != color){
			e.defaultColor = color;

		}
        this.married_egde = e;
		e = this.getEdgeStartAndEnd(j, this);
		if(e.defaultColor != color){
			e.defaultColor = color;
		}
		((MSNode)j).isMarried = true;
        ((MSNode)j).married_egde = e;
		Tools.repaintGUI();

	}
	public boolean checkMarriedPredicate(){
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
        boolean flag = this.checkMarriedPredicate();
		if(this.marriedPredicate != flag){
            myLog.logln("*NodeID:"+this.ID+"does update rule!! *");
            checkIfWeAreInFault();
            this.marriedPredicate = flag;
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
		if((this.marriedPredicate == this.checkMarriedPredicate()) && this.pointingNode == -1 && (j=this.checkNeighborForMarriage())!=-1){
            myLog.logln("**** Node:ID "+this.ID +" married with "+ j+"****");
            this.pointingNode = j;
            this.isMarried = true;
            this.setColorToEdgeAndNodes(Color.GREEN, Tools.getNodeByID(j));
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
		if((this.marriedPredicate == this.checkMarriedPredicate())
                && this.pointingNode == -1
                && this.checkNeighborForMarriage() == -1
				&& (list=this.getListOfUnMarriedWithGreaterID())!=null){
            myLog.logln("**Seduction rule..now NodeID: " + this.ID + " pointing to " + this.pointingNode+"**");
            this.pointingNode = Collections.max(list);
            checkIfWeAreInFault();
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
		if(this.marriedPredicate == this.checkMarriedPredicate() && this.pointingNode!=-1
				&& (temp = this.getNodeByID(this.pointingNode)).pointingNode!=this.ID
				&& (temp.isMarriedPredicate() || temp.ID <= this.ID)){
            myLog.logln("**Abandonment rule for NodeID: " + this.ID + "**");
            this.pointingNode = -1;
            checkIfWeAreInFault();
            if(this.married_egde != null){
				married_egde = null;
			}
            this.isMarried = false;
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
		this.isMarried = this.marriedPredicate = false;
		this.pointingNode = -1 ;
		this.isEligibile = true;
		this.end_flag = false;
		this.married_egde = null;

	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		myLog.logln("---------------------------------------------------------------------");
		if(this.isAllowed_To_Move){
			myLog.logln("Node: "+this.ID+"Scheduler decides that i can run");
			if(this.updateRules()){
                myLog.logln("---------------------------------------------------------------------");
                return;
			}
			if(this.marriageRule()){
				myLog.logln("NodeID:"+this.ID+"does marriage!!");
                myLog.logln("---------------------------------------------------------------------");
                return;
			}
			if(this.seductionRule()){
				myLog.logln("NodeID:"+this.ID+"does seduction rule!!");
                myLog.logln("---------------------------------------------------------------------");
                return;
			}
			if(this.abandonmentRule()){
				myLog.logln("NodeID:"+this.ID+"does abandonment rule!!");
                myLog.logln("---------------------------------------------------------------------");
                return;
			}
			this.end_flag = true;
		}else{
			myLog.logln("***WARN: *** Node: "+this.ID+"Scheduler doesn't allow the execution...try next round!!");
			myLog.logln("---------------------------------------------------------------------");
		}
	}

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
