package g1.a1;

public class LinkSwitchHost{

	public int link_number_;
	public Switch switch_;
	public Host host_;
	public LinkSwitchHost(int link_number){
		link_number_ = link_number;
	}
	
	public void runLinkSwitchHost(){
		System.out.println("Running LinkSwitchHost No " + link_number_);
		
	}
}
