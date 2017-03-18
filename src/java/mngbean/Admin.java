/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mngbean;

import java.io.Serializable;
import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;

import pojos.Guests;
import pojos.Users;
import pojos.Treatment;
import pojos.Event;

/**
 *
 * @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu
 * This is a developer, beta version, without comments and documentation!  Please, read it cautiously :-)
 */
@ManagedBean
@SessionScoped
public class Admin implements Serializable{

    private Users user;
    Session session;
    private Treatment treatment;
    private Guests guest;
    private List<Treatment> treatments = new ArrayList<>();
    private List<Guests> guests = new ArrayList<>();
    private String searchString = null;
    private String treSearch = null;
    private boolean editable;
    private boolean deleting;
    private Event cure;
    private List<Event> events = new ArrayList<>();
    private Date date;
    private String[] selectedNames = null;
    private List<String> names = null;
    private String name = "";
    private String[] selectedTypes = null;
    private List<String> types = null;
    private String type = "";
    DefaultScheduleModel dsm = new DefaultScheduleModel();

    public Admin() {
    }

    public String login(String name, String password) {
        String valasz = "";
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Users WHERE username=:par1 AND password=:par2");
        query.setString("par1", name);
        query.setString("par2", password);
        Users getuser = (Users) query.uniqueResult();
        session.close();
        if (getuser != null && getuser.getUsername().equalsIgnoreCase(name) && getuser.getPassword().equalsIgnoreCase(password)) {
            user = getuser;
            events = getEvents(user);
            guests = getGuests(user);
            treatments = getTreatments(user);
            valasz = "admin/admin";
        } else {
            valasz = "error";

        }
        return valasz;
    }

    public void outLogger() {
        this.type = null;
        this.name = null;
        this.guest = null;
        this.treatment = null;
        this.date = null;
        this.searchString = "";
        this.treSearch = "";
        this.dsm.clear();
    }

    public String logout() {
        this.user = null;
        this.guest = null;
        this.treatment = null;
        this.date = null;
        this.searchString = "";
        this.treSearch = "";
        this.type = null;
        this.name = null;
        this.events.clear();
        this.guests.clear();
        this.treatments.clear();
        this.types.clear();
        this.names.clear();
        this.selectedNames = null;
        this.selectedTypes = null;
        this.dsm.clear();
        return "index";
    }

    public String saveUser(String name, String username, String password, String email) {
        if (!name.isEmpty() && !username.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
            Users newUser = new Users(name, password, username, email);
            session = hibernate.HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.saveOrUpdate(newUser);
            session.getTransaction().commit();
            session.close();
            user = newUser;
            return "admin/admin";
        }
        return "error";
    }

    public List<Guests> getGuests(Users user) {
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Guests WHERE fkey=:par1");
        query.setString("par1", (user.getId() + ""));
        guests = query.list();
        session.close();
        return guests;
    }

    public void saveGuest(Users user, String name, String phone, String email, Boolean pest, Boolean buda, Boolean rendszeres, Boolean alkalmi) {
        Guests newGuest = new Guests();
        newGuest.setUsers(user);
        newGuest.setName(name);
        newGuest.setPhone(phone);
        newGuest.setEmail(email);
        newGuest.setPest(pest);
        newGuest.setBuda(buda);
        newGuest.setRendszeres(rendszeres);
        newGuest.setAlkalmi(alkalmi);
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(newGuest);
        session.getTransaction().commit();
        session.close();
        guests.add(newGuest);
    }

    public void editGuest(String name, String phone, String email, Boolean pest, Boolean buda, Boolean rendszeres, Boolean alkalmi) {
        guest.setName(name);
        guest.setPhone(phone);
        guest.setEmail(email);
        guest.setPest(pest);
        guest.setBuda(buda);
        guest.setRendszeres(rendszeres);
        guest.setAlkalmi(alkalmi);
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(guest);
        session.getTransaction().commit();
        session.close();
    }

    public void deleteGuest(Guests guest) {
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(guest);
        session.getTransaction().commit();
        session.close();
        guests.remove(guest);
    }

    public List<Guests> nameSearch(Users user, String searchString) {
        this.searchString = searchString;
        this.user = user;
        guests = getGuests(user);
        Collections.sort(guests, new Comparator<Guests>() {
            @Override
            public int compare(final Guests object1, final Guests object2) {
                Collator col = Collator.getInstance(new Locale("hu", "HU"));
                return col.compare(object1.getName().toLowerCase(), object2.getName().toLowerCase());
            }
        });
        if (searchString != null) {
            Iterator<Guests> names = guests.iterator();
            while (names.hasNext()) {
                String namesLc = names.next().getName().toLowerCase();
                if (!namesLc.startsWith(searchString)) {
                    names.remove();
                }
            }
        }
        return guests;
    }

    public List<String> getNames() {
        List<String> namesList = new ArrayList<>();
        for (Guests g : guests) {
            namesList.add(g.getName());
        }
        names = namesList;
        return names;
    }

    public String getName() {
        if (selectedNames != null) {
            name = selectedNames[0];
        } else {
            name = "No guest";
        }
        return name;
    }

    public String pestiE() {
        if (guest.getPest()) {
            return "Pest";
        }
        return "Buda";
    }

    private List<Treatment> getTreatments(Users user) {
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Treatment WHERE fkeyuser=:par1");
        query.setString("par1", (user.getId() + ""));
        treatments = query.list();
        session.close();
        return treatments;
    }

    public Treatment getTreatment(String type) {
        for (Treatment t : treatments) {
            if (t.getType().equalsIgnoreCase(type)) {
                t = treatment;
            }
        }
        return treatment;
    }

    public void saveTreatment(Users user, String type, String price, String time) {

        Treatment newTreatment = new Treatment();
        newTreatment.setUsers(user);
        newTreatment.setType(type);
        newTreatment.setPrice(Integer.parseInt(price));
        newTreatment.setTime(Integer.parseInt(time));
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(newTreatment);
        session.getTransaction().commit();
        session.close();
        treatments.add(newTreatment);
    }

    public void editTreatment(String type, String price, String time) {
        treatment.setType(type);
        treatment.setPrice(Integer.parseInt(price));
        treatment.setTime(Integer.parseInt(time));
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(treatment);
        session.getTransaction().commit();
        session.close();
    }

    public void deleteTreatment(Treatment treatment) {
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(treatment);
        session.getTransaction().commit();
        session.close();
        treatments.remove(treatment);
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public List<Treatment> treatmentSearch(Users user, String treSearch) {
        this.treSearch = treSearch;
        this.user = user;
        treatments = getTreatments(user);
        Collections.sort(treatments, new Comparator<Treatment>() {
            @Override
            public int compare(final Treatment object1, final Treatment object2) {
                Collator col = Collator.getInstance(new Locale("hu", "HU"));
                return col.compare(object1.getType().toLowerCase(), object2.getType().toLowerCase());
            }
        });
        if (treSearch != null) {
            Iterator<Treatment> types = treatments.iterator();
            while (types.hasNext()) {
                String typesLc = types.next().getType().toLowerCase();
                if (!typesLc.startsWith(treSearch)) {
                    types.remove();
                }
            }
        }
        return treatments;
    }

    public List<Event> getEvents(Users user) {
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("FROM Event WHERE fkeyuser=:par1");
        query.setInteger("par1", user.getId());
        events = query.list();
        session.close();
        getScheduleModel();
        return events;
    }

    public void saveEvent(Guests guest, Treatment treatment, Users users, String date) {
        Event newEvent = new Event();
        newEvent.setGuests(guest);
        newEvent.setTreatment(treatment);
        newEvent.setUsers(users);
        newEvent.setDate(date);
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(newEvent);
        session.getTransaction().commit();
        session.close();
        events.add(newEvent);
    }

    public void saveEvent(Event event) {
        session = hibernate.HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.saveOrUpdate(event);
        session.getTransaction().commit();
        session.close();
        events.add(event);
    }

    public void saveThisEvent(Users user, String name, String type, Date date) {
        Guests gue = null;
        Treatment tre = null;
        for (Guests g : guests) {
            if (g.getName().equalsIgnoreCase(name)) {
                gue = g;
            }
        }
        for (Treatment t : treatments) {
            if (t.getType().equalsIgnoreCase(type)) {
                tre = t;
            }
        }
        for(Event e : events){
            if(!e.getDate().equalsIgnoreCase(date.toString())){
                Event eve = new Event(gue, tre, user, date.toString());
                saveEvent(eve);
            }
        }
    }

    public DefaultScheduleModel getScheduleModel() {
        dsm.clear();
        DateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US);
        if (events == null) {
            dsm.clear();
        } else {
            Date dateStart = null ;
                for (int i = 0; i < events.size(); i++) {    
                    try {
                        dateStart = df.parse(events.get(i).getDate());
                    } catch (ParseException ex) {
                        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    DefaultScheduleEvent dse = new DefaultScheduleEvent();
                    Date dateEnd = dateEnd(dateStart, events.get(i).getTreatment());
                    dse.setTitle(eventTitle(events.get(i).getGuests(), events.get(i).getTreatment()));
                    dse.setStartDate(dateStart);
                    dse.setEndDate(dateEnd);
                    dsm.addEvent(dse);
                    }               
        }        
        return dsm;
    }

//    public DefaultScheduleEvent getScheduleEvent(){
//        DefaultScheduleEvent scheduleEvent = dsm.getEvent(name);
//        
//        return scheduleEvent;
//    }
//    
    public List<String> getTypes() {
        List<String> typeList = new ArrayList<>();
        for (Treatment t : treatments) {
            typeList.add(t.getType());
        }
        types = typeList;
        return types;
    }

    public String getType() {
        if (selectedTypes != null) {
            type = selectedTypes[0];
        } else {
            type = "No treatment";
        }
        return type;
    }

    public String eventTitle(Guests guest, Treatment treatment) {
        return guest.getName() + ", " + treatment.getType();
    }

    public Date dateEnd(Date date, Treatment treatment) {
        long start = date.getTime();
        long time = treatment.getTime() * 60000;
        long end = start + time;
        Date dateEnd = new Date(end);
        return dateEnd;
    }
//    public Date thisDate(String date){
//       
//        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//        Date parsedDate = null;
//        try {
//            parsedDate = df.parse(date);
//        } catch (ParseException ex) {
//            System.out.println(ex);
//        }
//       return parsedDate;
//        }
//    
//    public DefaultScheduleEvent getScheduleEvent(){
//        DefaultScheduleEvent dse = new DefaultScheduleEvent
//                        (eventTitle(guest, treatment),
//                         dateStart(date),
//                         dateEnd(date, treatment));
//        dsm.addEvent(dse);
//        return dse;
//    }

    public void setGuests(List<Guests> guests) {
        this.guests = guests;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public List<Guests> getGuests() {
        return guests;
    }

    public boolean isDeleting() {
        return deleting;
    }

    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }

    public List<Treatment> getTreatments() {
        return treatments;
    }

    public void setTreatments(List<Treatment> treatments) {
        this.treatments = treatments;
    }

    public Event getEvent() {
        return cure;
    }

    public void setEvent(Event event) {
        this.cure = event;
    }

    public String getTreSearch() {
        return treSearch;
    }

    public void setTreSearch(String treSearch) {
        this.treSearch = treSearch;
    }

    public Date getDate() {

        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String[] getSelectedNames() {
        return selectedNames;
    }

    public void setSelectedNames(String[] selectedNames) {
        this.selectedNames = selectedNames;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String[] getSelectedTypes() {
        return selectedTypes;
    }

    public void setSelectedTypes(String[] selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Guests getGuest() {

        return guest;
    }

    public void setGuest(Guests guest) {
        this.guest = guest;

    }

    public Treatment getTreatment() {
        return treatment;
    }
}
