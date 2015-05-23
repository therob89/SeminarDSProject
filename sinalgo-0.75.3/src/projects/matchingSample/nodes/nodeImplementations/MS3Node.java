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

public class MS3Node extends MSNode {

	//Logging myLog = Logging.getLogger("logAlgorithm3.txt");
	boolean want_to_act;
    public final static Color defaultColor = Color.PINK;


    @Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		this.setColor(defaultColor);
	}


	public boolean isWant_to_act() {
		return want_to_act;
	}

    protected Integer getAvailableNeighbor(){
        MS3Node n;
        for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
            n = (MS3Node)it.next().endNode;
            if(n.getPointingNode() == -1){
                return n.ID;
            }
        }
        return -1;
    }
	public boolean allowedToAct(){
		for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
			MS3Node n = (MS3Node)it.next().endNode;
			if(n.isWant_to_act() && this.isWant_to_act() && n.ID > this.ID){
				return false;
			}
		}
		return true;
	}
	protected MS3Node wantToEngage(){
		Integer pointingNeighbor;
		if(this.getPointingNode() == -1 && (pointingNeighbor=checkNeighborForMarriage())!=-1){
            return (MS3Node)Tools.getNodeByID(pointingNeighbor);
		}
		return null;
	}
	
	protected MS3Node wantToPropose(){
		Integer availableNeighbor;
		if(this.getPointingNode() == -1 && this.checkNeighborForMarriage()==-1
				&& (availableNeighbor=this.getAvailableNeighbor())!=-1){
			return (MS3Node)Tools.getNodeByID(availableNeighbor);
		}
		return null;
	}
	protected boolean wantTODesengage(){
        MS3Node n;
		if(this.getPointingNode()!=-1 && (n=(MS3Node)Tools.getNodeByID(this.getPointingNode())).getPointingNode()!=this.ID && n.getPointingNode()!=-1){
			return true;
		}
		return false;
		
	}
	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		if(this.isAllowed_To_Move){
            MS3Node node;
			boolean canAct = this.allowedToAct();
			if((this.want_to_act = (node=this.wantToEngage())!=null && canAct)){
                checkIfWeAreInFault();
                //myLog.logln("*** MARRIAGE for Node: "+this.ID +" with node "+node.ID+"***");
                this.pointingNode = node.ID;
                this.isMarried = true;
                this.setColorToEdgeAndNodes(Color.GREEN, node);
                return;
            }
            if((this.want_to_act = (node = this.wantToPropose())!=null && canAct)){
                checkIfWeAreInFault();
                //myLog.logln("** Proposing Node: "+this.ID +" to node "+node.ID+"**");
                this.pointingNode = node.ID;
                return;
            }
            if((this.want_to_act = this.wantTODesengage() && canAct)){
                checkIfWeAreInFault();
                //myLog.logln("* Node: "+this.ID +" does DESENGAGE!!!!!");
                this.pointingNode = -1;
                if(this.isMarried){
                    this.isMarried = false;
                }
                return;
            }
            boolean p1,p2,p3;
            p1 = wantToEngage()!=null;
            p2 = wantToPropose()!=null;
            p3 = wantTODesengage();
            if(this.want_to_act != (p1 || p2 || p3)){
                //myLog.logln("* Node: "+this.ID +" does an UPDATE!!!!!");
                this.want_to_act = p1 || p2 || p3;
                return;
            }
			this.end_flag = true;
			//myLog.logln("\t \t NodeID:"+this.ID+"cannot perform any action...so END FLAG = TRUE");
		}else{
			//myLog.logln("Node: "+this.ID+"Cannot execute...Try to next round!!..having wantToAct ="+this.want_to_act);
		}
	}
	
	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		// TODO Auto-generated method stub
		super.drawNodeAsDiskWithText(g, pt, highlight, Integer.toString(this.ID), 14, Color.WHITE);
	}

}
