package projects.matchingSample.nodes.timers;


import projects.matchingSample.nodes.messages.MSMessage;
import projects.matchingSample.nodes.nodeImplementations.MSNode;
import sinalgo.nodes.timers.Timer;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

public class MSTimer extends Timer {

	MSMessage m;
	MSNode sender;
	double interval;
	Logging myLog = Logging.getLogger("myLog.txt");
	
	public MSTimer(MSMessage mex, MSNode sender,double interval){
		this.m = mex;
		this.sender = sender;
		this.interval = interval;
	}
	
	public MSTimer(MSNode sender, double interval){
		this.sender = sender;
		this.interval = interval;
	}
	/**
	 * This method must implement the daemon
	 * 
	 * 
	 * 
	 * 
	 */
	@Override
	public void fire() {
		// TODO Auto-generated method stub
		if(this.sender!=null){
			myLog.logln("Timer elapsed for nodeID: "+this.sender.ID);
			if(this.sender.updateRules()){
				myLog.logln("NodeID:"+this.sender.ID+"does update!!");
			}
			if(this.sender.marriageRule()){
				myLog.logln("NodeID:"+this.sender.ID+"does marriage!!");
			}
			if(this.sender.seductionRule()){
				myLog.logln("NodeID:"+this.sender.ID+"does seduction rule!!");
			}
			if(this.sender.abandonmentRule()){
				myLog.logln("NodeID:"+this.sender.ID+"does abandonment rule!!");
			}
		}
		this.startRelative(Tools.getRandomNumberGenerator().nextDouble(), sender);

	}
}
