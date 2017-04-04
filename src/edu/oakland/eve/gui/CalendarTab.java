package edu.oakland.eve.gui;

import com.google.api.services.calendar.model.CalendarListEntry;
import edu.oakland.eve.core.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

/**
 * The main window calendar tab
 * @author Michael MacLean
 */
class CalendarTab extends JPanel {
    private JTabbedPane tabs;

    CalendarTab(){
        setLayout(new BorderLayout());
        setName("Calendar");

        tabs = new JTabbedPane();
        tabs.setTabPlacement(JTabbedPane.LEFT);
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.addMouseListener(new Listener());
        add(tabs, BorderLayout.CENTER);
        updateCalendars();
    }

     void updateCalendars(){
        tabs.removeAll();
        try {
            List<CalendarListEntry> cl = Program.calendars.fetchCalendars().getItems();
            for(CalendarListEntry c : cl){
                tabs.add(new JCalendar(Program.calendars.fetchCalendar(c.getId())));
            }
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to pull calendars: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
        }
    }

    void deleteCalendar(JCalendar jCal){
         if(JOptionPane.showConfirmDialog(this, "Delete the calendar?", "EVE Calendars", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
             try {
                 Program.calendars.deleteCalendar(jCal.getCalender());
                 updateCalendars();
             }
             catch(IOException e){
                 JOptionPane.showMessageDialog(this, "Error deleting calendar: " + e.getMessage(), "EVE Calendars", JOptionPane.ERROR_MESSAGE);
             }
         }
    }


    class Listener extends MouseAdapter {
         public void mouseClicked(MouseEvent e){
             // right click
             if(e.getButton() == 3){
                 int n = tabs.getUI().tabForCoordinate(tabs, e.getX(), e.getY());
                 new ContextMenu(tabs, e.getX(), e.getY());
             }
         }
    }

    class ContextMenu extends JPopupMenu{
         ContextMenu(JTabbedPane comp, int x, int y) {
             JMenuItem addEventItem = new JMenuItem("New Event");
             JCalendar jCal = (JCalendar)comp.getComponentAt(comp.indexAtLocation(x, y));
             addEventItem.addActionListener(e -> jCal.addEvent());
             JMenuItem editItem = new JMenuItem("Edit Calendar");
             JMenuItem deleteItem = new JMenuItem("Delete Calendar");
             deleteItem.addActionListener(e -> deleteCalendar(jCal));
             add(addEventItem);
             add(editItem);
             addSeparator();
             add(deleteItem);
             show(comp, x, y);
         }
    }
}
