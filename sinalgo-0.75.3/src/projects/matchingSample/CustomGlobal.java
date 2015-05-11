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
	Logging log = Logging.getLogger("ms_log.txt");
	boolean first_Algorithm = false;
	boolean second_Algorithm = false;
	boolean third_Algorithm = false;
    boolean fourth_algorithm = false;
	Integer edges_fist_algorithm;
	Integer edges_second_algorithm;
	Integer edges_third_algorithm;
	Integer nodes_first_algorithm;
	boolean OPTIMAL_CASE = false;

	Integer algorithm_choosed = -1;
	/* (non-Javadoc)
	 * @see runtime.AbstractCustomGlobal#hasTerminated()
	 */
	public boolean hasTerminated() {
		//return first_Algorithm && second_Algorithm;
		if(this.algorithm_choosed !=-1){
			switch(this.algorithm_choosed) {
				case 1:
					return this.first_Algorithm;
				case 2:
					return this.second_Algorithm;
				case 3:
					return this.third_Algorithm;
				case 4:
					return false;
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
				log.log(String.valueOf(matrix[i][j])+" ");
			}
			log.logln();
		}
		Matrix m = new Matrix(matrix);
		return m.rank();
	}
	
	
	@Override
	public void preRun() {
		// TODO Auto-generated method stub
		super.preRun();
		//this.flag = false;
		this.edges_fist_algorithm = -1;
		this.nodes_first_algorithm = -1;
	}
	private Integer check_First_Algorithm(){
		Iterator<Node> it = Tools.getNodeList().iterator();
		Node _n;
        Integer count = 0;
		while(it.hasNext()){
			_n = it.next();
			if(_n.getClass() == MSNode.class){
				MSNode n = (MSNode)_n;
				this.edges_fist_algorithm += n.outgoingConnections.size();
				this.nodes_first_algorithm+=1;
				if(!n.getEndFlag()){
					return -1;
				}
                if(n.getMarried_egde()!=null){
                    count +=1;
                }
			}
		}
		return count;
	}

	private boolean check_Second_Algorithm(){
		Iterator<Node> it = Tools.getNodeList().iterator();
		Node _n;
		while(it.hasNext()){
			_n = it.next();
			if(_n.getClass() == MS2Node.class){
				MS2Node n = (MS2Node)_n;
				if(!n.getEndFlag()){
					return false; 
				}
			}
		}
		return true;
		
	}
	private boolean check_Third_Algorithm(){
		Iterator<Node> it = Tools.getNodeList().iterator();
		Node _n;
		while(it.hasNext()){
			_n = it.next();
			if(_n.getClass() == MS3Node.class){
				MS3Node n = (MS3Node)_n;
				if(!n.getEndFlag()){
					return false; 
				}
			}
		}
		return true;
		
	}
    private Integer check_Fourth_Algorithm(){
        Iterator<Node> it = Tools.getNodeList().iterator();
        Node _n;
        Integer count = 0;
        while(it.hasNext()){
            _n = it.next();
            if(_n.getClass() == MS4Node.class){
                MS4Node n = (MS4Node)_n;
                this.edges_fist_algorithm += n.outgoingConnections.size();
                if(!n.getEndFlag()){
                    return -1;
                }
                count +=1;
            }
        }
        return count;
    }
	@Override
	public void postRound() {
	// TODO Auto-generated method stub
		super.postRound();
        Integer res;
		if(this.algorithm_choosed!=-1){
			switch(this.algorithm_choosed) {
                case 1:
                    if((res=this.check_First_Algorithm())!=-1 && !this.first_Algorithm){
                        this.first_Algorithm = true;
                        Tools.appendToOutput("Algorithm1 converge in '" + Tools.getGlobalTime() + "'Steps'\n");
                        Tools.appendToOutput("Algorithm1 find this size of max matching "+res+"\n");
                    }
                    break;
				case 2:
					if (this.check_Second_Algorithm() && !this.second_Algorithm) {
						this.second_Algorithm = true;
						Tools.appendToOutput("Algorithm2 converge in  '" + Tools.getGlobalTime() + "'Steps'\n");
					}
					break;
				case 3:
					if (this.check_Third_Algorithm() && !this.third_Algorithm) {
						this.third_Algorithm = true;
						Tools.appendToOutput("Algorithm3 converge in  '" + Tools.getGlobalTime() + "'Steps'\n");
					}
					break;
				case 4:
                    if((res=this.check_Fourth_Algorithm())!=-1 && !this.fourth_algorithm) {
                        this.fourth_algorithm = true;
                        Tools.appendToOutput("Maximal converge in '" + Tools.getGlobalTime() + "'Steps'\n");
                        Tools.appendToOutput("Maximal matching size is =  :"+res/2);
                        log.logln("Try to find the optimum!!!");
                        for (Iterator<Node> it = Tools.getNodeList().iterator(); it.hasNext();) {
                            MS4Node node = (MS4Node) it.next();
                            node.setFindTheOptimum();
                        }
                    }
			}
		}
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
	@AbstractCustomGlobal.GlobalMethod(menuText="Optimal Case")
	public void optimalCase() {
		// Query the user for an input
		String answer = JOptionPane.showInputDialog(null, "Insert Y if you want the optimal solution N otherwise.");
		// Show an information message 
		if(answer.equals("Y")){
			this.OPTIMAL_CASE = true;
		}else{
			this.OPTIMAL_CASE = false;
		}
		if(Tools.getGlobalTime()!=0){
			JOptionPane.showMessageDialog(null, "Error you can select the optimal algorithm only at beginning","Alert", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
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
			if(this.algorithm_choosed !=1 && this.algorithm_choosed != 2 && this.algorithm_choosed != 3 && this.algorithm_choosed!=4){
				JOptionPane.showMessageDialog(null, "You must insert a valid Algorithm -> 1|2|3 ", "Alert", JOptionPane.INFORMATION_MESSAGE);
				this.algorithm_choosed = -1;
			}
			JOptionPane.showMessageDialog(null, "You have selected:"+this.algorithm_choosed,"Notice", JOptionPane.INFORMATION_MESSAGE);
			if(this.algorithm_choosed == 4){
				for (Iterator<Node> it = Tools.getNodeList().iterator(); it.hasNext(); ) {
					MS4Node n = (MS4Node) it.next();
					n.setFindTheOptimum();
				}
			}

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
            Integer algorithmType = Integer.parseInt(Tools.showQueryDialog("Choose algorithm 1|2|3|4:"));
            Integer numberOfFaults = Integer.parseInt(Tools.showQueryDialog("Insert the number of initial faults"));
            if ((probability < 0 || probability > 1) || (algorithmType < 1 || algorithmType > 4)) {
                JOptionPane.showMessageDialog(null, "Insert appropriate values for probability and number of Nodes", "Alert", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            this.algorithm_choosed = algorithmType;
            JOptionPane.showMessageDialog(null, "Building the graph with this parameters: |N|=" + Tools.getNodeList().size() + " p =" + probability + "Number of faults = " + numberOfFaults, "Notice", JOptionPane.INFORMATION_MESSAGE);
            log.logln("Parameters for simulation are " + Tools.getNodeList().size() + " p: " + probability + " algorithm = " + algorithmType + "Number of faults = " + numberOfFaults);
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

    public void faultTheStateOfNode(Integer k){
        Set<Integer> set = new HashSet<Integer>();
        for(int i=0;i<k;i++){
            switch (this.algorithm_choosed){
                case 1:
                    MSNode node;
                    do{
                        node = (MSNode)Tools.getRandomNode();
                    }while (set.contains(node.ID));
                    node.setFaultState();
                    set.add(node.ID);
                    break;
                case 4:
                    do{
                        node = (MSNode)Tools.getRandomNode();
                    }while (set.contains(node.ID));
                    node.setFaultState();
                    set.add(node.ID);
                    break;
            }
        }
        Tools.repaintGUI();
    }
    public void buildErdosRenyiConnections(Double probability){
        PrintWriter writer,writer2;
        writer  = null;
        writer2 = null;
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
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
                    log.logln("Start node :"+e.startNode.ID +" to "+ e.endNode.ID);
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
            br = new BufferedReader(new FileReader(PATH+path));
            while ((sCurrentLine = br.readLine()) != null) {
                String[] tokens = sCurrentLine.split(",");
                Integer node_i = Integer.parseInt(tokens[0]);
                Integer node_j = Integer.parseInt(tokens[1]);
                Node n = Tools.getNodeByID(node_i);
                n.addBidirectionalConnectionTo(Tools.getNodeByID(node_j));
            }

        }
        catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "File not found!!!", "Error", JOptionPane.INFORMATION_MESSAGE);
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
	
}



