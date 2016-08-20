package g1.a1;
import java.util.List;
import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.graph.DefaultEdge;
import java.util.ArrayList;
import java.util.Random;

public class Sdmc {

	/**
	 * @param args
	 */
	
	
	public static Link[][]  SwitchSwitchLinks;
	public static Link[][]  SwitchHostLinks;
	public static Link[][]  HostSwitchLinks;
	public static Switch[] Switches;
	public static Host[] Hosts;
	public DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);	
	
	
	public static Integer[][] SwitchSwitchGraph;
	public static Integer[][] SwitchHostGraph;
	public static Integer numberOfHosts;
	public static Integer numberOfSwitches;
	
	public static void main(String[] args) {
		System.out.println("Hello World!");
		Sdmc sdmc_ = new Sdmc();
		//sdmc_.createRandomNetwork(30,10, 1);
		sdmc_.createFatTreeNetwork(4, 1);
		//sdmc_.printNetwork();
		sdmc_.CreateRoutingTable();
		//sdmc_.printOFRules();
		sdmc_.CreateMulticastRoutingTable(18, 12, 16, 20001, 13, 1, 9);
		//sdmc_.printOFRules();
	
//		for(int countpk=0; countpk<2; countpk++){
			//String s = " Hello World" + countrun + ":" + countpk;// from host 15 of switch 19 to host 8 of switch 16";
//			String s = "Hello World Unicast Packet";
//			Packet pk = new Packet(15, 1, s);
			//Sdmc.HostSwitchLinks[15][19].putonlink(pk);
			String s = "Hello World Multicast Packet";
			Packet pk = new Packet(13, 20001, s);
			Sdmc.HostSwitchLinks[13][18].putonlink(pk);
			
			
			String s1 = "Hello World Unicast Packet";
			Packet pk1 = new Packet(13, 1, s1);
			Sdmc.HostSwitchLinks[13][18].putonlink(pk1);
			String s2 = "Hello World Unicast Packet";
			Packet pk2 = new Packet(13, 9, s2);
			Sdmc.HostSwitchLinks[13][18].putonlink(pk2);


//			}

		
		
		sdmc_.start();
		

	}
	
	public void CreateMulticastRoutingTable(int src_sw, int dest1_sw, int dest2_sw, int mid, int src_host, int dest1_host, int dest2_host){


		List<DefaultEdge> path1 =	DijkstraShortestPath.findPathBetween(directedGraph, "Switch-"+src_sw+"-", "Switch-"+dest1_sw+"-");		
		List<DefaultEdge> path2 =	DijkstraShortestPath.findPathBetween(directedGraph, "Switch-"+src_sw+"-", "Switch-"+dest2_sw+"-");		
		System.out.println(path1);
		System.out.println(path2);
		int smaller_path_size = path1.size();
		if(path2.size() < path1.size())
			smaller_path_size = path2.size();
		int path_match_length =  0;
		
		System.out.println("\n\n\nOF Rules for Common Path");
		for(int countpath = 0; countpath < smaller_path_size; countpath++){
			if( path1.get(countpath).equals(path2.get(countpath))){
				System.out.println(path1.get(countpath));
				System.out.println(path2.get(countpath));

				//System.out.println(path.get(countpath));
				DefaultEdge i = path1.get(countpath);
				int install_switch = Integer.parseInt(i.toString().split("-")[1]);
				int forwarding_switch = Integer.parseInt(i.toString().split("-")[3]);
				System.out.println("install_switch = " + install_switch);
				System.out.println("forwarding_switch = " + forwarding_switch);
				
				boolean match_found = false;
				for(int countOF = 0; countOF < Switches[install_switch].OF_Rules.size(); countOF++){
					OpenFlowRule OF_Match = Switches[install_switch].OF_Rules.get(countOF);
					if(OF_Match.IP_SRC == src_host && OF_Match.IP_DEST == mid){
						if(Switches[install_switch].OF_Rules.get(countOF) .outports.contains(forwarding_switch) == false)
							Switches[install_switch].OF_Rules.get(countOF).addOuputPortToOFRule(forwarding_switch);
						match_found = true;
						break;
					}
				}
				if(match_found == false){
					OpenFlowRule OF_Rule_ = new OpenFlowRule(src_host, mid, forwarding_switch);
					Switches[install_switch].OF_Rules.add(OF_Rule_);
				}
			}
			else{
				System.out.println("\n\n\nOF Rules for Common Switch");
				DefaultEdge i1 = path1.get(countpath);
				DefaultEdge i2 = path2.get(countpath);
				int install_switch = Integer.parseInt(i1.toString().split("-")[1]);
				int forwarding_switch1 = Integer.parseInt(i1.toString().split("-")[3]);
				int forwarding_switch2 = Integer.parseInt(i2.toString().split("-")[3]);
				System.out.println("install_switch = " + install_switch);
				System.out.println("forwarding_switch1 = " + forwarding_switch1);
				System.out.println("forwarding_switch2 = " + forwarding_switch2);

				boolean match_found = false;
				for(int countOF = 0; countOF < Switches[install_switch].OF_Rules.size(); countOF++){
					OpenFlowRule OF_Match = Switches[install_switch].OF_Rules.get(countOF);
					if(OF_Match.IP_SRC == src_host && OF_Match.IP_DEST == mid){
						if(Switches[install_switch].OF_Rules.get(countOF).outports.contains(forwarding_switch1) == false)
							Switches[install_switch].OF_Rules.get(countOF).addOuputPortToOFRule(forwarding_switch1);
						if(Switches[install_switch].OF_Rules.get(countOF).outports.contains(forwarding_switch2) == false)
							Switches[install_switch].OF_Rules.get(countOF).addOuputPortToOFRule(forwarding_switch2);
						match_found = true;
						break;
					}
				}
				if(match_found == false){
					OpenFlowRule OF_Rule_ = new OpenFlowRule(src_host, mid, forwarding_switch1);
					OF_Rule_.addOuputPortToOFRule(forwarding_switch2);
					Switches[install_switch].OF_Rules.add(OF_Rule_);
				}
				path_match_length = countpath;
				break;
			}
		}
		System.out.println("\n\n\nOF Rules for Non-Common Path from Path1");
		for(int countpath = path_match_length + 1; countpath < path1.size(); countpath++){
			System.out.println(path1.get(countpath));
			//System.out.println(path.get(countpath));
			DefaultEdge i = path1.get(countpath);
			int install_switch = Integer.parseInt(i.toString().split("-")[1]);
			int forwarding_switch = Integer.parseInt(i.toString().split("-")[3]);
			System.out.println("install_switch = " + install_switch);
			System.out.println("forwarding_switch = " + forwarding_switch);

			boolean match_found = false;
			for(int countOF = 0; countOF < Switches[install_switch].OF_Rules.size(); countOF++){
				OpenFlowRule OF_Match = Switches[install_switch].OF_Rules.get(countOF);
				if(OF_Match.IP_SRC == src_host && OF_Match.IP_DEST == mid){
					if(Switches[install_switch].OF_Rules.get(countOF).outports.contains(forwarding_switch) == false)
						Switches[install_switch].OF_Rules.get(countOF).addOuputPortToOFRule(forwarding_switch);
					match_found = true;
					break;
				}
			}
			if(match_found == false){
				OpenFlowRule OF_Rule_ = new OpenFlowRule(src_host, mid, forwarding_switch);
				Switches[install_switch].OF_Rules.add(OF_Rule_);
			}
			
			if(countpath == path1.size() - 1)
			{
				System.out.println("edge switch");
				match_found = false;
				for(int countOF = 0; countOF < Switches[forwarding_switch].OF_Rules.size(); countOF++){
					OpenFlowRule OF_Match = Switches[forwarding_switch].OF_Rules.get(countOF);
					if(OF_Match.IP_SRC == Configuration_Parameters.Host_Forward_multicast && OF_Match.IP_DEST == mid){
						if(Switches[forwarding_switch].OF_Rules.get(countOF).outports_hosts.contains(dest1_host) == false)
							Switches[forwarding_switch].OF_Rules.get(countOF).addOutputHostPortToOFRule(dest1_host);
						match_found = true;
						break;
					}
				}
				if(match_found == false){
					OpenFlowRule OF_Rule_ = new OpenFlowRule(mid, dest1_host, false);
					Switches[forwarding_switch].OF_Rules.add(OF_Rule_);
				}
			}
		}
		
		
		System.out.println("\n\n\nOF Rules for Non-Common Path from Path2");
		for(int countpath = path_match_length + 1; countpath < path2.size(); countpath++){
			System.out.println(path2.get(countpath));
			DefaultEdge i = path2.get(countpath);
			int install_switch = Integer.parseInt(i.toString().split("-")[1]);
			int forwarding_switch = Integer.parseInt(i.toString().split("-")[3]);
			System.out.println("install_switch = " + install_switch);
			System.out.println("forwarding_switch = " + forwarding_switch);

			boolean match_found = false;
			for(int countOF = 0; countOF < Switches[install_switch].OF_Rules.size(); countOF++){
				OpenFlowRule OF_Match = Switches[install_switch].OF_Rules.get(countOF);
				if(OF_Match.IP_SRC == src_host && OF_Match.IP_DEST == mid){
					if(Switches[install_switch].OF_Rules.get(countOF).outports.contains(forwarding_switch) == false)
						Switches[install_switch].OF_Rules.get(countOF).addOuputPortToOFRule(forwarding_switch);
					match_found = true;
					break;
				}
			}
			if(match_found == false){
				OpenFlowRule OF_Rule_ = new OpenFlowRule(src_host, mid, forwarding_switch);
				Switches[install_switch].OF_Rules.add(OF_Rule_);
			}
			
			if(countpath == path2.size() - 1)
			{
				System.out.println("edge switch");
				match_found = false;
				for(int countOF = 0; countOF < Switches[forwarding_switch].OF_Rules.size(); countOF++){
					OpenFlowRule OF_Match = Switches[forwarding_switch].OF_Rules.get(countOF);
					if(OF_Match.IP_SRC == Configuration_Parameters.Host_Forward_multicast && OF_Match.IP_DEST == mid){
						if(Switches[forwarding_switch].OF_Rules.get(countOF).outports_hosts.contains(dest2_host) == false)
							Switches[forwarding_switch].OF_Rules.get(countOF).addOutputHostPortToOFRule(dest2_host);
						match_found = true;
						break;
					}
				}
				if(match_found == false){
					OpenFlowRule OF_Rule_ = new OpenFlowRule(mid, dest2_host, false);
					Switches[forwarding_switch].OF_Rules.add(OF_Rule_);
				}
			}
		}

//
//		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
//			for(int countColumn = 0; countColumn < numberOfSwitches; countColumn++){
//				if(countColumn != countRow){
//					//System.out.println("Shorted Path between" + "Switch-"+ countRow + "Switch-" + countColumn);
//
//					//
//					for(int countpath = 0; countpath < path.size(); countpath++){
//						//System.out.println(path.get(countpath));
//						DefaultEdge i = path.get(countpath);
//						int install_switch = Integer.parseInt(i.toString().split("-")[1]);
//						int forwarding_switch = Integer.parseInt(i.toString().split("-")[3]);
//						//System.out.println("install_switch = " + install_switch);
//						//System.out.println("forwarding_switch = " + forwarding_switch);
//						for(int countSrcHost =0; countSrcHost < numberOfHosts; countSrcHost++){
//							if(SwitchHostGraph[countRow][countSrcHost] == 1){
//								for(int countDestHost =0; countDestHost < numberOfHosts; countDestHost++){
//									if(SwitchHostGraph[countColumn][countDestHost] == 1){
//										boolean match_found = false;
//										for(int countOF = 0; countOF < Switches[install_switch].OF_Rules.size(); countOF++){
//											OpenFlowRule OF_Match = Switches[install_switch].OF_Rules.get(countOF);
//											if(OF_Match.IP_SRC == countSrcHost && OF_Match.IP_DEST == countDestHost){
//												Switches[install_switch].OF_Rules.get(countOF).addOuputPortToOFRule(forwarding_switch);
//												match_found = true;
//												break;
//											}
//										}
//										if(match_found == false){
//											OpenFlowRule OF_Rule_ = new OpenFlowRule(countSrcHost, countDestHost, forwarding_switch);
//											Switches[install_switch].OF_Rules.add(OF_Rule_);
//										}
//									}
//								}
//							}
//						}
//					}
//
//				}
//
//			}
//
//		}
//
//	}
		


	}


	
	
	public void start(){
		//Switches[19].send_packet(15, 8, "Hello from host 15 of switch 19 to host 8 of switch 16");
				


		for(int countrun = 0; countrun < Configuration_Parameters.runcount; countrun++){

			System.out.println("\n\n\n\nRun Number " + countrun);
			for(int counthost = 0; counthost < numberOfHosts; counthost++)
				Hosts[counthost].run();
			

			for(int countswitch= 0; countswitch < numberOfSwitches; countswitch++)
				Switches[countswitch].run();
			for(int counthost = 0; counthost < numberOfHosts; counthost++)
				for(int countswitch= 0; countswitch < numberOfSwitches; countswitch++)
					if(HostSwitchLinks[counthost][countswitch] != null)
						HostSwitchLinks[counthost][countswitch].run();
			for(int countswitch= 0; countswitch < numberOfSwitches; countswitch++)
				for(int counthost = 0; counthost < numberOfHosts; counthost++)
					if(SwitchHostLinks[countswitch][counthost] != null)
						SwitchHostLinks[countswitch][counthost].run();
			for(int countswitch1= 0; countswitch1 < numberOfSwitches; countswitch1++)
				for(int countswitch2= 0; countswitch2 < numberOfSwitches; countswitch2++)
					if(SwitchSwitchLinks[countswitch1][countswitch2] != null)
						SwitchSwitchLinks[countswitch1][countswitch2].run();
			
		}
	}

	
	public void printOFRules(){
		for(int countRow = 0; countRow < numberOfSwitches; countRow++){
			//Switches[countRow].printOFRules();
		}
		
		Switches[18].printOFRules();
		Switches[11].printOFRules();
		Switches[2].printOFRules();
		
		Switches[5].printOFRules();
		Switches[12].printOFRules();
//		
//		
		Switches[9].printOFRules();
		Switches[16].printOFRules();
		
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
		HostSwitchLinks = new Link[numberOfHosts][numberOfSwitches];
		Switches = new Switch[numberOfSwitches];
		Hosts = new Host[numberOfHosts];
		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			Switches[countRow] = new Switch(countRow);
			for(int countColumn = 0; countColumn < numberOfSwitches; countColumn++){
				SwitchSwitchGraph[countRow][countColumn] = 0;
				SwitchSwitchLinks[countRow][countColumn] = null;
			}
		}
		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfHosts; countColumn++){
				SwitchHostGraph[countRow][countColumn] = 0;
				SwitchHostLinks[countRow][countColumn] = null;
				HostSwitchLinks[countColumn][countRow] = null;
			}
		}
		
		for(int countColumn = 0; countColumn < numberOfHosts; countColumn++){
			Hosts[countColumn] = new Host(countColumn); 
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
					SwitchSwitchLinks[countRow][countColumn] = new Link();
					SwitchSwitchLinks[countColumn][countRow] = new Link();
				}
			}
		}

		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfHosts; countColumn++){
				if(rand.nextFloat() >= 0.5f){
					SwitchHostGraph[countRow][countColumn] = 1;
					SwitchHostLinks[countRow][countColumn] = new Link();
					HostSwitchLinks[countColumn][countRow] = new Link();
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
					SwitchHostLinks[coreswitchno + aggrswitchno + countedgeswitch][(countedgeswitch * fatdegree/2) + counthost] = new Link();
					HostSwitchLinks[(countedgeswitch * fatdegree/2) + counthost][coreswitchno + aggrswitchno + countedgeswitch] = new Link();
				}
		}

		for(int countaggrswitch = 0; countaggrswitch < aggrswitchno; countaggrswitch++){
			for(int countaggr = 0; countaggr < fatdegree/2; countaggr++){
				SwitchSwitchGraph[coreswitchno + countaggrswitch][coreswitchno + aggrswitchno + countaggrswitch + countaggr - countaggrswitch % (fatdegree / 2 )] = fatdegree / 2 * baseBW;
				SwitchSwitchLinks[coreswitchno + countaggrswitch][coreswitchno + aggrswitchno + countaggrswitch + countaggr - countaggrswitch % (fatdegree / 2 )] = new Link();
				SwitchSwitchGraph[coreswitchno + aggrswitchno + countaggrswitch + countaggr - countaggrswitch % (fatdegree / 2 )][coreswitchno + countaggrswitch] = fatdegree / 2 * baseBW;
				SwitchSwitchLinks[coreswitchno + aggrswitchno + countaggrswitch + countaggr - countaggrswitch % (fatdegree / 2 )][coreswitchno + countaggrswitch] = new Link();				
			}
		}
		
		for(int countaggrswitch = 0; countaggrswitch < aggrswitchno; countaggrswitch++){
			for(int countcore = 0; countcore < fatdegree/2; countcore++){
				SwitchSwitchGraph[coreswitchno + countaggrswitch][countcore + (fatdegree / 2 ) * (countaggrswitch%(fatdegree / 2))] = fatdegree / 2 * baseBW;
				SwitchSwitchLinks[coreswitchno + countaggrswitch][countcore + (fatdegree / 2 ) * (countaggrswitch%(fatdegree / 2))] = new Link();
				SwitchSwitchGraph[countcore + (fatdegree / 2 ) * (countaggrswitch%(fatdegree / 2))][coreswitchno + countaggrswitch] = fatdegree / 2 * baseBW;
				SwitchSwitchLinks[countcore + (fatdegree / 2 ) * (countaggrswitch%(fatdegree / 2))][coreswitchno + countaggrswitch] = new Link();
			}
		}
		


	}
	
	public void CreateRoutingTable(){

		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			directedGraph.addVertex("Switch-" + countRow + "-");
		}
		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfSwitches; countColumn++){
				if(SwitchSwitchGraph[countRow][countColumn] != 0 ){
					directedGraph.addEdge("Switch-" + countRow + "-", "Switch-" + countColumn + "-");
				}
			}
		}
		
		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfHosts; countColumn++){
				if(SwitchHostGraph[countRow][countColumn] == 1){
					OpenFlowRule OF_Rule_ = new OpenFlowRule(countColumn, countColumn, true); //unicast rule
					Switches[countRow].OF_Rules.add(OF_Rule_);
				}
			}
		}

		for(int countRow= 0; countRow < numberOfSwitches; countRow++){
			for(int countColumn = 0; countColumn < numberOfSwitches; countColumn++){
				if(countColumn != countRow){
					//System.out.println("Shorted Path between" + "Switch-"+ countRow + "Switch-" + countColumn);
					List<DefaultEdge> path =	DijkstraShortestPath.findPathBetween(directedGraph, "Switch-"+countRow+"-", "Switch-"+countColumn+"-");
					//System.out.println(path);
					for(int countpath = 0; countpath < path.size(); countpath++){
						//System.out.println(path.get(countpath));
						DefaultEdge i = path.get(countpath);
						int install_switch = Integer.parseInt(i.toString().split("-")[1]);
						int forwarding_switch = Integer.parseInt(i.toString().split("-")[3]);
						//System.out.println("install_switch = " + install_switch);
						//System.out.println("forwarding_switch = " + forwarding_switch);
						for(int countSrcHost =0; countSrcHost < numberOfHosts; countSrcHost++){
							if(SwitchHostGraph[countRow][countSrcHost] == 1){
								for(int countDestHost =0; countDestHost < numberOfHosts; countDestHost++){
									if(SwitchHostGraph[countColumn][countDestHost] == 1){
										boolean match_found = false;
										for(int countOF = 0; countOF < Switches[install_switch].OF_Rules.size(); countOF++){
											OpenFlowRule OF_Match = Switches[install_switch].OF_Rules.get(countOF);
											if(OF_Match.IP_SRC == countSrcHost && OF_Match.IP_DEST == countDestHost){
												if(Switches[install_switch].OF_Rules.get(countOF).outports.contains(forwarding_switch) == false)
													Switches[install_switch].OF_Rules.get(countOF).addOuputPortToOFRule(forwarding_switch);
												match_found = true;
												break;
											}
										}
										if(match_found == false){
											OpenFlowRule OF_Rule_ = new OpenFlowRule(countSrcHost, countDestHost, forwarding_switch);
											Switches[install_switch].OF_Rules.add(OF_Rule_);
										}
									}
								}
							}
						}
					}
				}
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
