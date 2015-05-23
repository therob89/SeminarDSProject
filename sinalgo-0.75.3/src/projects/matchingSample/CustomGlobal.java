/*
 Copyright (c) 2007, Distributed Computing Group (DCG)
                    ETH Zurich
                    Switzerland
                    dcg.ethz.ch

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

 - Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the
   distribution.

 - Neither the name 'Sinalgo' nor the names of its contributors may be
   used to endorse or promote products derived from this software
   without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package projects.matchingSample;

import java.awt.Color;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.ToLongBiFunction;

import javafx.util.Pair;

import javax.swing.JOptionPane;

import projects.defaultProject.models.distributionModels.Random;
import projects.defaultProject.nodes.messages.StringMessage;
import projects.matchingSample.nodes.edges.MEdge;
import projects.matchingSample.nodes.nodeImplementations.MS2Node;
import projects.matchingSample.nodes.nodeImplementations.MS3Node;
import projects.matchingSample.nodes.nodeImplementations.MS4Node;
import projects.matchingSample.nodes.nodeImplementations.MSNode;
import sinalgo.configuration.Configuration;
import sinalgo.models.DistributionModel;
import sinalgo.models.Model;
import sinalgo.nodes.Node;
import sinalgo.nodes.Position;
import sinalgo.nodes.edges.Edge;
import sinalgo.runtime.*;
import sinalgo.runtime.Runtime;
import sinalgo.tools.Tools;
import sinalgo.tools.logging.Logging;

/**
 * This class holds customized global state and methods for the framework. 
 * The only mandatory method to overwrite is 
 * <code>hasTerminated</code>
 * <br>
 * Optional methods to override are
 * <ul>
 * <li><code>customPaint</code></li>
 * <li><code>handleEmptyEventQueue</code></li>
 * <li><code>onExit</code></li>
 * <li><code>preRun</code></li>
 * <li><code>preRound</code></li>
 * <li><code>postRound</code></li>
 * <li><code>checkProjectRequirements</code></li>
 * </ul>
 * @see sinalgo.runtime.AbstractCustomGlobal for more details.
 * <br>
 * In addition, this class also provides the possibility to extend the framework with
 * custom methods that can be called either through the menu or via a button that is
 * added to the GUI. 
 */
public class CustomGlobal extends AbstractCustomGlobal{

    final String PATH = "/Users/robertopalamaro/Desktop/InputFile/";
    //Logging log = Logging.getLogger("ms_log.txt");
    boolean first_Algorithm = false;
    boolean second_Algorithm = false;
    boolean third_Algorithm = false;
    boolean fourth_algorithm = false;
    boolean approximazion_alg = false;
    boolean fifth_algorithm = false;
    Integer tempFourth = 0;
    Integer algorithm_choosed = -1;


    public boolean hasTerminated() {
        if(this.algorithm_choosed !=-1){
            switch(this.algorithm_choosed) {
                case 1:
                    return this.first_Algorithm;
                case 2:
                    return this.second_Algorithm;
                case 3:
                    return this.third_Algorithm;
                case 4:
                    return this.approximazion_alg;
                case 5:
                    return this.fifth_algorithm;
            }
        }
        return false;
    }
    private Edge getEdgeByStartEndNode(Node a, Node b){
        Edge temp;
        for(Iterator<Edge> it = a.outgoingConnections.iterator();it.hasNext();){
            temp = it.next();
            if(temp.endNode.equals(b)){
                return temp;

            }
        }
        return null;
    }
    private int computeMatrix(){
        int n = Tools.getNodeList().size();
        double[][] matrix = new double[n][n];
        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
            Node i = it.next();
            for (Iterator<Node> it2 = Tools.getNodeList().iterator();it2.hasNext();){
                Node j = it2.next();
                int _i = i.ID -1;
                int _j = j.ID -1;
                if(i.ID == j.ID){
                    matrix[_i][_j] = 0;
                }
                if(!i.outgoingConnections.contains(i, j)){
                    matrix[_i][_j] = 0;
                }
                else if(i.outgoingConnections.contains(i, j) && i.ID < j.ID){
                    matrix[_i][_j] = this.getEdgeByStartEndNode(i, j).getID();
                }
                else if(i.outgoingConnections.contains(i, j) && i.ID > j.ID){
                    matrix[_i][_j] = -(this.getEdgeByStartEndNode(i, j).getID());
                }
            }

        }
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                //log.log(String.valueOf(matrix[i][j])+" ");
            }
            //log.logln();
        }
        Matrix m = new Matrix(matrix);
        return m.rank();
    }


    @Override
    public void preRun() {
        // TODO Auto-generated method stub
        super.preRun();
    }
    private Integer checkingIfAllNodesHasFinished(){
        Integer count = 0;
        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
            MSNode node = (MSNode)it.next();
            if(!node.isEnd_flag()){
                return -1;
            }
            if(node.isMarried()){
                count+=1;
            }
        }
        return count/2;
    }

    private void printTheMarriages(){
        TreeMap<Integer,Integer> map = new TreeMap<Integer, Integer>();
        List<Integer> singleList = new ArrayList<Integer>();
        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();)

        {
            MS4Node node = (MS4Node)it.next();
            if(node.isMarried()){
                if(node.pointingNode != -1 && node.getP_v()!=-1){
                    if(!map.containsKey(node.ID) && !map.containsKey(node.pointingNode)){
                        map.put(node.ID,node.pointingNode);
                    }
                }
                if(node.getP_v() != -1 && node.pointingNode == -1){
                    if(!map.containsKey(node.ID) && !map.containsKey(node.getP_v())){
                        map.put(node.ID,node.getP_v());
                    }
                }
            }
            else{
                singleList.add(node.ID);
            }
        }
        //log.logln("Married nodes ---> "+map.toString());
        //log.logln("Single nodes ----> "+singleList.toString());
    }
    @Override
    public void postRound() {
        // TODO Auto-generated method stub
        super.postRound();
        Integer res = 0;
        if(this.algorithm_choosed!=-1){
            switch(this.algorithm_choosed) {
                case 1:
                    if((res=this.checkingIfAllNodesHasFinished())!=-1 && !this.first_Algorithm){
                        this.first_Algorithm = true;
                        Tools.appendToOutput("Algorithm1 converge in '" + Tools.getGlobalTime() + "'Steps'\n");
                        Tools.appendToOutput("Algorithm1 find this size of max matching "+res+"\n");
                    }
                    break;
                case 2:
                    if ((res=this.checkingIfAllNodesHasFinished())!=-1 && !this.second_Algorithm) {
                        this.second_Algorithm = true;
                        Tools.appendToOutput("Algorithm2 converge in  '" + Tools.getGlobalTime() + "'Steps'\n");
                        Tools.appendToOutput("Algorithm2 find this size of max matching "+res+"\n");
                    }
                    break;
                case 3:
                    if ((res=this.checkingIfAllNodesHasFinished())!=-1 && !this.third_Algorithm) {
                        this.third_Algorithm = true;
                        Tools.appendToOutput("Algorithm3 converge in  '" + Tools.getGlobalTime() + "'Steps'\n");
                        Tools.appendToOutput("Algorithm3 find this size of max matching "+res+"\n");
                        this.tempFourth = res;
                    }
                    break;
                case 4:
                    if(!this.fourth_algorithm && (res=this.checkingIfAllNodesHasFinished())!=-1) {
                        Integer c = 0;
                        this.fourth_algorithm = true;
                        Tools.appendToOutput("Maximal converge in '" + Tools.getGlobalTime() + "'Steps'\n");
                        Tools.appendToOutput("Maximal matching size is = "+res+"\n");
                        //log.logln("Try to find the optimum!!!");
                        for (Iterator<Node> it = Tools.getNodeList().iterator(); it.hasNext();) {
                            MS4Node node = (MS4Node) it.next();
                            if(node.isMarried()){
                                c+=1;
                            }
                            node.setFindTheOptimum();
                            node.end_flag = false;
                        }
                        this.printTheMarriages();
                        //log.logln("Total number of nodes married = "+c);
                        this.tempFourth = res;
                    }
                    if(this.fourth_algorithm && (res=this.checkingIfAllNodesHasFinished())!=-1 && !this.approximazion_alg){
                        Tools.appendToOutput("RES ---->"+res+"\n");
                        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
                            MS4Node node = (MS4Node)it.next();
                            if(node.isSecondMatchDone()){
                                //Tools.appendToOutput("**NODE with a success in MATCH SECOND =="+node.ID+" \n");
                                node.setColorToEdgeAndNodes(Color.BLACK, Tools.getNodeByID(node.pointingNode));
                                Tools.getNodeByID(node.pointingNode).setColor(node.defaultColor);
                                node.setColorToEdgeAndNodes(Color.MAGENTA, Tools.getNodeByID(node.getP_v()));
                                ((MS4Node)Tools.getNodeByID(node.getP_v())).isMarried = true;
                                MS4Node married = (MS4Node)Tools.getNodeByID(node.getPointingNode());
                                married.setColorToEdgeAndNodes(Color.MAGENTA,Tools.getNodeByID(married.getP_v()));
                                ((MS4Node)Tools.getNodeByID(married.getP_v())).isMarried = true;
                            }
                        }
                        Integer newRes = checkingIfAllNodesHasFinished();
                        Tools.appendToOutput("Approx algorithm converge in '" + Tools.getGlobalTime() + "'Steps'\n");
                        Tools.appendToOutput("New Maximal matching size is = "+newRes+" so improved by "+(newRes-tempFourth)+"\n");
                        //log.logln("--------------------APPROX RESULT-----------------------------");
                        Integer count = 0;
                        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
                            MS4Node node = (MS4Node)it.next();
                            if(node.isMarried()) {
                                count++;
                            }
                        }
                        this.printTheMarriages();
                        //log.logln("----------------NUMBER OF MARRIED : "+count+"-----------------------------");
                        this.approximazion_alg = true;
                        break;
                    }
                case 5:
                    if ((res=this.checkingIfAllNodesHasFinished())!=-1 && !this.fifth_algorithm) {
                        this.fifth_algorithm = true;
                        Tools.appendToOutput("Algorithm3_probabilistic converge in  '" + Tools.getGlobalTime() + "'Steps'\n");
                        Tools.appendToOutput("Algorithm3_probabilistic  find this size of max matching " + res + "\n");
                    }

            }
        }
    }
    /*
        TODO: modify this method
         Thus two neighboring nodes v and w are matched if and only if
         either pv = w and pw = v, or if pv = null, pw = null, and (v,w) ∈ M′.
         In a stable state, all nodes in matched(V) will satisfy one of these conditions,
         while each node x ∈ single(V )
         will have px = null if it has not been able to match and px = v, where v ∈ single(N(x)) if it has matched with v.
     */
    public boolean checkingIfThereIsAnIncreaseOfMatch(){

        //log.logln("--------------State for the nodes---------------------");
        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
            MS4Node node = (MS4Node)it.next();
            //log.logln("NODE_ID "+node.ID+"pointing node: "+node.pointingNode+" p_v = "+node.getP_v()+" isMarried "+node.isMarried());
        }
        //log.logln("-------------------------------------------------------");
        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
            MS4Node node = (MS4Node)it.next();
            MS4Node pointing_node;
            if(!node.isMarried() && (node.getP_v() == -1
                    || (pointing_node=(MS4Node)Tools.getNodeByID(node.getP_v()))!=null
                    && node.outgoingConnections.contains(node,pointing_node)
                    && pointing_node.getP_v() ==node.ID)){
                //log.logln("NODE_ID "+node.ID+" SINGLE");
                continue;
            }
            pointing_node = (MS4Node)Tools.getNodeByID(node.getP_v());
            if((pointing_node!=null && pointing_node.getP_v() == node.ID) || (node.getP_v() == -1 && node.isMarried && ((MS4Node)(Tools.getNodeByID(node.pointingNode))).getP_v()==-1)){
                //log.logln("NODE_ID "+node.ID+" MATCHED");
                continue;
            }
            return false;
        }
        return true;
    }
    /**
     * An example of a method that will be available through the menu of the GUI.
     */
    @AbstractCustomGlobal.GlobalMethod(menuText="Echo")
    public void echo() {
        // Query the user for an input
        String answer = JOptionPane.showInputDialog(null, "This is an example.\nType in any text to echo.");
        // Show an information message
        JOptionPane.showMessageDialog(null, "You typed '" + answer + "'", "Example Echo", JOptionPane.INFORMATION_MESSAGE);
    }

    @AbstractCustomGlobal.GlobalMethod(menuText="Set Threshold Probability")
    public void setThreshold() {
        // Query the user for an input
        if(Tools.getGlobalTime()!=0){
            JOptionPane.showMessageDialog(null, "You can change this probability only when the simulation start","Alert", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String answer = JOptionPane.showInputDialog(null, "Set the probability that a node can be selected to run.");
        // Show an information message
        try{
            double k = Double.parseDouble(answer);
            Iterator<Node> it = Tools.getNodeList().iterator();
            while(it.hasNext()){
                Node n = it.next();
                if(n.getClass() == MSNode.class){
                    MSNode n1 = (MSNode)n;
                    n1.setThresholdProbability(k);
                }
                if(n.getClass() == MS2Node.class){
                    MS2Node n1 = (MS2Node)n;
                    n1.setThresholdProbability(k);
                }
            }
            JOptionPane.showMessageDialog(null, "Well done you have set this value:"+k,"Notice", JOptionPane.INFORMATION_MESSAGE);
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "You must insert a valid double ", "Alert", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @AbstractCustomGlobal.GlobalMethod(menuText="Choose Algorithm")
    public void choosedAlgorithm() {
        String answer = JOptionPane.showInputDialog(null, "Choose which algorithm analyze.");
        // Show an information message
        try{
            this.algorithm_choosed = Integer.parseInt(answer);
            if(this.algorithm_choosed !=1 && this.algorithm_choosed != 2 && this.algorithm_choosed != 3 && this.algorithm_choosed!=4 && this.algorithm_choosed!=5){
                JOptionPane.showMessageDialog(null, "You must insert a valid Algorithm -> 1|2|3 ", "Alert", JOptionPane.INFORMATION_MESSAGE);
                this.algorithm_choosed = -1;
            }
            JOptionPane.showMessageDialog(null, "You have selected:"+this.algorithm_choosed,"Notice", JOptionPane.INFORMATION_MESSAGE);
            /*
            if(this.algorithm_choosed == 4){
                for (Iterator<Node> it = Tools.getNodeList().iterator(); it.hasNext(); ) {
                    MS4Node n = (MS4Node) it.next();
                    n.setFindTheOptimum();
                }
            }*/

        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "You must insert a valid Algorithm -> 1|2|3 ", "Alert", JOptionPane.INFORMATION_MESSAGE);

        }
    }

    @AbstractCustomGlobal.CustomButton(buttonText="CLEAR", toolTipText="Reset the colors of the GUI")
    public void sampleButton2() {
        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
            Node n = it.next();
            if(n.getColor()!=Color.BLACK){
                n.setColor(Color.BLACK);
            }
            for(Iterator<Edge> it2=n.outgoingConnections.iterator();it2.hasNext();){
                Edge e = it2.next();
                if(e.defaultColor != Color.BLACK){
                    e.defaultColor = Color.BLACK;
                }
            }
        }
    }

    @AbstractCustomGlobal.CustomButton(buttonText="Verify", toolTipText="Verify Upper bound")
    public void showPane(){
        String answer1 = JOptionPane.showInputDialog(null, "Choose kind of algorithm.. 1 | 2 | 3");
        Integer algorithmType;
        try{
            algorithmType= Integer.valueOf(answer1);
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Wrong number inserted."+answer1,"ERROR",JOptionPane.ERROR_MESSAGE);
            return;
        }

        String answer = JOptionPane.showInputDialog(null, "Insert |N| and |E| in this form N,E");
        String[] parameters = answer.split(",");
        if(parameters.length == 2){
            try{
                Integer n = Integer.valueOf(parameters[0]);
                Integer e = Integer.valueOf(parameters[1]);
                switch(algorithmType){
                    case 1:
                        JOptionPane.showMessageDialog(null, "Upper bound for algorithm = "+algorithmType+ " = "
                                +(4*n+2*e),"Answer",JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Error with parameters..you have insert."+answer);
            }

        }
    }
    @AbstractCustomGlobal.CustomButton(buttonText="Check", toolTipText="cond")
    public void checkCondition() {
        Tools.appendToOutput("Max matching is --> " + this.checkingIfThereIsAnIncreaseOfMatch() + "\n");
    }

    @AbstractCustomGlobal.CustomButton(buttonText="RK", toolTipText="Find Max Matching Size")
    public void rankOfMatrix() {
        JOptionPane.showMessageDialog(null, "Rank = "+(this.computeMatrix()/2), "Size of Max Matching", JOptionPane.INFORMATION_MESSAGE);
    }
    @AbstractCustomGlobal.CustomButton(buttonText="Edmond", toolTipText="Edmond")
    public void edmondAlgorithm() {
        Integer sizeOfGraph = Tools.getNodeList().size();
        @SuppressWarnings("unchecked")
        List<Integer>[] graph = new List[sizeOfGraph];
        for (int i = 0; i < sizeOfGraph; i++) {
            graph[i] = new ArrayList<Integer>();
        }
        for (Iterator<Node> it = Tools.getNodeList().iterator(); it.hasNext(); ) {
            for (Iterator<Edge> it2 = it.next().outgoingConnections.iterator(); it2.hasNext(); ) {
                Edge edge = it2.next();
                graph[(edge.startNode.ID - 1)].add((edge.endNode.ID - 1));
            }
        }
        List<Pair<Integer, Integer>> l = EdmondsMaximumCardinalityMatching.maxMatching(graph);
        for (Pair<Integer, Integer> p : l) {
            Tools.appendToOutput(String.valueOf(p.getKey()) + " : " + String.valueOf(p.getValue()) + "\n");
            Node i = Tools.getNodeByID(p.getKey());
            Node j = Tools.getNodeByID(p.getValue());
            i.setColor(Color.MAGENTA);
            j.setColor(Color.MAGENTA);
            for (Iterator<Edge> it = i.outgoingConnections.iterator(); it.hasNext(); ) {
                Edge e = it.next();
                if (e.endNode.ID == j.ID) {
                    e.defaultColor = Color.MAGENTA;
                }
            }
            for (Iterator<Edge> it = j.outgoingConnections.iterator(); it.hasNext(); ) {
                Edge e = it.next();
                if (e.endNode.ID == i.ID) {
                    e.defaultColor = Color.MAGENTA;
                }
            }

        }
        Tools.repaintGUI();
        JOptionPane.showMessageDialog(null, "Maximum matching for the given graph is: "
                + l.size() / 2, "Max Matching", JOptionPane.INFORMATION_MESSAGE);
    }

    @AbstractCustomGlobal.CustomButton(buttonText="[Build Graph]", toolTipText="Build the Graph according to Erdos Enji model")
    public void buildGraph(){
        try {
            double probability = Double.parseDouble(Tools.showQueryDialog("p factor [0,1]:"));
            Integer algorithmType = Integer.parseInt(Tools.showQueryDialog("Choose algorithm 1|2|3|4|5:"));
            Integer numberOfFaults = Integer.parseInt(Tools.showQueryDialog("Insert the number of initial faults"));
            if ((probability < 0 || probability > 1) || (algorithmType < 1 || algorithmType > 5)) {
                JOptionPane.showMessageDialog(null, "Insert appropriate values for probability and number of Nodes", "Alert", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            this.algorithm_choosed = algorithmType;
            JOptionPane.showMessageDialog(null, "Building the graph with this parameters: |N|=" + Tools.getNodeList().size() + " p =" + probability + "Number of faults = " + numberOfFaults, "Notice", JOptionPane.INFORMATION_MESSAGE);
            //log.logln("Parameters for simulation are " + Tools.getNodeList().size() + " p: " + probability + " algorithm = " + algorithmType + "Number of faults = " + numberOfFaults);
            buildErdosRenyiConnections(probability);
            if (numberOfFaults < 0 || numberOfFaults > Tools.getNodeList().size()) {
                JOptionPane.showMessageDialog(null, "You have insert a wrong number of faults..taking a random value", "Error", JOptionPane.INFORMATION_MESSAGE);
                faultTheStateOfNode(Tools.getRandomNumberGenerator().nextInt(Tools.getNodeList().size()));
            } else {
                faultTheStateOfNode(numberOfFaults);
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Insert appropriate values for values", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    public void buildErdosRenyiConnections(Double probability){
        PrintWriter writer,writer2;
        writer  = null;
        writer2 = null;
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm");
        try {
            writer = new PrintWriter(PATH+"Simulation" + sdf.format(cal.getTime()) + ".txt");
            writer.println("#####----- start of node posiitons -----#####");
            for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
                Node node = it.next();
                Position pos = node.getPosition();
                writer.println(String.valueOf(pos.xCoord)+","+String.valueOf(pos.yCoord)+","+String.valueOf(pos.zCoord));
            }
            writer2 = new PrintWriter(PATH+"Connections"+sdf.format(cal.getTime())+".txt");
            ArrayList<Pair<Integer,Integer>> l = computeCombinations();
            for(Pair<Integer,Integer> pair : l){
                if(Tools.getRandomNumberGenerator().nextDouble()<=probability){
                    Node n = Tools.getNodeByID(pair.getKey());
                    n.addBidirectionalConnectionTo(Tools.getNodeByID(pair.getValue()));
                    writer2.println(pair.getKey()+","+pair.getValue());
                }
            }
            Tools.repaintGUI();
            for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
                Node n = it.next();
                for(Iterator<Edge> edge = n.outgoingConnections.iterator();edge.hasNext();){
                    Edge e = edge.next();
                    //log.logln("Start node :"+e.startNode.ID +" to "+ e.endNode.ID);
                }
            }
        }catch (FileNotFoundException e){
            Tools.appendToOutput("File not found");
        }finally {
            if(writer!=null){
                writer.close();
            }
            if(writer2!=null){
                writer2.close();
            }
        }

    }
    private static ArrayList<Pair<Integer,Integer>> computeCombinations(){
        ArrayList<Pair<Integer,Integer>> list = new ArrayList<Pair<Integer, Integer>>();
        for(Iterator<Node> it = Tools.getNodeList().iterator();it.hasNext();){
            Node i = it.next();
            for(Iterator<Node> it2 = Tools.getNodeList().iterator();it2.hasNext();){
                Node j = it2.next();
                Integer i_ID = i.ID;
                Integer j_ID = j.ID;
                if(i_ID!=j_ID){
                    Pair<Integer,Integer> p_temp = new Pair<Integer, Integer>(i_ID,j_ID);
                    Pair<Integer,Integer> p_inv = new Pair<Integer, Integer>(j_ID,i_ID);
                    if(!list.contains(p_temp) && !list.contains(p_inv)){
                        list.add(p_temp);
                    }
                }
            }
        }
        return list;
    }
    @AbstractCustomGlobal.CustomButton(buttonText="[Connections]", toolTipText="Create Connections")
    public void connectionsByFile() {
        String path = Tools.showQueryDialog("Insert the name of the connection file");
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Insert a valid name for connection file", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(PATH+"/Connections"+path+".txt"));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] tokens = sCurrentLine.split(",");
                Integer node_i = Integer.parseInt(tokens[0]);
                Integer node_j = Integer.parseInt(tokens[1]);
                Node n = Tools.getNodeByID(node_i);
                n.addBidirectionalConnectionTo(Tools.getNodeByID(node_j));
            }
            path = Tools.showQueryDialog("Insert the number of faults");
            Integer k = Integer.parseInt(path);
            this.faultTheStateOfNode(k);

        }
        catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "File not found!!!", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;

        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Wrong number !!!", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Tools.repaintGUI();
        }
    }

    @AbstractCustomGlobal.CustomButton(buttonText="[Fault]", toolTipText="Fault nodes at random")
    public void faultTheNode(){
        try {
            Integer numFault = Integer.parseInt(Tools.showQueryDialog("How many nodes to fault?"));
            this.faultTheStateOfNode(numFault);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }
    public void faultTheStateOfNode(Integer k) {
        Set<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < k; i++) {
            MSNode node;
            do {
                node = (MSNode) Tools.getRandomNode();
            } while (set.contains(node.ID));
            node.setFaultState();
            set.add(node.ID);
        }
        Tools.repaintGUI();
    }
	
}



