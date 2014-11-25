/* ApplicationForLeave.java

	Purpose:
		
	Description:
		
	History:
		2013/6/27, Dennis

Copyright (C) 2010 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zss.jsfdemo;

import java.io.*;
import java.net.URL;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.zkoss.zss.api.*;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.api.model.Sheet;
import org.zkoss.zss.jsf.Action;
import org.zkoss.zss.jsf.ActionBridge;

@ManagedBean
@RequestScoped
public class ApplicationForLeave {

	/*
	 * the book of spreadsheet
	 */
	private Book book;
	
	/*
	 * the bridge to execute action in ZK context
	 */
	private ActionBridge actionBridge;

	public Book getBook() {
		if (book != null) {
			return book;
		}
		try {
			URL bookUrl = FacesContext.getCurrentInstance()
					.getExternalContext()
					.getResource("/WEB-INF/books/application_for_leave.xlsx");
			book = Importers.getImporter().imports(bookUrl, "app4leave");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		Sheet sheet = book.getSheetAt(0);

		// reset sample data
		// you can use a cell reference to get a range
		Range from = Ranges.range(sheet, "E5");// Ranges.range(sheet,"From");
		// or you can use a name to get a range (the named range has to be set in book);
		Range to = Ranges.rangeByName(sheet, "To");
		Range reason = Ranges.rangeByName(sheet, "Reason");
		Range applicant = Ranges.rangeByName(sheet, "Applicant");
		Range requestDate = Ranges.rangeByName(sheet, "RequestDate");

		// use range api to set the cell data
		from.getCellData().setValue(DateUtil.tomorrowDate(0));
		to.getCellData().setValue(DateUtil.tomorrowDate(0));
		reason.setCellEditText("");
		applicant.setCellEditText("");
		requestDate.getCellData().setValue(DateUtil.todayDate());

		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}
	
	public ActionBridge getActionBridge() {
		return actionBridge;
	}

	public void setActionBridge(ActionBridge actionBridge) {
		this.actionBridge = actionBridge;
	}

	public void doReset() {
		
		//use actionBridge to execute the action inside ZK context
		//so the spreadsheet can get the update of book automatically
		actionBridge.execute(new Action() {
			public void execute() {
				Sheet sheet = book.getSheetAt(0);

				// reset sample data
				// you can use a cell reference to get a range
				Range from = Ranges.range(sheet, "E5");// Ranges.range(sheet,"From");
				// or you can use a name to get a range (the named range has to be
				// set in Excel);
				Range to = Ranges.rangeByName(sheet, "To");
				Range reason = Ranges.rangeByName(sheet, "Reason");
				Range applicant = Ranges.rangeByName(sheet, "Applicant");
				Range requestDate = Ranges.rangeByName(sheet, "RequestDate");

				// use range API to set the cell data
				from.getCellData().setValue(DateUtil.tomorrowDate(0));
				to.getCellData().setValue(DateUtil.tomorrowDate(0));
				reason.setCellEditText("");
				applicant.setCellEditText("");
				requestDate.getCellData().setValue(DateUtil.todayDate());
			}
		});
		addMessage("Reset book");
	}
	
	private void addMessage(String message){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(message));
	}

	public void doOk() {
		//access cell data
		actionBridge.execute(new Action() {
			public void execute() {
				Sheet sheet = book.getSheetAt(0);

				Date from = Ranges.rangeByName(sheet,"From").getCellData().getDateValue();
				Date to = Ranges.rangeByName(sheet,"To").getCellData().getDateValue();
				String reason = Ranges.rangeByName(sheet,"Reason").getCellData().getStringValue();
				Double total = Ranges.rangeByName(sheet,"Total").getCellData().getDoubleValue();
				String applicant = Ranges.rangeByName(sheet,"Applicant").getCellData().getStringValue();
				Date requestDate = Ranges.rangeByName(sheet,"RequestDate").getCellData().getDateValue();
				
				//validate input
				if(from == null){
					addMessage("FROM is not a correct date");
				}else if(to == null){
					addMessage("TO is not a correct date");
				}else if(total==null || total.intValue()<0){
					addMessage("TOTAL small than 1");
				}else if(reason == null){
					addMessage("REASON is empty");
				}else if(applicant == null){
					addMessage("APPLICANT is empty");
				}else if(requestDate == null){
					addMessage("REQUEST DATE is empty");
				}else{
					//Handle your business logic here 
					
					addMessage("Your request are sent, following is your data");

					addMessage("From :" +from);
					addMessage("To :" + to);
					addMessage("Reason :"+ reason);
					addMessage("Total :"+ total.intValue());//we only need int
					addMessage("Applicant :"+ applicant);
					addMessage("RequestDate :"+ requestDate.getTime());
					
					//You can also store the book, and load it back later by exporting it to a file
					Exporter exporter = Exporters.getExporter();
					FileOutputStream fos = null;
					try {
						File temp = File.createTempFile("app4leave_", ".xlsx");
						fos = new FileOutputStream(temp); 
						exporter.export(sheet.getBook(), fos);
						System.out.println("file save at "+temp.getAbsolutePath());
						
						addMessage("Archive "+ temp.getName());
					} catch (IOException e) {
						e.printStackTrace();
					} finally{
						if(fos!=null)
							try {
								fos.close();
							} catch (IOException e) {
								//handle the exception
							}
					}
				}
			}
		});
	}
}
