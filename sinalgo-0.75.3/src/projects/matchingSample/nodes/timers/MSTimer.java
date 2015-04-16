package projects.matchingSample.nodes.timers;

import projects.matchingSample.nodes.messages.MSMessage;
import projects.matchingSample.nodes.nodeImplementations.MSNode;
import sinalgo.nodes.timers.Timer;
import sinalgo.tools.logging.Logging;

public class MSTimer extends Timer {

	MSMessage m;
	MSNode sender;
	int interval;
	Logging myLog = Logging.getLogger("myLog.txt");
	
	public MSTimer(MSMessage mex, MSNode sender,int interval){
		this.m = mex;
		this.sender = sender;
		this.interval = interval;
	}
	@Override
	public void fire() {
		// TODO Auto-generated method stub
		if(this.sender!=null){
			
		}

	}

}
