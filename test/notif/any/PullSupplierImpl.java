/*
 * Created on 27-jun-2005
 *
 */
//package test.any;

import org.omg.CORBA.Any;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyComm.InvalidEventType;
//import org.omg.CosNotifyComm.PushConsumerPOA;
import org.omg.CosNotifyComm.PullSupplierPOA;

/**
 * @author caceres
 *
 */
public class PullSupplierImpl extends PullSupplierPOA
{
    ORB orb;
    String proc_name;
    

    /**
     * @param orb
     */
    public PullSupplierImpl(ORB orb, String proc_name)
    {
        this.orb = orb;
        this.proc_name = proc_name;
    }
    

    public Any pull()
        throws Disconnected
    {
        System.out.println(proc_name + " pull ");
        Any event;
        event = org.omg.CORBA.ORB.init().create_any();
        event.insert_string("Hello by Pull");
        return event;
    }


    public Any try_pull(org.omg.CORBA.BooleanHolder has_event)
        throws Disconnected
    {
        System.out.println(proc_name + " try_pull ");
        Any event;
        event = org.omg.CORBA.ORB.init().create_any();
        event.insert_string("Hello by TryPull.");
        has_event.value = true;
        return event;
    }


    /* (non-Javadoc)
     * @see org.omg.CosEventComm.PushConsumerOperations#disconnect_push_consumer()
     */
    public void disconnect_pull_supplier()
    {
       
    }

    
    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.NotifySubscribeOperations#subscription_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
     */
    public void subscription_change(_EventType[] added, _EventType[] removed)
        throws InvalidEventType
    {
        // TODO Auto-generated method stub
        
    }

}
