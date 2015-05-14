package projects.matchingSample.nodes.nodeImplementations;

import sinalgo.tools.Tools;

import java.awt.*;

/**
 * Created by robertopalamaro on 14/05/15.
 */
public class MS5Node extends MS3Node {

    private final Color defaultColor = Color.CYAN;

    @Override
    public void init() {
        super.init();
        this.setColor(defaultColor);
    }

    @Override
    public void postStep() {
        if(this.isAllowed_To_Move){
            MS3Node node;
            if((node=this.wantToEngage())!=null && Tools.getRandomNumberGenerator().nextDouble()>0.5){
                checkIfWeAreInFault();
                myLog.logln("*** MARRIAGE for Node: "+this.ID +" with node "+node.ID+"***");
                this.pointingNode = node.ID;
                this.isMarried = true;
                this.setColorToEdgeAndNodes(Color.GREEN, node);
                return;
            }
            if((node = this.wantToPropose())!=null && Tools.getRandomNumberGenerator().nextDouble()>0.5){
                checkIfWeAreInFault();
                myLog.logln("** Proposing Node: "+this.ID +" to node "+node.ID+"**");
                this.pointingNode = node.ID;
                return;
            }
            if(this.wantTODesengage() && Tools.getRandomNumberGenerator().nextDouble()>0.5){
                checkIfWeAreInFault();
                myLog.logln("* Node: "+this.ID +" does DESENGAGE!!!!!");
                this.pointingNode = -1;
                if(this.isMarried){
                    this.isMarried = false;
                }
                return;
            }
            this.end_flag = true;
            myLog.logln("\t \t NodeID:"+this.ID+"cannot perform any action...so END FLAG = TRUE");

        }else{
            myLog.logln("Node: "+this.ID+"Cannot execute...Try to next round!!..having wantToAct ="+this.want_to_act);
        }

    }
}
