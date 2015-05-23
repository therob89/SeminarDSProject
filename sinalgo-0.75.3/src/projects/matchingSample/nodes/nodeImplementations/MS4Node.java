package projects.matchingSample.nodes.nodeImplementations;

import javafx.util.Pair;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Connections;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by robertopalamaro on 09/05/15.
 */
public class MS4Node extends MSNode{

    Integer p_v,alfa_v,beta_v;
    boolean rematch_v;
    //Logging //myLog = Logging.getLogger("logAlgorithm4.txt");
    public final static Color defaultColor = Color.YELLOW;
    boolean findTheOptimum;
    boolean hasFinished;

    public boolean isSecondMatchDone() {
        return secondMatchDone;
    }

    boolean secondMatchDone;


    public boolean isHasFinished() {
        return hasFinished;
    }

    public Integer getP_v() {
        return p_v;
    }


    @Override
    public void preStep() {
        super.preStep();
    }

    @Override
    public void init() {
        super.init();
        this.setColor(Color.YELLOW);
        this.hasFinished = false;
        this.secondMatchDone = false;
    }


    @Override
    public void postStep() {
        if(!this.findTheOptimum) {
            super.postStep();
        }
        if(this.findTheOptimum && this.isAllowed_To_Move){
            if(!this.isMarried()){
                //myLog.logln("------------SINGLE NODE: " + this.ID + " BEGINNING Turn state = " + this.printTheStateOfNode() + "---------------------");
                if(singleNodeRoutine()){
                    //myLog.logln("------------SINGLE NODE: "+ this.ID + " END TURN state : "+this.printTheStateOfNode()+"----------------------------");
                    return;
                }
                end_flag = true;
                //myLog.logln("*************SINGLE NODE: "+ this.ID + " ***** END FLAG = TRUE ****** state: "+this.printTheStateOfNode());

            }else{
                //myLog.logln("------------MARRIED NODE: " + this.ID + "BEGINNING TURN state = " + this.printTheStateOfNode() + "--------------------");
                if(updateRoutine()){
                    //myLog.logln("\t MATCHED NODE: "+this.ID+": UPDATE ROUTINE DONE!!");
                    //myLog.logln("------------MARRIED NODE: " + this.ID + "END TURN state = " + this.printTheStateOfNode() + "---------------------");
                    return;
                }
                if(matchFirst()){
                    //myLog.logln("\t MATCHED NODE: "+this.ID+": MATCH FIRST DONE!");
                    //myLog.logln("------------MARRIED NODE: " + this.ID + "END TURN state = " + this.printTheStateOfNode() + "---------------------");
                    return;
                }
                if(matchSecond()){
                    //myLog.logln("\t MATCHED NODE: "+this.ID+": MATCH SECOND DONE!");
                    //myLog.logln("------------MARRIED NODE: " + this.ID + "END TURN state = " + this.printTheStateOfNode() + "---------------------");
                    return;
                }
                if(resetMatch()){
                    //myLog.logln("\t MATCHED NODE: "+this.ID+": RESET MATCH DONE!");
                    //myLog.logln("------------MARRIED NODE: " + this.ID + "END TURN state = " + this.printTheStateOfNode() + "---------------------");
                    return;
                }
                end_flag = true;
                //myLog.logln("*************MARRIED NODE: "+ this.ID + " ***** END FLAG = TRUE ****** state+"+this.printTheStateOfNode());
            }
        }else{
            //myLog.logln("-----------------");
            //myLog.logln("\t ******* WARN:: Node ID"+this.ID+" cannot run in this round..try next time ******* ");
            //myLog.logln("-----------------");

        }
    }

    public String printTheStateOfNode(){
        if(this.isMarried) {
            return "<" + this.alfa_v + ", " + this.beta_v + ", " + this.p_v + ", " + this.rematch_v + ", married with:" + this.pointingNode + ">";
        }else{
            return "<" + this.p_v + " married:"+this.isMarried()+">";
        }
    }

    public void setFindTheOptimum() {
        this.findTheOptimum = true;
        //myLog.logln("Node: "+this.ID+"------------------------ Now find the optimum is: "+ this.findTheOptimum+ "And Married Predicate is: "+this.isMarried);
        if(!this.isMarried){
            this.p_v = -1;
            this.alfa_v = -1;
            this.beta_v = -1;
        }
        else{
            List<Integer> list = new ArrayList<Integer>();
            for(Iterator<Edge> it=this.outgoingConnections.iterator();it.hasNext();){
                MS4Node n = (MS4Node) it.next().endNode;
                if(!n.isMarried){
                    list.add(n.ID);
                }
            }
            if(list.isEmpty()){
                /*
                if(Tools.getRandomNumberGenerator().nextDouble()>=0.5){
                    do{
                        this.alfa_v= Tools.getRandomNode().ID;
                    }while(this.alfa_v == this.ID);
                    do{
                        this.beta_v= Tools.getRandomNode().ID;
                    }while(this.beta_v == this.ID);

                }else{
                    this.alfa_v= -1;
                    this.beta_v = -1;*/
                this.p_v = this.alfa_v = this.beta_v = -1;

            }else{
                this.alfa_v = list.get(Tools.getRandomNumberGenerator().nextInt(list.size()));
                this.p_v = -1;
                list.remove(this.alfa_v);
                if(list.isEmpty()){
                    this.beta_v = -1;
                }else{
                    this.beta_v = list.get(Tools.getRandomNumberGenerator().nextInt(list.size()));
                }
            }
        }
        //myLog.logln("Node: "+this.ID+"------------------------ Start state is = "+this.printTheStateOfNode());
    }


    /***********************************************************************************************************
     *
     *
     *  									SINGLE NODE ROUTINE ajfaijs
     *
     *
     *************************************************************************************************************/
    private boolean singleNodeRoutine(){
        Set<Integer> s = new HashSet<Integer>();
        for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
            MS4Node n = (MS4Node) it.next().endNode;
            if(n.p_v == this.ID){
                s.add(n.ID);
            }
        }
        ////myLog.logln("Single node: "+this.ID+" has this set of neighbors pointing to his"+ s.toString()+" size = "+s.size());
        if((this.p_v == -1 && s.size()!=0)
                || (!this.checkIfBelongToSetWithNull(this.getMarriedNeighbor(), this.p_v))
                || (this.p_v!=-1 && ((MS4Node)Tools.getNodeByID(this.p_v)).p_v!=this.ID))
        {
            if(s.size()==0){
                this.p_v = -1;
            }else{
                this.p_v = Collections.min(s);
            }
            //myLog.logln(" \t Single node: "+this.ID+" taking the lowest from neighbors = "+this.p_v);
            return true;
        }
        //myLog.logln(" \t \t **WARN ** Single node routine It can not be activated");
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
        if(value == -1){
            return true;
        }
        if(!set.isEmpty()){
            return set.contains(value);
        }
        return false;
    }
    private boolean updateRoutine(){
        //myLog.logln(" MATCHED NODE: "+this.ID+": UPDATE ROUTINE START");
        Pair<Integer,Integer> bestRematch = this.bestRematch();
        if((this.alfa_v!=-1 && this.beta_v!=-1 && this.alfa_v > this.beta_v)
                || (this.alfa_v==-1 && this.beta_v!=-1)
                || (!this.checkIfBelongToSetWithNull(this.getSingleNeighbor(), this.alfa_v) || !this.checkIfBelongToSetWithNull(this.getSingleNeighbor(), this.beta_v))
                || (this.alfa_v==this.beta_v && this.alfa_v!=-1)
                || (!this.checkIfBelongToSetWithNull(this.getSingleNeighbor(),this.p_v))
                || (!(this.alfa_v==bestRematch.getKey() && this.beta_v==bestRematch.getValue()) && (this.p_v == -1 || (((MS4Node)Tools.getNodeByID(this.p_v)).p_v!=this.ID && (((MS4Node)Tools.getNodeByID(this.p_v)).p_v!=-1)))))
        {
            this.alfa_v = bestRematch.getKey();
            this.beta_v = bestRematch.getValue();
            this.p_v = -1;
            this.rematch_v = false;
            this.secondMatchDone = false;
            return true;
        }
        //myLog.logln("**WARN** MATCHED NODE: "+this.ID+": UPDATE ROUTINE can not be activated");
        return false;
    }

    private boolean matchFirst(){
        //myLog.logln("MATCHED NODE: "+this.ID+": MATCH ROUTINE START");
        Integer askFirst = this.askFirst(this.ID);
        if((askFirst!=-1) && (this.p_v != askFirst || (this.rematch_v !=(((MS4Node)Tools.getNodeByID(this.p_v)).p_v == this.ID)))){
            this.p_v = askFirst;
            this.rematch_v = (((MS4Node)Tools.getNodeByID(this.p_v)).p_v == this.ID);
            return true;
        }
        //myLog.logln("**WARN** MATCHED NODE: "+this.ID+": Match first routine can not be activated *******");
        return false;
    }


    private boolean matchSecond(){
        //myLog.logln("MATCHED NODE: "+this.ID+": MATCH SECOND START");
        Integer askSecond = this.askSecond(this.ID);
        if(askSecond!=-1
                && (((MS4Node)Tools.getNodeByID(this.pointingNode)).rematch_v)
                && (this.p_v!=askSecond))
        {
            this.p_v = askSecond;
            secondMatchDone = true;
            return true;
        }
        //myLog.logln("**WARN** MATCHED NODE: "+this.ID+": Match second routine can not be activated");
        return false;
    }

    private boolean resetMatch(){
        //myLog.logln("MATCHED NODE: "+this.ID+": RESET MATCH START");
        Integer askFirst = this.askFirst(this.ID);
        Integer askSecond = this.askSecond(this.ID);
        if(((askFirst== -1 && askSecond == -1)) && (!(this.p_v==-1 && this.rematch_v==false))){
            this.p_v = -1;
            this.rematch_v = false;
            this.secondMatchDone = false;
            return true;
        }
        //myLog.logln("**WARN** MATCHED NODE: "+this.ID+": Reset Matching routine can not be  activated");
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
        //myLog.logln("\t\t\t NODE: " + this.ID + ": BEST REMATCH Routine start \t");
        Set<Integer> singleNeighbor = this.getSingleNeighbor();
        HashSet<Integer> singleNeighborAvailable = new HashSet<Integer>();
        if(singleNeighbor.size()==0){
            //myLog.log("\t\t\t NODE: " + this.ID + ": BEST REMATCH Routine, no Single Neighbor available ->" + singleNeighbor.toString() + "\t");
            return new Pair<Integer,Integer>(-1,-1);
        }
        for(Integer s_ID:singleNeighbor){
            MS4Node node = (MS4Node)Tools.getNodeByID(s_ID);
            if(node.p_v == -1 || node.p_v == this.ID){
                singleNeighborAvailable.add(s_ID);
            }
        }

        switch(singleNeighborAvailable.size()){
            case 0:
                //myLog.logln("\t\t\t NODE: " + this.ID + ": BEST REMATCH :: Neighbor available size:0 = " + singleNeighborAvailable.toString() + "\t");
                return new Pair<Integer,Integer>(-1, -1);
            case 1:
                //myLog.logln("\t\t\t NODE: " + this.ID + ": BEST REMATCH :: Neighbor available size:1 = " + singleNeighborAvailable.toString() + "\t");
                return new Pair<Integer,Integer>(Collections.min(singleNeighborAvailable), -1);
            default:
                //myLog.logln("\t\t\t NODE: " + this.ID + ": BEST REMATCH :: Neighbor available size:2 = " + singleNeighborAvailable.toString() + "\t");
                Integer alfa = Collections.min(singleNeighborAvailable);
                singleNeighborAvailable.remove(alfa);
                return new Pair<Integer,Integer>(alfa,Collections.min(singleNeighborAvailable));
        }
    }

    private Integer askFirst(Integer v){
        //myLog.logln("\t\t\t NODE: " + this.ID + ": Ask first Routine start \t");
        MS4Node marriedWith = (MS4Node)Tools.getNodeByID(this.pointingNode);
        HashSet<Integer> s = new HashSet<Integer>();
        s.addAll(Arrays.asList(this.alfa_v,this.beta_v,marriedWith.alfa_v,marriedWith.beta_v));
        if(this.alfa_v != -1 && marriedWith.alfa_v != -1 && s.size()>=2){
            if((this.alfa_v<marriedWith.alfa_v)
                    || (this.alfa_v == marriedWith.alfa_v && this.beta_v == -1)
                    || (this.alfa_v == marriedWith.alfa_v && marriedWith.beta_v != -1 && this.ID < this.pointingNode))
            {
                //myLog.logln("\t\t\t NODE " + this.ID + " *****ASK FIRST(" + v + ") is correctly executed *****  ---> Returning ---> " + this.alfa_v);
                return this.alfa_v;
            }
        }
        //myLog.logln("** WARN ** " + this.ID + " ASK FIRST(" + v + ") can not be activated!!!!");
        return -1;


    }

    private Integer askSecond(Integer v){
        //myLog.logln("\t\t\t NODE: " + this.ID + ": Ask Second Routine start \t");
        Integer askFirst_married = ((MS4Node)Tools.getNodeByID(this.pointingNode)).askFirst(this.pointingNode);
        if(askFirst_married!=-1){
            Set<Integer> s = new HashSet<Integer>(Arrays.asList(this.alfa_v,this.beta_v));
            s.remove(((MS4Node)Tools.getNodeByID(this.pointingNode)).alfa_v);
            s.remove(-1);
            if(s.isEmpty()){
                //myLog.logln("\t\t\t Node:" + this.ID + "have the lowest between alfa_v,beta_v / alfa_m_v == 0 \t");
                return -1;
            }
            //myLog.logln("\t\t\t NODE " + this.ID + " *****ASK SECOND(" + v + ") is correctly executed *****  ---> Returning ---> " + this.alfa_v+"\t");
            return Collections.min(s);
        }
        //myLog.logln("** WARN ** "+this.ID+" ASK Second("+v+") can not be activated!!!! \n");
        return -1;
    }

    private Set<Integer> getSingleNeighbor(){
        Set<Integer> s = new HashSet<Integer>();
        for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
            MS4Node x = (MS4Node) it.next().endNode;
            if(!x.isMarried){
                s.add(x.ID);
            }
        }
        return s;
    }
    private Set<Integer> getMarriedNeighbor(){
        Set<Integer> s = new HashSet<Integer>();
        for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
            MS4Node x = (MS4Node) it.next().endNode;
            if(x.isMarried){
                s.add(x.ID);
            }
        }
        return s;
    }
    @NodePopupMethod(menuText="Force Pointing")
    public void forcePointing(){
        String answer = JOptionPane.showInputDialog(null, "Insert the node to match");
        if(answer.equals("-1")){
            this.p_v = -1;
        }else {
            this.p_v = Integer.valueOf(answer);
        }
    }

    @NodePopupMethod(menuText="Force Marriage")
    public void forceMarriage(){
        this.isMarried = true;
        String answer = JOptionPane.showInputDialog(null, "Insert the node to match");
        try{

            this.pointingNode = Integer.valueOf(answer);
            this.setColor(Color.BLUE);
            this.getEdgeByEndNode(this.pointingNode).defaultColor = Color.blue;
            Tools.getNodeByID(this.pointingNode).setColor(Color.BLUE);
            ((MS4Node)Tools.getNodeByID(this.pointingNode)).isMarried = true;
            ((MS4Node)Tools.getNodeByID(this.pointingNode)).pointingNode = this.ID;
            ((MS4Node)Tools.getNodeByID(this.pointingNode)).getEdgeByEndNode(this.ID).defaultColor = Color.blue;
            Tools.repaintGUI();
        }catch (NumberFormatException e){
            //myLog.logln("Error with the popup menu");
        }

    }

    @Override
    public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
        // TODO Auto-generated method stub
        super.drawNodeAsDiskWithText(g, pt, highlight, Integer.toString(this.ID), 14, Color.BLACK);
    }

    @NodePopupMethod(menuText="Optim")
    public void forceOptimum(){
        this.setFindTheOptimum();
    }

}
