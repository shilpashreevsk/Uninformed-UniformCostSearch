import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class FindRoute{
    public static void main(String[] args){

        int n=0;
        int dup = 0;
        String[] lineArr;
        ArrayList<String> startCity = new ArrayList<String>(); 	
        ArrayList<String> endCity = new ArrayList<String>();
        ArrayList<Double> distance = new ArrayList<Double>();
        ArrayList<String> nodes = new ArrayList<String>();
        ArrayList<Node> nodeList = new ArrayList<Node>();
        
        String fileName = args[0];
        String source=args[1];
        String destination=args[2];

        try
        {
        	//Split the line with source, destination and cost into separate ArrayList
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while (!(line = br.readLine()).equalsIgnoreCase("END OF INPUT")) {
                lineArr = line.split(" ");
                startCity.add(lineArr[0]);
                endCity.add(lineArr[1]);
                distance.add(Double.parseDouble(lineArr[2]));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // To get all unique nodes from startCity and endCity ArrayList
        while (n < startCity.size())
        {
            int x = 0;
            while(x < nodes.size())
            {
                if (nodes.get(x).contains(startCity.get(n)))
                {
                    dup = 1;
                    break;
                }
                x++;
            }
            if (dup == 0)
            {
                nodes.add(startCity.get(n));
            }
            dup = 0;
            n++;
        }
        n = 0;
        while (n < endCity.size())
        {
            int x = 0;
            while(x < nodes.size())
            {
                if (nodes.get(x).contains(endCity.get(n)))
                {
                    dup = 1;
                    break;
                }
                x++;
            }
            if (dup == 0)
            {
                nodes.add(endCity.get(n));
            }
            dup = 0;
            n++;
        }
        n = 0;
        while (n < nodes.size())
        {
            nodeList.add(new Node(nodes.get(n)));
            n++;
        }
        n = 0;
        int i = 0;
        int j = 0;
        
        //To create edges between nodes
        ArrayList<ConnectingPath> Edges = new ArrayList<ConnectingPath>();
        while(n < nodeList.size())
        {
            while (i < startCity.size() && i<endCity.size())
            {
                if (nodeList.get(n).state.contains(startCity.get(i)))
                {
                    while (j < nodeList.size())
                    {
                        if (nodeList.get(j).state.contains(endCity.get(i)))
                        {
                            break;
                        }
                        j++;
                    }
                    Edges.add(new ConnectingPath(nodeList.get(j), distance.get(i)));
                    j = 0;
                }
                if (nodeList.get(n).state.contains(endCity.get(i)))
                {
                    while (j < nodes.size())
                    {
                        if (nodeList.get(j).state.contains(startCity.get(i)))
                        {
                            break;
                        }
                        j++;
                    }
                    Edges.add(new ConnectingPath(nodeList.get(j), distance.get(i)));
                    j = 0;
                }
                i++;
            }
            i = 0;
            nodeList.get(n).adjacencies = Edges.toArray(new ConnectingPath[Edges.size()]);
            Edges.clear();
            n++;
        }
        n = 0;
        i = 0;
        // To get the index of source and destination mentioned in cmd line
        while (n < nodeList.size())
        {
            if (nodeList.get(n).state.contains(source))
            {
                break;
            }
            n++;
        }
        while (i < nodeList.size())
        {
            if (nodeList.get(i).state.contains(destination))
            {
                break;
            }
            i++;
        }
        
        //Call to UniformCostSearch
        UniformCostSearch(nodeList.get(n),nodeList.get(i));
        
        //To print the path
        List<Node> path = printPath(nodeList.get(i));
        
        //If no connection between two nodes
        if(1 == path.size())
        {
            System.out.println("distance: infinity\r\n" + "route: \r\n" + "none");
        }
        //Prints the source, intermediate and destination node
        else
        {
        	System.out.println("route:\r\n");
        	int p=0;
        	while(p<path.size()-1) {
        		for(int i1=0;i1<startCity.size()&&i1<endCity.size();i1++) {
        			if(((path.get(p).state.equalsIgnoreCase(startCity.get(i1))) && (path.get(p+1).state.equalsIgnoreCase(endCity.get(i1))))||((path.get(p).state.equalsIgnoreCase(endCity.get(i1))) && (path.get(p+1).state.equalsIgnoreCase(startCity.get(i1)))))
        			{
        				System.out.println(path.get(p).state+" to "+path.get(p+1).state+", "+distance.get(i1));
        				break;
        			}
        		}p++;
        	}
        	System.out.println("distance: "+nodeList.get(i).pathCost);
        }

    }

    public static void UniformCostSearch(Node source, Node goal){

        source.pathCost = 0;
        //Minimum priority queue 
        PriorityQueue<Node> queue = new PriorityQueue<Node>(20,
                new Comparator<Node>(){

                    //override compare method
                    public int compare(Node i, Node j){
                        if(i.pathCost > j.pathCost){
                            return 1;
                        }

                        else if (i.pathCost < j.pathCost){
                            return -1;
                        }

                        else{
                            return 0;
                        }
                    }
                }

        );
        //Add the source into the queue
        queue.add(source);
        
        Set<Node> closedSet = new HashSet<Node>();
        boolean found = false;

        //while frontier is not empty
        do{

            Node current = queue.poll();
            closedSet.add(current); 		//indicates it is visited


            if(current.state.equals(goal.state)){	//If current state is goal state then destination is reached
                found = true;


            }
            //If the current node has successor then expand
            for(ConnectingPath e: current.adjacencies){
                Node child = e.target;
                double cost = e.cost;
            
            //Add the successor to the queue if it is not explored 
                if(!closedSet.contains(child) && !queue.contains(child)){
                	child.pathCost=current.pathCost+cost;
                    child.parent = current;
                    queue.add(child);

                }
              //If already present, checks the cumulative cost, if it less than child cost visited before, update it
                else if((queue.contains(child))&&(child.pathCost>(current.pathCost+cost))){
                    
                	child.parent=current;
                    child.pathCost=current.pathCost+cost;
                    
                    queue.remove(child);
                    queue.add(child);
                }


            }
        }while(!queue.isEmpty()&&(found==false));

    }
//Print the path
public static List<Node> printPath(Node target){
        List<Node> path = new ArrayList<Node>();
        for(Node node = target; node!=null; node = node.parent){
            path.add(node);
        }

        Collections.reverse(path);
        
        return path;

    }

}
