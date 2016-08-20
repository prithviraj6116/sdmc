package g1.a1;

import java.util.ArrayList;

public class Switch {

	/**
	 * @param args
	 */
	
	public int switch_number_;
	public ArrayList<OpenFlowRule> OF_Rules = new ArrayList<OpenFlowRule>();

	public Switch (int switch_number){
		switch_number_ = switch_number;
	}

	
	
	public void sendpacket(int src_ip, int dest_ip, String data){
		System.out.println("Packet received at Switch no " + switch_number_  + " src_ip = " + src_ip + " and dest_ip = " + dest_ip);
		for(int countOF = 0 ; countOF < OF_Rules.size(); countOF++){
			OpenFlowRule of = OF_Rules.get(countOF);
			if(of.IP_SRC == src_ip && of.IP_DEST == dest_ip){
				for(int countports = 0 ; countports < of.outports.size(); countports++){
					//Sdmc.Switches[of.outports.get(countports)].send_packet(src_ip, dest_ip, data);
					//System.out.println("switch_number"+ switch_number_ + " : " + of.outports.get(countports));
					Packet packet_ = new Packet(src_ip, dest_ip, data);
					Sdmc.SwitchSwitchLinks[switch_number_][of.outports.get(countports)].putonlink(packet_);
				}
			}
			if(of.IP_SRC == Configuration_Parameters.Host_Forward_unicast && of.IP_DEST == dest_ip){
				for(int countporthosts = 0 ; countporthosts < of.outports_hosts.size(); countporthosts++){
					//Sdmc.Hosts[of.outports_hosts.get(countporthosts)].receive_packet(src_ip, dest_ip, data);
					Packet packet_ = new Packet(src_ip, dest_ip, data);
					Sdmc.SwitchHostLinks[switch_number_][of.outports_hosts.get(countporthosts)].putonlink(packet_);
				}
			}
			if(of.IP_SRC == Configuration_Parameters.Host_Forward_multicast && of.IP_DEST == dest_ip){
				for(int countporthosts = 0 ; countporthosts < of.outports_hosts.size(); countporthosts++){
					//Sdmc.Hosts[of.outports_hosts.get(countporthosts)].receive_packet(src_ip, dest_ip, data);
					Packet packet_ = new Packet(src_ip, dest_ip, data);
					Sdmc.SwitchHostLinks[switch_number_][of.outports_hosts.get(countporthosts)].putonlink(packet_);
				}
			}

			
		}
	}

	public void run(){
		System.out.println("Running Switch No " + switch_number_);
		for(int countlink = 0; countlink < Sdmc.numberOfSwitches; countlink++){
			if(Sdmc.SwitchSwitchLinks[countlink][switch_number_] != null){
					Link lk = Sdmc.SwitchSwitchLinks[countlink][switch_number_];
					for(int countpackets = 0; countpackets < lk.outpackets.size(); countpackets++){
						Packet pk = lk.outpackets.get(countpackets);
						sendpacket(pk.SRC_IP, pk.DST_IP, pk.data);
					}
			}
		}

		for(int countlink = 0; countlink < Sdmc.numberOfHosts; countlink++){
			if(Sdmc.HostSwitchLinks[countlink][switch_number_] != null){
				Link lk = Sdmc.HostSwitchLinks[countlink][switch_number_];
				for(int countpackets = 0; countpackets < lk.outpackets.size(); countpackets++){
					Packet pk = lk.outpackets.get(countpackets);
					sendpacket(pk.SRC_IP, pk.DST_IP, pk.data);
				}
			}
		}

	}
	
	
	
//	public void send_packet(int src_ip, int dest_ip, String data){
//		System.out.println("Packet received at Switch no " + switch_number_  + " src_ip = " + src_ip + " and dest_ip = " + dest_ip);
//		for(int countOF = 0 ; countOF < OF_Rules.size(); countOF++){
//			OpenFlowRule of = OF_Rules.get(countOF);
//			if(of.IP_SRC == src_ip && of.IP_DEST == dest_ip){
//				for(int countports = 0 ; countports < of.outports.size(); countports++){
//					//Sdmc.Switches[of.outports.get(countports)].send_packet(src_ip, dest_ip, data);
//					System.out.println("switch_number"+ switch_number_ + " : " + of.outports.get(countports));
//					Link link_ = Sdmc.SwitchSwitchLinks[switch_number_][of.outports.get(countports)];
//					Packet packet_ = new Packet(src_ip, dest_ip, data);
//					link_.putonlink(packet_);
//				}
//			}
//			if(of.IP_SRC == 10000 && of.IP_DEST == dest_ip){
//				for(int countporthosts = 0 ; countporthosts < of.outports_hosts.size(); countporthosts++){
//					//Sdmc.Hosts[of.outports_hosts.get(countporthosts)].receive_packet(src_ip, dest_ip, data);
//					Link link_ = Sdmc.SwitchHostLinks[switch_number_][of.outports_hosts.get(countporthosts)];
//					Packet packet_ = new Packet(src_ip, dest_ip, data);
//					link_.putonlink(packet_);
//				}
//			}
//		}
//	}
	
	public void printOFRules(){
		System.out.println("OF Rules for the Switch no " + switch_number_);
		for(int countOF = 0; countOF < OF_Rules.size(); countOF++){
			System.out.println(" OF Rules [" + countOF + "]");
			OpenFlowRule of =  OF_Rules.get(countOF);
			System.out.println("  SrcIP:"+ of.IP_SRC);
			System.out.println("  DestIP:"+ of.IP_DEST);
			System.out.println("  OutputPort: ");
			for(int countPorts = 0; countPorts < of.outports.size(); countPorts++){
				System.out.print("    " + of.outports.get(countPorts));
			}
			System.out.println();
			System.out.println("  OutputPortHosts: ");
			for(int countPorts = 0; countPorts < of.outports_hosts.size(); countPorts++){
				System.out.print("    " + of.outports_hosts.get(countPorts));
			}

			System.out.println();
		}
	}

}
