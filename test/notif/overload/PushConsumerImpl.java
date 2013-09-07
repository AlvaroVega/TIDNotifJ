/*
 * Morfeo Project
 * http://morfeo-project.org/
 * 
 * Copyright (C) 2008 Telefonica Investigacion y Desarrollo S.A. Unipersonal
 * 
 * Authors     : Alvaro Polo Valdenebro <apv at tid.es>
 *               Alvaro Vega Garcia <avega at tid dot es>
 * Module      : PushConsumerImpl
 * Description : Main program for client side in Overload Test
 *
 * Info about members and contributors of the MORFEO project
 * is available at:
 *
 *     http://www.morfeo-project.org/TIDorbJ/CREDITS
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
 *   http://www.morfeo-project.org/TIDorbJ/Licensing
 *
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.Any;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyComm.InvalidEventType;

import es.tid.TIDorbj.core.poa.POAManagerImpl;

import Overload.*;

public class PushConsumerImpl 
        extends HealingPushConsumerPOA
        implements POAManagerHolder
{

    private long m_start_timer;
    private long m_stop_timer;
    
    private long m_expected_requests;
    private long m_processed_requests;
    private long m_accounted_requests;
    private long m_ignored_requests;
    private double m_flood_threshold;


    ORB orb;

    POAManagerStatsExtractor m_stats_extractor;

    /**
     * @param orb
     */
    public PushConsumerImpl(ORB orb)
    {
        m_start_timer        = 0;
        m_stop_timer         = 0;
        
        m_expected_requests  = 0;
        m_processed_requests = 0;
        m_accounted_requests = 0;      
        m_ignored_requests   = 0;
        m_flood_threshold    = 0.0;

        this.orb = orb;

        m_stats_extractor = new POAManagerStatsExtractor(this);
    }

    public POAManagerImpl obtainPOAManager()
    {
        return (POAManagerImpl) _poa().the_POAManager();
    }

    /* (non-Javadoc)
     * @see org.omg.CosEventComm.PushConsumerOperations#push(org.omg.CORBA.Any)
     */
    public void push(Any data)
        throws Disconnected
    {

        Event event = new Event();
        event = EventHelper.extract(data);

        // Check if is the last message
        // then call to print statical numbers and disconnect and destroy
        if (event.bool_val) {
            Stats st = getStats();
            System.out.println("[PushConsumer] Received last message");
            // TODO
            System.out.println("[PushConsumer] Processed requests: " + 
                               st.requests_received);

        } else {
            
            request();

            compute(event);
        }

    }

    /* (non-Javadoc)
     * @see org.omg.CosEventComm.PushConsumerOperations#disconnect_push_consumer()
     */
    public void disconnect_push_consumer()
    {
        orb.shutdown(false);    
    }

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.NotifyPublishOperations#offer_change(
     *                                                org.omg.CosNotification._EventType[], 
     *                                                org.omg.CosNotification._EventType[])
     */
    public void offer_change(_EventType[] added, _EventType[] removed)
        throws InvalidEventType
    {
        throw new NO_IMPLEMENT();
    }



    //
    // Local operations
    // 

    protected static void compute(Event value)
    {
        // Do some computing
        // value.string_val = new String("Foobar string");
        for (int i = 0; i < 50000; i++)  {
            value.long_val += (int)(i * 14234.2532);
            value.long_val += (int)(i / 45.354 + 10);
        }
    }

    protected synchronized void request() {

        m_processed_requests++;
        long lower_thres = (long) (m_expected_requests * m_flood_threshold);
        long upper_thres = m_expected_requests - lower_thres;
        if (m_ignored_requests < lower_thres) {
            // In lower flood threshold
            m_ignored_requests++;
        }
        else if (m_ignored_requests + m_accounted_requests >= upper_thres) {
            // In upper flood threshold
            m_ignored_requests++;
            if (m_stop_timer == 0)
                m_stop_timer = System.currentTimeMillis();
        } 
        else {

            if (m_start_timer == 0)
                m_start_timer = System.currentTimeMillis();
         
            // When flood threshold is zero, the last request should
            // set the stop timer
            if (m_flood_threshold == 0.0 && 
                m_processed_requests == m_expected_requests)
                m_stop_timer = System.currentTimeMillis();
            
            m_accounted_requests++;
        }
    }

    /***
     * Operations for measurement
     */
    public synchronized void reset(double flood_threshold, long requests)
    {
        m_start_timer = 0;
        m_stop_timer  = 0;
        
        m_expected_requests = requests;
        m_processed_requests = 0;      
        m_accounted_requests = 0;      
        m_ignored_requests = 0;      
        m_flood_threshold = flood_threshold;
        
        System.err.println("Reset: " + requests + " reqs, " + 
                           flood_threshold * 100.0 + "% ignored (first and last " + 
                           (long) (requests * flood_threshold) + ")");
    }
    

    protected synchronized long getExpectedRequests()
    {
        return m_expected_requests;
    }
   
    protected synchronized long getProcessedRequests()
    {
        return m_processed_requests;
    }
    
    protected synchronized long getAccountedRequests()
    {
        return m_accounted_requests;
    }
    
    protected synchronized long getIgnoredRequests()
    {
        return m_ignored_requests;
    }
   
    protected synchronized long getMilliseconds()
    {
        return m_stop_timer - m_start_timer;
    }
    
    protected synchronized float getRequestsPerSecond()
    {
      return (float) (getAccountedRequests() / (getMilliseconds() / 1000.0));
    }

    public void resetStatsCounter() {
        m_stats_extractor.resetStatsCounter();
    }

    public Stats getStats() {
        return m_stats_extractor.getStats();
    }

    public Stats getLastSecondsStats(int seconds) {
        return m_stats_extractor.getLastSecondsStats(seconds);
    }

}
