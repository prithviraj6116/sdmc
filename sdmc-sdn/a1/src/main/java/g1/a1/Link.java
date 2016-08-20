package g1.a1;

import java.util.ArrayList;

public class Link {
	public ArrayList<Packet> inpackets = new ArrayList<Packet>();
	public ArrayList<Packet> outpackets = new ArrayList<Packet>();
	
	public void putonlink(Packet p){
		if(inpackets.size() == Configuration_Parameters.link_BW){
			inpackets.remove(0);
		}
		inpackets.add(p);
	}

	public void run(){
		outpackets.clear();
		int link_size = Configuration_Parameters.link_BW;
		if(inpackets.size() < link_size)
			link_size = inpackets.size();
		for(int countpackets = 0; countpackets < link_size; countpackets++ ){
			outpackets.add(inpackets.get(countpackets));
		}
		inpackets.clear();
	}

}
