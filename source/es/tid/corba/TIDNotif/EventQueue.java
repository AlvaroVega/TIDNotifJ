/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 1 $
* Date: $Date: 2008-08-11 17:42:13 +0100 (Tue, 1 Aug 2008) $
* Last modified by: $Author: avega $
*
* (C) Copyright 2005 Telefonica Investigacion y Desarrollo
*     S.A.Unipersonal (Telefonica I+D)
*
* Info about members and contributors of the MORFEO project
* is available at:
*
*   http://www.morfeo-project.org/TIDNotifJ/CREDITS
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*
* If you want to use this software an plan to distribute a
* proprietary application in any way, and you are not licensing and
* distributing your source code under GPL, you probably need to
* purchase a commercial license of the product.  More info about
* licensing options is available at:
*
*   http://www.morfeo-project.org/TIDNotifJ/Licensing
*/

package es.tid.corba.TIDNotif;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;


import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.TRANSIENT;

import org.omg.CosNotification.FifoOrder;


/**
 * Queue of events to be used by ProxyPullSupplier. 
 * 
 * @autor Alvaro Vega García
 * @version 1.0
 */
class EventQueue {

    // Naive implementation
    private TreeSet m_values = null;

    protected short m_queue_order;

    private boolean m_deactivation;

    private int m_max_queued_events;


    /**
     * Constructors
     * 
     */
    public EventQueue(int max_events,
                      Comparator comparator,
                      short order) {
        m_max_queued_events = max_events;
        m_values = new TreeSet(comparator);
        m_queue_order = order;
        m_deactivation = false;
    }
    

    public EventQueue(int max_events) {
        m_max_queued_events = max_events;
        m_values = new TreeSet(new FifoComparator());
        m_queue_order = FifoOrder.value;
        m_deactivation = false;
    }



    /**
     * Change comparator.
     * 
     */
    public synchronized void setComparator(Comparator comparator,
                                           short order)
    {
        TreeSet aux = m_values;        
        m_values = new TreeSet(comparator);
        m_values.addAll(aux);        
        m_queue_order = order;
    }


    /**
     * Adds an new request to the queue.
     * 
     * @param request The request to be added.
     */
    public synchronized void add(org.omg.CORBA.Any event) {

        if ( m_deactivation  || 
             m_values.size() >= this.getMaxQueuedEvents()) {

        } else {
            m_values.add(event);
        }
    }


    /**
     * Gets (and removes) the first element of the queue.
     * 
     * @return The first element of the queue.
     */
    public synchronized org.omg.CORBA.Any get() {

        if ( m_values.isEmpty() ) {
            if (m_deactivation) {
                return null;
            }
        }

        // Get out the first element
        org.omg.CORBA.Any event = (org.omg.CORBA.Any) m_values.first();
        

        // Remove the event from the queue
        if (m_queue_order == FifoOrder.value) {
            Iterator i = m_values.iterator();
            Object o = i.next();
            i.remove();
        } else 
            m_values.remove(event);
        
        return event;
    }


    /**
     * @return Number of enqueued elements.
     */
    public synchronized int size() {
        return m_values.size();
    }


    /**
     * Set all events to "discarding".
     */
    public synchronized void discardAll() {
        
        Iterator it  = m_values.iterator();
        while (it.hasNext()){
            org.omg.CORBA.Any event = (org.omg.CORBA.Any) it.next();
            // TODO: mark event as destroyed
        }
    }


    /**
     * The POAManager is being deactivating, notify it to all blocked threads.
     */
    synchronized void deactivation() {
        if (!m_deactivation) {
            m_deactivation = true;
            notifyAll();
        }
    }


    /**
     * @return Max number of enqueued elements.
     */
    public synchronized int getMaxQueuedEvents() {
        return m_max_queued_events;
    }


    /**
     * set the Max number of enqueued elements.
     */
    public synchronized void setMaxQueuedEvents(int max_queued_events) {
        m_max_queued_events = max_queued_events;
    }


}
