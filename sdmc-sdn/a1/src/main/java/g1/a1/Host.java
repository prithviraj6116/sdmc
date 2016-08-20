package g1.a1;

import java.util.ArrayList;

public class Host {
	
	public int host_number_;
	public ArrayList<LinkSwitchHost> linksSwitchHost = new ArrayList<LinkSwitchHost>();
	
	public Host(int host_number){
		host_number_ = host_number;
	}

	public void run(){
		System.out.println("Running Host No " + host_number_);
		
		for(int countlink = 0; countlink < Sdmc.numberOfSwitches; countlink++){
			if(Sdmc.SwitchHostLinks[countlink][host_number_] != null){
				Link lk = Sdmc.SwitchHostLinks[countlink][host_number_];
				for(int countpackets = 0; countpackets < lk.outpackets.size(); countpackets++){
					Packet pk = lk.outpackets.get(countpackets);
					System.out.println("Received packet with (src:dst:data) = " + pk.SRC_IP + " : " + pk.DST_IP + ": " + pk.data);
				}
			}
		}
	}
	
	
	
	public void receive_packet(int src_ip, int dest_ip, String data){
		System.out.println(" Data received = " + data + " at Host number" + host_number_);
	}

	
	public void send_packet(int src_ip, int dest_ip, String data){
		
		System.out.println(" Data sent = " + data );
	}


}
