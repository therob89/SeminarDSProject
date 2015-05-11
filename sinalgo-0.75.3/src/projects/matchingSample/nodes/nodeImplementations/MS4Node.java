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
    Logging myLog = Logging.getLogger("logAlgorithm4.txt");
    boolean findTheOptimum;
    boolean hasFinished;


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
    }


    @Override
    public void postStep() {
        super.postStep();
        if(this.findTheOptimum && this.isAllowed_To_Move){
            if(!this.isMarried()){
                myLog.logln("------------SINGLE NODE: "+this.ID+"---------------------");
                singleNodeRoutine();
                myLog.logln("---------------------------------------------------------");
            }else{
                myLog.logln("------------MARRIED NODE: "+this.ID+"---------------------");
                updateRoutine();
                matchFirst();
                matchSecond();
                resetMatch();
                myLog.logln("---------------------------------------------------------");
            }
        }else{
            myLog.logln("-----------------");
            myLog.logln("WARN:: Node ID"+this.ID+" cannot run in this round..try next time");
            myLog.logln("-----------------");

        }
    }

    private String printTheStateOfNode(){
        if(this.isMarried) {
            return "<" + this.alfa_v + ", " + this.beta_v + ", " + this.p_v + ", " + this.rematch_v + ", married with:" + this.pointingNode + " >";
        }else{
            return "<" + this.p_v + " >";
        }
    }

    public void setFindTheOptimum() {
        this.findTheOptimum = true;
        myLog.logln("Node: "+this.ID+"------------------------ Now find the optimum is: "+ this.findTheOptimum+ "And Married Predicate is: "+this.isMarried);
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
                this.p_v = -1;
                if(Tools.getRandomNumberGenerator().nextDouble()>=0.5){
                    do{
                        this.alfa_v= Tools.getRandomNode().ID;
                    }while(this.alfa_v == this.ID);
                    do{
                        this.beta_v= Tools.getRandomNode().ID;
                    }while(this.beta_v == this.ID);

                }else{
                    this.alfa_v= -1;
                    this.beta_v = -1;
                }

            }else{
                this.alfa_v = list.get(Tools.getRandomNumberGenerator().nextInt(list.size()));
                //this.p_v = -1;
                this.p_v = this.alfa_v;
                if(Tools.getRandomNumberGenerator().nextDouble()>=0.5){
                    this.beta_v = -1;
                }else{
                    do{
                        this.beta_v= Tools.getRandomNode().ID;
                    }while(this.beta_v == this.ID);
                }
            }
        }
        myLog.logln("Node: "+this.ID+"------------------------ Start state is = "+this.printTheStateOfNode());
    }


    /***********************************************************************************************************
     *
     *
     *  									SINGLE NODE ROUTINE
     *
     *
     *************************************************************************************************************/
    private boolean singleNodeRoutine(){
        myLog.logln("START SINGLE NODE ROUTINE");
        Set<Integer> s = new HashSet<Integer>();
        for(Iterator<Edge> it = this.outgoingConnections.iterator();it.hasNext();){
            MS4Node n = (MS4Node) it.next().endNode;
            if(n.p_v == this.ID){
                s.add(n.ID);
            }
        }
        myLog.logln("Single node: "+this.ID+" has this set of neighbors pointing to his"+ s.toString()+" size = "+s.size());
        if((this.p_v == -1 && s.size()!=0)
                || (!this.checkIfBelongToSetWithNull(this.getMarriedNeighbor(), this.p_v))
                || (this.p_v!=-1 && ((MS4Node)Tools.getNodeByID(this.p_v)).p_v!=this.ID))
        {
            if(s.size()==0){
                this.p_v = -1;
            }else{
                this.p_v = Collections.min(s);
            }
            myLog.logln("Single node: "+this.ID+" taking the lowest from neighbors = "+this.p_v);
            return true;
        }
        myLog.logln("**WARN** Single node: "+this.ID+" single node routine is not activated");
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
        myLog.logln("MATCHED NODE: "+this.ID+": Starting Update Routine");
        Pair<Integer,Integer> bestRematch = this.bestRematch();
        if((this.alfa_v!=-1 && this.beta_v!=-1 && this.alfa_v>this.beta_v)
                || (!this.checkIfBelongToSetWithNull(this.getSingleNeighbor(), this.alfa_v) && !this.checkIfBelongToSetWithNull(this.getSingleNeighbor(), this.beta_v))
                || (this.alfa_v==this.beta_v && this.alfa_v!=-1)
                || (!this.checkIfBelongToSetWithNull(this.getSingleNeighbor(),this.p_v))
                || ((this.alfa_v!=bestRematch.getKey() || this.beta_v!=bestRematch.getValue()) && (this.p_v == -1 || (((MS4Node)Tools.getNodeByID(this.p_v)).p_v!=this.ID && (((MS4Node)Tools.getNodeByID(this.p_v)).p_v!=-1)))))
        {
            myLog.logln("MATCHED NODE: "+this.ID+": ***** needed to update its state *****");
            this.alfa_v = bestRematch.getKey();
            this.beta_v = bestRematch.getValue();
            this.p_v = -1;
            this.rematch_v = false;
            return true;
        }
        myLog.logln("**WARN** MATCHED NODE: "+this.ID+": UPDATE ROUTINE is not activated");
        return false;
    }

    private boolean matchFirst(){
        myLog.logln("MATCHED NODE: "+this.ID+": Starting Match First");
        Integer askFirst = this.askFirst(this.ID);
        if((askFirst!=-1) && (this.p_v != askFirst || (this.rematch_v !=(((MS4Node)Tools.getNodeByID(this.p_v)).p_v == this.ID)))){
            myLog.logln("MATCHED NODE: "+this.ID+": ***** Match First is correctly executed ***** ");
            this.p_v = askFirst;
            this.rematch_v = (((MS4Node)Tools.getNodeByID(this.p_v)).p_v == this.ID);
            return true;
        }
        myLog.logln("**WARN** MATCHED NODE: "+this.ID+": Match first routine is not activated");
        return false;
    }


    private boolean matchSecond(){
        myLog.logln("MATCHED NODE: "+this.ID+": Starting Match Second");
        Integer askSecond = this.askSecond(this.ID);
        if(askSecond!=-1
                && (((MS4Node)Tools.getNodeByID(this.pointingNode)).rematch_v)
                && (this.p_v!=askSecond))
        {
            this.p_v = askSecond;
            myLog.logln("MATCHED NODE: "+this.ID+": ***** Match Second is correctly executed ***** ");
            return true;
        }
        myLog.logln("**WARN** MATCHED NODE: "+this.ID+": Match second routine is not activated");
        return false;
    }

    private boolean resetMatch(){
        myLog.logln("MATCHED NODE: "+this.ID+": Starting Reset Matching");
        Integer askFirst = this.askFirst(this.ID);
        Integer askSecond = this.askSecond(this.ID);
        if(((askFirst== -1 && askSecond == -1)) && (this.p_v!=-1 && this.rematch_v!=false)){
            myLog.logln("MATCHED NODE: "+this.ID+": ***** MUST DO A RESET MATCH ******");
            this.p_v = -1;
            this.rematch_v = false;
            return true;
        }
        myLog.logln("**WARN** MATCHED NODE: "+this.ID+": Reset Matching routine is not activated");
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
        Set<Integer> singleNeighbor = this.getSingleNeighbor();
        HashSet<Integer> singleNeighborAvailable = new HashSet<Integer>();
        if(singleNeighbor.size()==0){
            myLog.logln("\t\t\t MATCHED NODE: "+this.ID+": BEST REMATCH Routine -> No Single Neighbor available = "+singleNeighbor.toString());
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
                myLog.logln("\t\t\tMATCHED NODE: "+this.ID+": BEST REMATCH :: Neighbor available = "+singleNeighborAvailable.toString());
                return new Pair<Integer,Integer>(-1,-1);
            case 1:
                myLog.logln("\t\t\tMATCHED NODE: "+this.ID+": BEST REMATCH :: Neighbor available = "+singleNeighborAvailable.toString());
                return new Pair<Integer,Integer>(Collections.min(singleNeighborAvailable),-1);
            default:
                myLog.logln("\t\t\tMATCHED NODE: "+this.ID+": BEST REMATCH :: Neighbor available = "+singleNeighborAvailable.toString());
                Integer alfa = Collections.min(singleNeighborAvailable);
                singleNeighborAvailable.remove(alfa);
                return new Pair<Integer,Integer>(alfa,Collections.min(singleNeighborAvailable));
        }
    }

    private Integer askFirst(Integer v){
        MS4Node marriedWith = (MS4Node)Tools.getNodeByID(this.pointingNode);
        HashSet<Integer> s = new HashSet<Integer>();
        s.addAll(Arrays.asList(this.alfa_v,this.beta_v,marriedWith.alfa_v,marriedWith.beta_v));
        if(this.alfa_v != -1 && marriedWith.alfa_v != -1 && s.size()>=2){
            if((this.alfa_v<marriedWith.alfa_v)
                    || (this.alfa_v == marriedWith.alfa_v && this.beta_v == -1)
                    || (this.alfa_v == marriedWith.alfa_v && marriedWith.beta_v != -1 && this.ID < this.pointingNode))
            {
                myLog.logln("\t \t MATCHED NODE "+this.ID+" *****ASK FIRST("+v+") is correctly executed *****  ---> Returning ---> "+this.alfa_v);
                return this.alfa_v;
            }
        }
        myLog.logln("\t \t ** WARN ** "+this.ID+" ASK FIRST("+v+") is not activated!!!!");
        return -1;


    }

    private Integer askSecond(Integer v){
        Integer askFirst_married = ((MS4Node)Tools.getNodeByID(this.pointingNode)).askFirst(this.pointingNode);
        if(askFirst_married!=-1){
            Set<Integer> s = new HashSet<Integer>(Arrays.asList(this.alfa_v,this.beta_v));
            s.remove(((MS4Node)Tools.getNodeByID(this.pointingNode)).alfa_v);
            s.remove(-1);
            if(s.isEmpty()){
                myLog.logln("\t \t Matched node :"+this.ID+"have the lowest betweem alfa_v,beta_v / alfa_m_v == 0");
                return -1;
            }
            myLog.logln("\t \t MATCHED NODE "+this.ID+" *****ASK SECOND("+v+") is correctly executed *****  ---> Returning ---> "+this.alfa_v);
            return Collections.min(s);
        }
        myLog.logln("\t \t ** WARN ** "+this.ID+" ASK Second("+v+") is not activated!!!!");
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
            myLog.logln("Error with the popup menu");
        }

    }

    @Override
    public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
        // TODO Auto-generated method stub
        super.drawNodeAsDiskWithText(g, pt, highlight, Integer.toString(this.ID), 14, Color.BLACK);
    }

}
