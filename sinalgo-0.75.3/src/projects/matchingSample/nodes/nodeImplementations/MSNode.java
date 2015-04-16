package projects.matchingSample.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Random;

import projects.matchingSample.nodes.messages.MSMessage;
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
	boolean PRmarried(){
		Connections conn = this.outgoingConnections;
		Iterator<Edge> it = conn.iterator();
		while(it.hasNext()){
			MSNode temp = (MSNode) it.next().endNode;
			if(temp.pointingNode == this.ID){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	boolean updateRules(){
		boolean flag = this.PRmarried();
		if(this.isMarried != flag){
			this.isMarried = flag;
			return true;
		}
		return false;
	}
	
	boolean marriageRule(){
		int j = -1;
		if((this.isMarried == this.PRmarried()) && this.pointingNode == -1 && (j=this.checkNeighborForMarriage())!=-1){
			this.pointingNode = j;
			return true;
		}
		return false;
	}
	
	void seductionRule(){
		int j;
		if((this.isMarried == this.PRmarried()) && this.pointingNode == -1 && this.checkNeighborForMarriage()==-1 && (j = this.checkMaxNeighborForMarriageWithNullPreference())!=-1){
			this.pointingNode = j;
		}
	}
	void abandonmentRule(){
		MSNode temp = this.getNodeByID(this.pointingNode);
		if(this.isMarried == this.PRmarried() && this.pointingNode!=-1 
				&& temp.pointingNode!=this.ID 
				&& (temp.isMarried || temp.ID <= this.ID)){
			this.pointingNode = -1;
		}
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
		this.isEligibile = false;
		
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
		while(it.hasNext()){
			MSMessage m = new MSMessage("hello from "+this.ID);
			this.send(m, it.next().endNode);
			
		}
	}
	
	@NodePopupMethod(menuText="Change_Color")
	public void myPopupMethod2(){
		this.setColor(Color.RED);
	}

	

}
