package projects.matchingSample.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
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

public class MSNode extends Node {

	//public static boolean isSending = true;
	public boolean isMarried;
	boolean findTheOptimum;
	public Integer pointingNode; 
	boolean isEligibile;
	int interval;
	boolean isAllowed_To_Move;
	public boolean end_flag;
	Edge married_egde;
	double threshold_probability = 0.5;
	Logging myLog = Logging.getLogger("logAlgorithm1.txt");
	
	/*
	 * 	VARIABLES FOR OPTIMAL CASE
	 * 
	 */
	Integer p_v,alfa_v,beta_v;
	boolean rematch_v;
	
	
	public void setFindTheOptimum(boolean findTheOptimum) {
		this.findTheOptimum = findTheOptimum;
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
		this.findTheOptimum = false;
	}     
	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		myLog.logln("Node: "+this.ID+" POST_STEP ");
		if(this.isAllowed_To_Move && !this.findTheOptimum){
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
		}
		if(this.isAllowed_To_Move && this.findTheOptimum){
			/*
			 * SINGLE NODE
			 */
			
		}else{
			myLog.logln("Node: "+this.ID+"Cannot execute...Try to next round!!");
		}

		
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

	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkRequirements() throws WrongConfigurationException {
		// TODO Auto-generated method stub
		
	}

	private boolean singleNodeRoutine(){
		Integer n;
		Set<Integer> s = new HashSet<Integer>();
		for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
			MSNode neighbor = (MSNode) it.next().endNode;
			if(neighbor.p_v == this.ID){
				s.add(neighbor.ID);
			}
		}
		MSNode pv_node = (MSNode) Tools.getNodeByID(this.p_v);
		if(this.p_v == -1 && !s.isEmpty() 
				|| (!s.contains(this.p_v) && this.p_v!=-1)
				|| (this.p_v != null && pv_node.p_v != this.ID)){
			this.p_v = Collections.min(s);
			return true;
		}
		return false;
	}
	
	/*
	 *  MATCHED NODE ROUTINE
	 */
	
	
	private boolean matchFirst(){
		Integer askFirst = this.askFirst(this.ID);
		MSNode n = (MSNode) Tools.getNodeByID(this.p_v);
		if(askFirst != null && (this.p_v != askFirst || this.rematch_v != (n.p_v == this.ID))){
			this.p_v = askFirst;
			this.rematch_v = (n.p_v == this.ID);
			return true;
		}
		return false;
	}
	
	
	private boolean matchSecond(){
		Integer askSecond = this.askSecond(this.ID);
		if(askSecond != null && this.rematch_v && this.p_v != askSecond){
			this.p_v = askSecond;
			return true;
		}
		return false;

	}
	
	private boolean resetMatch(){
		
		Integer askFirst = this.askFirst(this.ID);
		Integer askSecond = this.askSecond(this.ID);
		if((askFirst == null && askSecond == null)  && this.p_v != null && this.rematch_v != false){
			this.p_v = null;
			this.rematch_v = false;
		}
		
		return false;
	}
	
	
	
	
	
	
	private Pair<Integer,Integer> bestRematch(){
		Integer a,b;
		Set<Integer> s = this.getNeighborPointingMeForRematch();
		a = Collections.min(s);
		s.remove(a);
		b = Collections.min(s);
		return new Pair<Integer,Integer>(a,b);
	}
	
	private Integer askFirst(Integer v){
		MSNode v_node = (MSNode) Tools.getNodeByID(v);
		Set<Integer> s = new HashSet<Integer>();
		MSNode neighbor = (MSNode)Tools.getNodeByID(v_node.pointingNode);
		s.add(alfa_v);
		s.add(beta_v);
		s.add(neighbor.alfa_v);
		s.add(neighbor.beta_v);
		if(v_node.alfa_v != -1 && neighbor.alfa_v!=-1 && s.size()>=2){
			if(v_node.alfa_v < neighbor.alfa_v 
					|| (v_node.alfa_v == neighbor.alfa_v && v_node.beta_v == -1)
					|| (v_node.alfa_v == neighbor.alfa_v && 
					neighbor.beta_v != -1 && v_node.ID < neighbor.ID)){
				
				return v_node.alfa_v;
			}
		}
		return null;
	}
	
	private Integer askSecond(Integer v){
		MSNode v_node = (MSNode)Tools.getNodeByID(v);
		if(this.askFirst(v_node.pointingNode)!=null){
			Set<Integer> s = new HashSet<Integer>();
			s.add(v_node.alfa_v);
			s.add(v_node.beta_v);
			MSNode mv = (MSNode)Tools.getNodeByID(v_node.pointingNode);
			s.remove(mv.alfa_v);
			return Collections.min(s);
		}
		return null;
	}
	
	
	
	
	private Set<Integer> getNeighborPointingMeForRematch(){
		Set<Integer> s = new HashSet<Integer>();
		for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
			MSNode x = (MSNode) it.next().endNode;
			if(x.p_v == -1 ||x.p_v == this.ID){
				s.add(x.ID);
			}
		}
		return s;
	}
	private int checkNeighborForReMarriage(){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			MSNode temp = (MSNode) it.next().endNode;
			if(temp.p_v == this.ID){
				return temp.ID;
			}
		}
		return -1;
	}
	
	private Set<Integer> getSingleNeighbor(){
		Set<Integer> s = new HashSet<Integer>();
		for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
			MSNode x = (MSNode) it.next().endNode;
			if(!x.isMarried){
				s.add(x.ID);
			}
		}
		return s;
	}
	private Set<Integer> getMarriedNeighbor(){
		Set<Integer> s = new HashSet<Integer>();
		for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
			MSNode x = (MSNode) it.next().endNode;
			if(x.isMarried){
				s.add(x.ID);
			}
		}
		return s;
	}
}
