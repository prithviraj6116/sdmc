package sdmc;

import java.util.ArrayList;

public class Switch {

	/**
	 * @param args
	 */
	
	public int switch_number_;
	
	public ArrayList<LinkSwitchHost> linksSwitchHost = new ArrayList<LinkSwitchHost>();
	public ArrayList<LinkSwitchSwitch> linksSwitchSwitch = new ArrayList<LinkSwitchSwitch>();

	public Switch (int switch_number){
		switch_number_ = switch_number;
	}
	
	public void runSwitch(){
		System.out.println("Running Switch No " + switch_number_);
		
	}

}
