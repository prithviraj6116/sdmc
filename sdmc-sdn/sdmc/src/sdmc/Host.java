package sdmc;

import java.util.ArrayList;

public class Host {
	
	public int host_number_;
	public ArrayList<LinkSwitchHost> linksSwitchHost = new ArrayList<LinkSwitchHost>();
	
	public Host(int host_number){
		host_number_ = host_number;
	}

	public void runHost(){
		System.out.println("Running Host No " + host_number_);
	}

}
