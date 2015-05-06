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
	
	public Integer getP_v(){
		return this.p_v;
	}
	public void setFindTheOptimum(boolean findTheOptimum) {
		this.findTheOptimum = findTheOptimum;
		myLog.logln("Node: "+this.ID+"------------------------ Now find the optimum is: "+ this.findTheOptimum+ "And Married Predicate is: "+this.isMarried);
		if(Tools.getRandomNumberGenerator().nextDouble()>=0.5){
			this.alfa_v = null;
		}else{
			do{
				this.alfa_v = Tools.getRandomNumberGenerator().nextInt(Tools.getNodeList().size()+1);
			}while(this.alfa_v==this.ID);
		}
		if(Tools.getRandomNumberGenerator().nextDouble()>=0.5){
			this.beta_v = null;
		}else{
			do{
				this.beta_v = Tools.getRandomNumberGenerator().nextInt(Tools.getNodeList().size()+1);
			}while(this.beta_v==this.ID);
		}
		if(Tools.getRandomNumberGenerator().nextDouble()>=0.5){
			this.p_v = null;
		}else{
			List<Integer> s_1 = new ArrayList<Integer>();
			for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
				MSNode n = (MSNode) it.next().endNode;
				if(n.ID != this.pointingNode){
					s_1.add(it.next().endNode.ID);
				}
			}
			if(s_1.isEmpty()){
				this.p_v = null;
				return;
			}
			this.p_v = s_1.get(Tools.getRandomNumberGenerator().nextInt(s_1.size()));
		}
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
		e = this.getEdgeStartAndEnd(i, j);
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
		int j = -1;
		if((this.isMarried == this.PRmarried()) && this.pointingNode == -1 && (j=this.checkNeighborForMarriage())!=-1){
			this.pointingNode = j;
			myLog.logln("Node:ID "+this.ID +" married with "+ j);
			/*
			this.setColor(Color.GREEN);
			this.getNodeByID(j).setColor(Color.GREEN);
			Edge e = this.getEdgeByEndNode(j);
			if(e!=null){
				e.defaultColor = Color.GREEN;
			}
			Tools.repaintGUI();*/
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
		this.alfa_v = this.beta_v = this.p_v = null;
		this.rematch_v = false;
	}
	
	private String printTheStateOfNode(){
		return "<"+this.alfa_v+", "+this.beta_v+", "+this.p_v+", "+this.rematch_v+" >";
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
			if(this.findTheOptimum){
				if(!this.isMarried){
					myLog.logln("Single Node: "+this.ID+" current state at the beginning of the move:"+this.printTheStateOfNode());
					if(this.singleNodeRoutine()){
						return;
					}
					myLog.logln("Single Node: "+this.ID+" current state at the end of the move:"+this.printTheStateOfNode());
					myLog.logln("---------------------------------------------------------------------");
				}
				else{
					myLog.logln("Married Node: "+this.ID+" current state at the beginning of the move:"+this.printTheStateOfNode());
					this.updateRoutine();
					this.matchFirst();
					this.matchSecond();
					this.resetMatch();
					myLog.logln("Married Node: "+this.ID+" current state at the beginning of the move:"+this.printTheStateOfNode());
					myLog.logln("---------------------------------------------------------------------");

				}		
			}
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
	
	/***********************************************************************************************************
	 *  
	 *  
	 *  									SINGLE NODE ROUTINE	 
	 *  
	 *  
	 *************************************************************************************************************/
	private boolean singleNodeRoutine(){
		myLog.logln(this.ID+": START SINGLE NODE ROUTINE");
		Set<Integer> s = new HashSet<Integer>();
		for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
			MSNode n = (MSNode) it.next().endNode;
			if(n.p_v == this.ID){
				s.add(n.ID);
			}
		}
		myLog.logln(this.ID+": START SINGLE NODE ROUTINE...set of neighbors pointing me is: "+ s.toString());
		if((this.p_v == null && s.size()!=0) 
				|| (this.checkIfBelongToSetWithNull(this.getMarriedNeighbor(), this.p_v))
				|| (this.p_v!=null && ((MSNode)Tools.getNodeByID(this.p_v)).p_v!=this.ID))
		{
			this.p_v = Collections.min(s);
			myLog.logln(this.ID+": START SINGLE NODE ROUTINE...taking the lowest from neighbors = "+this.p_v);
		}
		myLog.logln(this.ID+": END SINGLE NODE ROUTINE");
		return false;
	}
	
	/***********************************************************************************************************
	 *  
	 *  
	 *  									MATCH NODE ROUTINES	 
	 *  
	 *  
	 *************************************************************************************************************/
	private boolean checkIfBelongToSetWithNull(Set<Integer> set, Integer value){
		boolean flag = false;
		if(set!=null){
			set.add(null);
			flag = set.contains(value);
			set.remove(null);
		}else{
			// Set == null
			return value == null;
		}
		return flag;
		
	}
	private boolean updateRoutine(){
		myLog.logln("MATCHED NODE: "+this.ID+": UPDATE ROUTINE.....START");
		Pair<Integer,Integer> bestRematch = this.bestRematch();
		if((this.alfa_v != null && this.beta_v !=null && this.alfa_v>this.beta_v)
				|| (!this.checkIfBelongToSetWithNull(this.getSingleNeighbor(), this.alfa_v) || !this.checkIfBelongToSetWithNull(this.getSingleNeighbor(), this.alfa_v))
				|| (this.alfa_v==this.beta_v && this.alfa_v!=null)
				|| (!this.checkIfBelongToSetWithNull(this.getSingleNeighbor(),this.p_v))
				|| ((this.alfa_v!=bestRematch.getKey() || this.beta_v!=bestRematch.getValue()) && (this.p_v == null || (((MSNode)Tools.getNodeByID(this.p_v)).p_v!=this.ID || (((MSNode)Tools.getNodeByID(this.p_v)).p_v!=null)))))
		{
			myLog.logln("MATCHED NODE: "+this.ID+": ** UPDATING ALL THE VALUES ** ");
			this.alfa_v = bestRematch.getKey();
			this.beta_v = bestRematch.getValue();
			this.p_v = null;
			this.rematch_v = false;
			return true;
		}
		myLog.logln("MATCHED NODE: "+this.ID+": UPDATE ROUTINE.....END FALSE");
		return false;
	}
	
	private boolean matchFirst(){
		myLog.logln("MATCHED NODE: "+this.ID+": MATCH FIRST.....START");
		Integer askFirst = this.askFirst(this.ID);
		Integer p_p_v;
		if(((MSNode)Tools.getNodeByID(this.p_v)).p_v!=null){
			p_p_v = ((MSNode)Tools.getNodeByID(this.p_v)).p_v;
		}
		else{
			p_p_v = null;
		}
		if((askFirst!=null) && (this.p_v != askFirst || this.rematch_v !=(p_p_v == this.ID))){
			myLog.logln("MATCHED NODE: "+this.ID+": ** INSIDE THE MATCH FIRST ** ");
			this.p_v = askFirst;
			this.rematch_v = (p_p_v == this.ID);
			return true;
		}
		myLog.logln("MATCHED NODE: "+this.ID+": MATCH FIRST.....END FALSE");
		return false;
	}
	
	
	private boolean matchSecond(){
		myLog.logln("MATCHED NODE: "+this.ID+": MATCH SECOND.....START");
		Integer askSecond = this.askSecond(this.ID);
		if(askSecond!=null 
				&& (this.isMarried && ((MSNode)Tools.getNodeByID(this.pointingNode)).rematch_v)
				&& (this.p_v!=askSecond))
		{
			this.p_v = askSecond;
			myLog.logln("MATCHED NODE: "+this.ID+": ** INSIDE THE MATCH SECOND ** ");
			return true;
		}
		return false;
	}
	
	private boolean resetMatch(){
		myLog.logln("MATCHED NODE: "+this.ID+": REset MATCH.....START");
		return false;
	}
	
	
	
	
	/***********************************************************************************************************
	 *  
	 *  
	 *  									FUNCTIONS ROUTINE	 
	 *  
	 *  
	 *************************************************************************************************************/
	
	private Pair<Integer,Integer> bestRematch(){
		Integer a,b;
		Set<Integer> singleNeighbor = this.getSingleNeighbor();
		Set<Integer> singleNeighborAvailable = new HashSet<Integer>(singleNeighbor);
		for(Integer n_ID : singleNeighbor){
			MSNode node = (MSNode) Tools.getNodeByID(n_ID);
			if(node.p_v == null || node.p_v == this.ID){
				singleNeighborAvailable.add(node.ID);
			}
		}
		myLog.logln(this.ID +": BEST REMATCH ROUTINE, size of neighbor available are: "+ singleNeighborAvailable.size());
		if(singleNeighborAvailable.isEmpty()){
			return new Pair<Integer,Integer>(null,null);
		}
		a = Collections.min(singleNeighborAvailable);
		singleNeighborAvailable.remove(a);
		if(singleNeighborAvailable.isEmpty()){
			myLog.logln(this.ID +": BEST REMATCH ROUTINE, only alfa is != null and is = "+a);
			return new Pair<Integer,Integer>(a,null);
		}
		b = Collections.min(singleNeighborAvailable);
		myLog.logln(this.ID +": BEST REMATCH ROUTINE, returning alfa and beta::::::::: alfa:"+a+" beta:"+b);
		return new Pair<Integer,Integer>(a,b);
	}
	
	private Integer askFirst(Integer v){
		myLog.logln(this.ID+" ASK FIRST ROUTINE");
		if(v==null){
			return null;
		}
		Set<Integer> s = new HashSet<Integer>();
		MSNode v_node = (MSNode) Tools.getNodeByID(v);
		if(v_node.pointingNode == -1){
			return null;
		}
		MSNode neighbor = (MSNode)Tools.getNodeByID(v_node.pointingNode);
		if(this.alfa_v!=null){
			s.add(alfa_v);
		}
		if(this.beta_v!=null){
			s.add(this.beta_v);
		}
		if(neighbor.alfa_v!=null){
			s.add(neighbor.alfa_v);
		}
		if(neighbor.beta_v!=null){
			s.add(neighbor.beta_v);
		}
		myLog.logln(this.ID+" ASK FIRST ROUTINE has Unique values size = "+s.size());
		if(v_node.alfa_v != null &&  neighbor.alfa_v!=null && s.size()>=2){
			if(v_node.alfa_v < neighbor.alfa_v 
					|| (v_node.alfa_v == neighbor.alfa_v && v_node.beta_v == null)
					|| (v_node.alfa_v == neighbor.alfa_v && neighbor.beta_v != null && v_node.ID < v_node.pointingNode)){
				myLog.logln(this.ID+" ASK FIRST ROUTINE ***************** RETURNING ALFA_V");
				return v_node.alfa_v;
			}
		}
		myLog.logln(this.ID+" ASK FIRST ROUTINE return null");
		return null;
	}
	
	private Integer askSecond(Integer v){
		myLog.logln(this.ID+" ASK SECOND ROUTINE");
		if(v==null || this.pointingNode==-1){
			return null;
		}
		MSNode v_node = (MSNode)Tools.getNodeByID(v);
		if(this.askFirst(v_node.pointingNode)!=null){
			Set<Integer> s = new HashSet<Integer>();
			s.add(v_node.alfa_v);
			s.add(v_node.beta_v);
			MSNode mv = (MSNode)Tools.getNodeByID(v_node.pointingNode);
			s.remove(mv.alfa_v);
			if(s.isEmpty()){
				myLog.logln(this.ID+" ASK SECOND ROUTINE have lowest between alfa_v,beta_v,mv.alfa_v == null");
				return null;
			}
			return Collections.min(s);
		}
		return null;
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
