//Connects the nodes
public class ConnectingPath {

	    public  double cost;	//step cost for edge
	    public  Node target;	//the destination node from the source which class this class

	    //Constructor to initialize step cost and destination 
		public ConnectingPath(Node node, double costVal) {
			// TODO Auto-generated constructor stub
			cost=costVal;
			target=node;
		}
}
