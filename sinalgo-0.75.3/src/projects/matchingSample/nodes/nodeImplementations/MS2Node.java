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

public class MS2Node extends MSNode {

	Logging myLog = Logging.getLogger("logAlgorithm2.txt");
	public final Color defaultColor = Color.BLUE;


    @Override
	public void init() {
		// TODO Auto-generated method stub
		super.init();
		this.setColor(Color.BLUE);
	}


	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		if(this.isAllowed_To_Move){
			if(this.matchingRule()){
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
            myLog.logln("\t \t NodeID:"+this.ID+"cannot perform any action...so END FLAG = TRUE");
        }else{
			myLog.logln("Node: "+this.ID+"Cannot execute...Try to next round!!");
		}

	}

	
	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		// TODO Auto-generated method stub
		super.drawNodeAsDiskWithText(g, pt, highlight, Integer.toString(this.ID), 14, Color.WHITE);
		//this.setColor(Color.BLUE);
		//Tools.repaintGUI();
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

	/**
	 *  three mutual exclusive guarded rules
	 * @return
	 */
	public boolean matchingRule(){
		MS2Node j;
		if(this.pointingNode == -1 && (j=(MS2Node)Tools.getNodeByID(this.checkNeighborForMarriage()))!=null){
            myLog.logln("*** MARRIAGE for Node: "+this.ID +" with node "+j.ID+"***");
            checkIfWeAreInFault();
            this.pointingNode = j.ID;
            this.isMarried = true;
			setColorToEdgeAndNodes(Color.GREEN,j);
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
		if(this.pointingNode == -1 && checkNeighborForMarriage()==-1 && (n = this.getMarriableNode())!=null){
            checkIfWeAreInFault();
            myLog.logln("** Seduction rule for Node: "+this.ID +" to node "+n.ID+"**");
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
		MS2Node temp;
		if(this.pointingNode!=-1 && (temp=((MS2Node)Tools.getNodeByID(this.pointingNode))).getPointingNode()!=this.ID && temp.getPointingNode()!=-1){
            myLog.logln("* Node: "+this.ID +" does abandonment Rule!!!!!");
            checkIfWeAreInFault();
            this.pointingNode = -1;
			return true;
		}
		return false;
	}
	
	

}
