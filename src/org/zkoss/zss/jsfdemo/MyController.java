package org.zkoss.zss.jsfdemo;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zss.ui.event.StopEditingEvent;

@ManagedBean(name= "myController")
@SessionScoped
public class MyController extends SelectorComposer<Component>{
	
	private static final long serialVersionUID = 1L;


	@Listen("onStopEditing = spreadsheet")
    public void onStopEditing(StopEditingEvent event){
		Clients.showNotification("edited");
	
	}
	
	
}
