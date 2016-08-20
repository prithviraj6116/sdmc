package sdmc;


import java.util.ArrayList;
import java.util.Random;

public class Sdmc {

	/**
	 * @param args
	 */
	
	
//	public ArrayList<Switch> switches = new ArrayList<Switch>();
//	public ArrayList<Host> hosts = new ArrayList<Host>();
//	public ArrayList<LinkSwitchHost> linksSwitchHost = new ArrayList<LinkSwitchHost>();
//	public ArrayList<LinkSwitchSwitch> linksSwitchSwitch = new ArrayList<LinkSwitchSwitch>();
	public Integer[][] SwitchSwitchGraph;
	public Integer[][] SwitchHostGraph;
//	public Integer[][] Map;
//	public Link[][]  Links;
	public Link[][]  SwitchSwitchLinks;
	public Link[][]  SwitchHostLinks;
	public Integer numberOfHosts;
	public Integer numberOfSwitches;
	
	
	public static void main(String[] args) {
		System.out.println("Hello World!");
		Sdmc sdmc_ = new Sdmc();
		//sdmc_.createRandomNetwork(4, 6, 1);
		sdmc_.createFatTreeNetwork(4, 1);
		sdmc_.printNetwork();
	}

	public void printNetwork(){
		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfSwitches; countColumn++){
				System.out.print(SwitchSwitchGraph[countRow][countColumn] + " ");
			}
			System.out.println("");
		}
		System.out.println("");
		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfHosts; countColumn++){
				System.out.print(SwitchHostGraph[countRow][countColumn] + " ");
			}
			System.out.println("");
		}	
	}

	
	public void createNetworkWithoutLinks(int noSwitches, int noHosts){
		numberOfHosts = noHosts;
		numberOfSwitches = noSwitches;


		SwitchSwitchGraph = new Integer[numberOfSwitches][numberOfSwitches];
		SwitchHostGraph = new Integer[numberOfSwitches][numberOfHosts];
		SwitchSwitchLinks = new Link[numberOfSwitches][numberOfSwitches];
		SwitchHostLinks = new Link[numberOfSwitches][numberOfHosts];
		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfSwitches; countColumn++){
				SwitchSwitchGraph[countRow][countColumn] = 0;
				SwitchSwitchLinks[countRow][countColumn] = null;
			}
		}
		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfHosts; countColumn++){
				SwitchHostGraph[countRow][countColumn] = 0;
				SwitchHostLinks[countRow][countColumn] = null;
			}
		}

		
	}
	
	public void createRandomNetwork(int noSwitches, int noHosts, int baseBW){
		
		createNetworkWithoutLinks(noSwitches, noHosts);
		System.out.println("Random Network will have "+ numberOfHosts + " hosts and " + numberOfSwitches + " switchces"); 
		
		Random rand = new Random();

		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfSwitches; countColumn++){
				if(SwitchSwitchGraph[countColumn][countRow] == 1 && SwitchSwitchGraph[countRow][countColumn] == 1)
					continue;
				if(rand.nextFloat() >= 0.5f){
					SwitchSwitchGraph[countRow][countColumn] = 1;
					SwitchSwitchGraph[countColumn][countRow] = 1;
					Link tempLink = new Link();
					SwitchSwitchLinks[countRow][countColumn] = tempLink; 
					SwitchSwitchLinks[countColumn][countRow] = tempLink; 
				}
			}
		}

		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfHosts; countColumn++){
				if(rand.nextFloat() >= 0.5f){
					SwitchHostGraph[countRow][countColumn] = 1;
					Link tempLink = new Link();
					SwitchHostLinks[countRow][countColumn] = tempLink; 
				}
			}
		}

	}
	

	public void createFatTreeNetwork(int fatdegree, int baseBW){
		if(fatdegree%2 != 0){
			System.out.println("Error: Fat degree must be multiple of 2");
			return;
		}
		int coreswitchno = (fatdegree / 2) * (fatdegree / 2);
		int aggrswitchno = coreswitchno * 2;
		int edgeswitchno = aggrswitchno;
		int hostnodeno = edgeswitchno * (fatdegree / 2);
		createNetworkWithoutLinks(coreswitchno+aggrswitchno+edgeswitchno, hostnodeno);
		System.out.println("Fat tree with degree " + fatdegree + " will have "+ numberOfHosts + " hosts and " + numberOfSwitches + " switchces"); 
		System.out.println("It will have " + coreswitchno + " core switches, "+ aggrswitchno + " aggregate swithces and " + edgeswitchno + " edge switches.");


		for(int countedgeswitch = 0; countedgeswitch < edgeswitchno; countedgeswitch++){
				for(int counthost = 0; counthost < fatdegree/2; counthost++){
					SwitchHostGraph[coreswitchno + aggrswitchno + countedgeswitch][(countedgeswitch * fatdegree/2) + counthost] = 1 * baseBW;
				}
		}

		for(int countaggrswitch = 0; countaggrswitch < aggrswitchno; countaggrswitch++){
			for(int countaggr = 0; countaggr < fatdegree/2; countaggr++){
				SwitchSwitchGraph[coreswitchno + countaggrswitch][coreswitchno + aggrswitchno + countaggrswitch + countaggr - countaggrswitch % (fatdegree / 2 )] = fatdegree / 2 * baseBW;
				SwitchSwitchGraph[coreswitchno + aggrswitchno + countaggrswitch + countaggr - countaggrswitch % (fatdegree / 2 )][coreswitchno + countaggrswitch] = fatdegree / 2 * baseBW;
			}
		}
		
		for(int countaggrswitch = 0; countaggrswitch < aggrswitchno; countaggrswitch++){
			for(int countcore = 0; countcore < fatdegree/2; countcore++){
				SwitchSwitchGraph[coreswitchno + countaggrswitch][countcore + (fatdegree / 2 ) * (countaggrswitch%(fatdegree / 2))] = fatdegree / 2 * baseBW;
				SwitchSwitchGraph[countcore + (fatdegree / 2 ) * (countaggrswitch%(fatdegree / 2))][coreswitchno + countaggrswitch] = fatdegree / 2 * baseBW;
			}
		}
		


	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	
//	
//	public void runNetwork(){
//		for(int countSwitches = 1; countSwitches <= switches.size(); countSwitches++)
//			switches.get(countSwitches - 1).runSwitch();
//		for(int countHosts = 1; countHosts <= hosts.size(); countHosts++)
//			hosts.get(countHosts - 1).runHost();
//		for(int countLinkSwitchSwitch = 1; countLinkSwitchSwitch <= linksSwitchSwitch.size(); countLinkSwitchSwitch++)
//			linksSwitchSwitch.get(countLinkSwitchSwitch - 1).runLinkSwitchSwitch();
//		for(int countLinkSwitchHost= 1; countLinkSwitchHost <= linksSwitchHost.size(); countLinkSwitchHost++)
//			linksSwitchHost.get(countLinkSwitchHost- 1).runLinkSwitchHost();
//	}
//
//
//	
//	public void printNetworkTopology(){
//		System.out.println("\nSwitch Information");
//		for(int countSwitches = 1; countSwitches <= switches.size(); countSwitches++){
//			System.out.println("Switch No: " + switches.get(countSwitches - 1).switch_number_);
//			System.out.println("\tConnections");
//			for(int countHosts = 1; countHosts<= switches.get(countSwitches - 1).linksSwitchHost.size(); countHosts++){
//				System.out.print("\tHost No: " + switches.get(countSwitches - 1).linksSwitchHost.get(countHosts - 1).host_.host_number_);
//				System.out.println(" Via Link No: " + switches.get(countSwitches - 1).linksSwitchHost.get(countHosts - 1).link_number_);
//			}
//			for(int countSwitchesInner= 1; countSwitchesInner <= switches.get(countSwitches - 1).linksSwitchSwitch.size(); countSwitchesInner++){
//				
//				if(switches.get(countSwitches - 1).linksSwitchSwitch.get(countSwitchesInner - 1).switch1_.switch_number_ ==
//						switches.get(countSwitches - 1).switch_number_)
//					System.out.print("\tSwitch No: " + switches.get(countSwitches - 1).linksSwitchSwitch.get(countSwitchesInner - 1).switch2_.switch_number_);
//				else
//					System.out.print("\tSwitch No: " + switches.get(countSwitches - 1).linksSwitchSwitch.get(countSwitchesInner - 1).switch1_.switch_number_);
//				System.out.println(" Via Link No: " + switches.get(countSwitches - 1).linksSwitchSwitch.get(countSwitchesInner - 1).link_number_);
//			}
//		}
//		System.out.println("\nHost Information");
//		for(int countHosts = 1; countHosts <= hosts.size(); countHosts++){
//			System.out.println("Host No: " + hosts.get(countHosts - 1).host_number_);
//			System.out.println("\tConnections");
//			for(int countSwitches = 1; countSwitches <= hosts.get(countHosts - 1).linksSwitchHost.size(); countSwitches++){
//				System.out.print("\tSwitch No: " + hosts.get(countHosts - 1).linksSwitchHost.get(countSwitches - 1).switch_.switch_number_);
//				System.out.println(" Via Link No: " + hosts.get(countHosts - 1).linksSwitchHost.get(countSwitches - 1).link_number_);
//				
//			}
//		}
//		System.out.println("\nLink Information");
//		for(int countLinkSwitchHost = 1; countLinkSwitchHost <= linksSwitchHost.size(); countLinkSwitchHost++){
//			System.out.println("LinkSwitchHost No: " + linksSwitchHost.get(countLinkSwitchHost - 1).link_number_);
//			System.out.println("\tConnections");
//			System.out.print("\tSwitch No: " + linksSwitchHost.get(countLinkSwitchHost - 1).switch_.switch_number_);
//			System.out.println(" To Host No: " + linksSwitchHost.get(countLinkSwitchHost - 1).host_.host_number_);
//		}
//		for(int countLinkSwitchSwitch = 1; countLinkSwitchSwitch <= linksSwitchSwitch.size(); countLinkSwitchSwitch++){
//			System.out.println("LinkSwitchSwitch No: " + linksSwitchSwitch.get(countLinkSwitchSwitch - 1).link_number_);
//			System.out.println("\tConnections");
//			System.out.print("\tSwitch No: " + linksSwitchSwitch.get(countLinkSwitchSwitch - 1).switch1_.switch_number_);
//			System.out.println(" To Switch No: " + linksSwitchSwitch.get(countLinkSwitchSwitch - 1).switch2_.switch_number_);
//		}
//
//		
//		
//	}
//	public void createLineareNetwork(int lengthOfNetwork){
//		for(int count = 1; count <= lengthOfNetwork; count++){
//			LinkSwitchHost tempLinkSwitchHost = new LinkSwitchHost(count);
//			Switch tempSwitch = new Switch(count);
//			tempSwitch.linksSwitchHost.add(tempLinkSwitchHost);
//			if(count > 1 && switches.size() > 0){
//				LinkSwitchSwitch tempLinkSwitchSwitch = new LinkSwitchSwitch(count - 1);
//				tempLinkSwitchSwitch.switch1_ = tempSwitch;
//				tempLinkSwitchSwitch.switch2_ = switches.get(switches.size() - 1);
//				tempSwitch.linksSwitchSwitch.add(tempLinkSwitchSwitch);
//				switches.get(switches.size() - 1).linksSwitchSwitch.add(tempLinkSwitchSwitch);
//				linksSwitchSwitch.add(tempLinkSwitchSwitch);
//			}
//			Host tempHost = new Host(count);
//			tempHost.linksSwitchHost.add(tempLinkSwitchHost);
//			tempLinkSwitchHost.host_  = tempHost;
//			tempLinkSwitchHost.switch_ = tempSwitch;
//			linksSwitchHost.add(tempLinkSwitchHost);
//			switches.add(tempSwitch);
//			hosts.add(tempHost);
//		}
//	}
	
	
	//List<List<String>> myList = new ArrayList<>();

	
//	public void readNetworkGraph(){
//		
//	}
	
//	public void createFatTreeNetwork(int fatdegree, int basebw){
//		if(fatdegree%2 == 0){
//			System.out.println("Error: Fat degree must be multiple of 2");
//			return;
//		}
//    int coreswitchno = (fatdegree / 2) * (fatdegree / 2);
//    int aggrswitchno = coreswitchno * 2;
//    int edgeswitchno = aggrswitchno;
//    int hostnodeno = edgeswitchno * (fatdegree / 2);
//    ArrayList<Switch> coreswitchlist = new ArrayList<Switch>();
//    ArrayList<Switch> aggrswitchlist = new ArrayList<Switch>();
//    ArrayList<Switch> edgeswitchlist= new ArrayList<Switch>();
//    ArrayList<Host> hostnodelist = new ArrayList<Host>();
//    for( int count = 0; count < coreswitchno ; count++){
//    	Switch tempSwitch = new Switch(count);
//		switches.add(tempSwitch);
//		coreswitchlist.add(tempSwitch);
//    }
//    for( int count = 0; count < aggrswitchno ; count++){
//    	Switch tempSwitch = new Switch(count);
//    	switches.add(tempSwitch);
//    	aggrswitchlist.add(tempSwitch);
//    }
//    for( int count = 0; count < edgeswitchno ; count++){
//    	Switch tempSwitch = new Switch(count);
//    	switches.add(tempSwitch);
//    	edgeswitchlist.add(tempSwitch);
//    }
//
//    for( int count = 0; count < hostnodeno ; count++){
//    	Host tempHost = new Host(count);
//    	hostnodelist.add(tempHost);
//    	hosts.add(tempHost);
//    }
//    
//    for (int edgeswitchcount  = 0; edgeswitchcount < edgeswitchno; edgeswitchcount++){
//    	for (int hostcount = 0; hostcount < fatdegree/2; hostcount++){
//    		LinkSwitchHost tempLinkSwitchHost = new LinkSwitchHost(linksSwitchHost.size());
//    		tempLinkSwitchHost.switch_ = edgeswitchlist.get(edgeswitchcount);
//    		tempLinkSwitchHost.host_ = hostnodelist.get(hostcount);
////    		tempSwitch.linksSwitchSwitch.add(tempLinkSwitchSwitch);
////    		switches.get(switches.size() - 1).linksSwitchSwitch.add(tempLinkSwitchSwitch);
//    		linksSwitchHost.add(tempLinkSwitchHost);
//
//
//    		//G.add_edge("h"+str((fatdegree/2) * edgeswitchcount + hostcount), "es"+str(edgeswitchcount), weight=1)
//    	}	
//    }		




//    for count in range(aggrswitchno):
//        G.add_node("as"+str(count), weight=3)
//    for count in range(edgeswitchno):
//        G.add_node("es"+str(count), weight=4)
//    for count in range(hostnodeno):
//        G.add_node("h"+str(count), weight=1)

		

//def draw_fattree(fatdegree,basebw):
//    if fatdegree % 2 != 0:
//        print "coreswitch should be multiple of 2";
//        return;
//    fatdegree = fatdegree;
//    coreswitchno = (fatdegree / 2) * (fatdegree / 2);
//    aggrswitchno = coreswitchno * 2
//    edgeswitchno = aggrswitchno
//    hostnodeno = edgeswitchno * (fatdegree / 2)
//    basebw = basebw;
//    coreswitchlist = []
//    aggrswitchlist = []
//    edgeswitchlist = []
//    hostnodelist = []
//    G=nx.Graph()    
//        
//    for count in range(coreswitchno):
//        G.add_node("cs"+str(count), weight=2)
//    for count in range(aggrswitchno):
//        G.add_node("as"+str(count), weight=3)
//    for count in range(edgeswitchno):
//        G.add_node("es"+str(count), weight=4)
//    for count in range(hostnodeno):
//        G.add_node("h"+str(count), weight=1)
//
//
//    for edgeswitchcount in range(0, edgeswitchno):
//        for hostcount in range(0, fatdegree/2):
//            G.add_edge("h"+str((fatdegree/2) * edgeswitchcount + hostcount), "es"+str(edgeswitchcount), weight=1)
//
//
//    for aggrswitchcount in range(0, aggrswitchno):
//        for fatdegreecount in range(0, fatdegree/2):
//            G.add_edge("as"+str(aggrswitchcount), "es"+str(aggrswitchcount + fatdegreecount - aggrswitchcount % (fatdegree/2)), weight=2)
//
//    for aggrswitchcount in range(0, aggrswitchno):
//        for coreswitchcount in range(0, fatdegree/2):
//            G.add_edge("as"+str(aggrswitchcount), "cs"+str((fatdegree/2) * (aggrswitchcount%(fatdegree/2)) + coreswitchcount), weight=2)

			
}
