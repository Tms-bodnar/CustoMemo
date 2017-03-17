/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mngbean;

/**
 *
 * @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
 
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
 
@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {
 
    private ScheduleModel eventModel;
    
    private ScheduleEvent event;
 
    @PostConstruct
    public void init() {
        
    }

    public ScheduleModel getEventModel(ScheduleModel eventModel) {
         this.eventModel = eventModel;
         System.out.println(this.eventModel.getEventCount());
         List<ScheduleEvent> events = new ArrayList<>();
         events = this.eventModel.getEvents();
         for(int i = 0; i < events.size();i++){
             events.get(i).setId(i+"");
             this.eventModel.updateEvent(events.get(i));
         }
        return this.eventModel;
    }
    
    
    
    public ScheduleEvent getEvent() {
        return event;
    }
 
    public ScheduleModel setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
        return this.eventModel;
    }
    
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
     
    public void addEvent(ActionEvent actionEvent) {
        if(event.getId() == null)
            eventModel.addEvent(event);
            
        else
            eventModel.updateEvent(event);
    }
     
    public String getGuest(ScheduleEvent event){
        if(event!= null){
        String title = event.getTitle();
        String guestName = title.substring(0, title.indexOf(','));
        return guestName;
        }else return "";
    }
    
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
       
    }
     
    public void onDateSelect(SelectEvent selectEvent) {
            event = new DefaultScheduleEvent();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "No booking!", null);
        addMessage(message);
    }
     
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
         
        addMessage(message);
    }
     
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}