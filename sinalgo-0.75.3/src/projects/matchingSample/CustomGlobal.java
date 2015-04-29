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

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import projects.matchingSample.nodes.nodeImplementations.MS2Node;
import projects.matchingSample.nodes.nodeImplementations.MS3Node;
import projects.matchingSample.nodes.nodeImplementations.MSNode;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.BidirectionalEdge;
import sinalgo.nodes.edges.Edge;
import sinalgo.runtime.AbstractCustomGlobal;
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
	
	Logging log = Logging.getLogger("ms_log.txt");
	boolean first_Algorithm = false;
	boolean second_Algorithm = false;
	boolean third_Algorithm = false;
	Integer edges_fist_algorithm;
	Integer edges_second_algorithm;
	Integer edges_third_algorithm;
	Integer nodes_first_algorithm;
	
	Integer algorithm_choosed = -1;
	/* (non-Javadoc)
	 * @see runtime.AbstractCustomGlobal#hasTerminated()
	 */
	public boolean hasTerminated() {
		//return first_Algorithm && second_Algorithm;
		if(this.algorithm_choosed !=-1){
			switch(this.algorithm_choosed){
			case 1:
				return this.first_Algorithm;
			case 2:
				return this.second_Algorithm;
			case 3:
				return this.third_Algorithm;
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
	private boolean check_First_Algorithm(){
		Iterator<Node> it = Tools.getNodeList().iterator();
		Node _n;
		while(it.hasNext()){
			_n = it.next();
			if(_n.getClass() == MSNode.class){
				MSNode n = (MSNode)_n;
				this.edges_fist_algorithm += n.outgoingConnections.size();
				this.nodes_first_algorithm+=1;
				if(!n.getEndFlag()){
					return false; 
				}
			}
		}
		return true;
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
	@Override
	public void postRound() {
	// TODO Auto-generated method stub
		super.postRound();
		if(this.algorithm_choosed!=-1){
			switch(this.algorithm_choosed){
			case 1:
				if(this.check_First_Algorithm() && this.first_Algorithm == false){
					this.first_Algorithm = true;
					Tools.appendToOutput("Algorithm1 converge in '" + Tools.getGlobalTime() + "'Steps'\n");
				}
				break;
			case 2:
				if(this.check_Second_Algorithm() && this.second_Algorithm == false){
					this.second_Algorithm = true;
					Tools.appendToOutput("Algorithm2 converge in  '" + Tools.getGlobalTime() + "'Steps'\n");	
				}
				break;
			case 3:
				if(this.check_Third_Algorithm() && this.third_Algorithm == false){
					this.third_Algorithm = true;
					Tools.appendToOutput("Algorithm3 converge in  '" + Tools.getGlobalTime() + "'Steps'\n");	
				}
				break;
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
			if(this.algorithm_choosed !=1 && this.algorithm_choosed != 2 && this.algorithm_choosed != 3){
				JOptionPane.showMessageDialog(null, "You must insert a valid Algorithm -> 1|2|3 ", "Alert", JOptionPane.INFORMATION_MESSAGE);
				this.algorithm_choosed = -1;
			}
			JOptionPane.showMessageDialog(null, "You have selected:"+this.algorithm_choosed,"Notice", JOptionPane.INFORMATION_MESSAGE);

		}catch(NumberFormatException e){
			JOptionPane.showMessageDialog(null, "You must insert a valid Algorithm -> 1|2|3 ", "Alert", JOptionPane.INFORMATION_MESSAGE);

		}
	}
	
	/**
	 * An example to add a button to the user interface. In this sample, the button is labeled
	 * with a text 'GO'. Alternatively, you can specify an icon that is shown on the button. See
	 * AbstractCustomGlobal.CustomButton for more details.   
	 */
	@AbstractCustomGlobal.CustomButton(buttonText="GO", toolTipText="A sample button")
	public void sampleButton() {
		JOptionPane.showMessageDialog(null, "You Pressed the 'GO' button.");
		
	}
	@AbstractCustomGlobal.CustomButton(buttonText="CLEAR", toolTipText="Reset the state of each nodes")
	public void sampleButton2() {
		//JOptionPane.showMessageDialog(null, "You Pressed the 'GO' button.");		
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
	
	@AbstractCustomGlobal.CustomButton(buttonText="Try", toolTipText="Try")
	public void computeEdge(){
		/*
		Iterator<Node> it = Tools.getNodeList().iterator();
		HashMap<Integer,Edge> v = new HashMap<Integer, Edge>();
		int k = 1;
		while(it.hasNext()){
			for(Iterator<Edge>it2=it.next().outgoingConnections.iterator();it2.hasNext();){
				Edge e = it2.next();
				if(!v.containsValue(e)){
					v.put(k,e);
					k+=1;
				}
			}
		}
		for(int z=1;z<k;z++){
			log.logln("Edge id "+z+ " contains: "+v.get(z).toString());
		}*/
		Iterator<Node> it = Tools.getNodeList().iterator();
		while(it.hasNext()){
			for(Iterator<Edge>it2=it.next().outgoingConnections.iterator();it2.hasNext();){
				log.logln("Edge id --> "+it2.next().getID());

			}
		}
	}
	
	
}




