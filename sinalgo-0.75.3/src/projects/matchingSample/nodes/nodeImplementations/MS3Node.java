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

public class MS3Node extends Node {

	Integer pointer_Node;
	boolean end_flag;
	Edge married_edge;
	Logging myLog = Logging.getLogger("logAlgorithm3.txt");
	double threshold_probability = 0.5;
	boolean isAllowed_To_Move;
	boolean want_to_act;

	public Integer getPointer_Node() {
		return pointer_Node;
	}
	public boolean getEndFlag(){
		return this.end_flag;
	}
	/*
	private boolean checkIfCanAct(){
		Iterator<Edge> it = outgoingConnections.iterator();
		while(it.hasNext()){
			MS3Node n = (MS3Node) it.next().endNode;
			if(n.getWantToAct() == this.want_to_act == true && n.ID >= this.ID){
				return false;
			}
		}
		return true;
	}*/
	private MS3Node getNeighborPointingMe(){
		Iterator<Edge> it = outgoingConnections.iterator();
		while(it.hasNext()){
			MS3Node n = (MS3Node) it.next().endNode;
			if(n.getPointer_Node() == this.ID){
				return n; 
			}
		}
		return null;
	}
	private MS3Node getAvailableNeighbor(){
		Iterator<Edge> it = outgoingConnections.iterator();
		while(it.hasNext()){
			MS3Node n = (MS3Node) it.next().endNode;
			if(n.getPointer_Node() == -1){
				return n; 
			}
		}
		return null;
	}
	private MS3Node getNodeByID(Integer id){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			MS3Node temp = (MS3Node)it.next().endNode;
			if(temp.ID == id){
				return temp;
			}
		}
		return null;
	}
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

	
	/*
	public boolean updateAction(){
		boolean t = this.want_to_act;
		if(t != (this.wantToEngage()!=null || this.wantToPropose()!=null || this.wantToDesengage())){
			this.want_to_act = t;
			return true;
		}
		return false;
	}*/
	@Override
	public void init() {
		// TODO Auto-generated method stub
		this.pointer_Node = -1;	
		this.setColor(Color.PINK);
		this.end_flag = false;
		this.want_to_act = false;
	}

	@Override
	public void neighborhoodChange() {
		// TODO Auto-generated method stub

	}
	
	public boolean allowedToAct(){
		for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
			MS3Node n = (MS3Node)it.next().endNode;
			if(this.want_to_act == n.want_to_act == true && this.ID < n.ID){
				return false;
			}
		}
		return true;
	}
	private MS3Node wantToEngage(){
		MS3Node temp;
		if(this.pointer_Node == -1 && (temp=this.getNeighborPointingMe())!=null){
			return temp;
		}
		return null;
	}
	
	private MS3Node wantToPropose(){
		MS3Node temp;
		if(this.pointer_Node == -1 && this.getNeighborPointingMe()==null 
				&& (temp=this.getAvailableNeighbor())!=null){
			return temp;
		}
		return null;
	}
	private boolean wantTODesengage(){
		MS3Node temp;
		temp = this.getNodeByID(this.pointer_Node);
		if(this.pointer_Node!=-1 && temp.pointer_Node!=this.ID && temp.pointer_Node != -1){
			return true;
		}
		return false;
		
	}
	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		if(this.isAllowed_To_Move){
			boolean canAct = this.allowedToAct();
			MS3Node temp = this.wantToEngage();
			this.want_to_act = (temp!=null && canAct);
			if(this.want_to_act){
				myLog.logln("NodeID:"+this.ID+"does matching!!");
				Edge e;
				this.pointer_Node = temp.ID;
				myLog.logln("*** Now NodeID:"+this.ID+"is married with "+temp.ID);
				this.setColor(Color.GREEN);
				temp.setColor(Color.GREEN);
				e = this.getEdgeByEndNode(temp.ID);
				if(e!=null){
					e.defaultColor = Color.GREEN;
				}
				this.married_edge = e;
				Tools.repaintGUI();
				return;
			}
			temp = this.wantToPropose();
			this.want_to_act=(temp!=null && canAct);
			if(this.want_to_act){
				myLog.logln("NodeID:"+this.ID+"does seduction!!");
				this.pointer_Node = temp.ID;
				return;
			}
			this.want_to_act = (this.wantTODesengage() && canAct);
			if(this.want_to_act){
				myLog.logln("NodeID:"+this.ID+"does desengage!!");
				return;
			}
			boolean t = (this.wantToEngage()!=null || this.wantToPropose()!=null || this.wantTODesengage());
			if(this.want_to_act != t){
				this.want_to_act = t;
				return;
			}
			this.end_flag = true;
			myLog.logln("NodeID:"+this.ID+"cannot perform any action!!");
		}else{
			myLog.logln("Node: "+this.ID+"Cannot execute...Try to next round!!..having wantToAct ="+this.want_to_act);
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

}
