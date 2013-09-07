/*
 * Created on 27-jun-2005
 *
 */
//package test.property;

import org.omg.CORBA.Any;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.PushConsumerPOA;

/**
 * @author caceres
 *
 */
public class PushConsumerImpl extends PushConsumerPOA
{
    ORB orb;
    
    

    /**
     * @param orb
     */
    public PushConsumerImpl(ORB orb)
    {
        this.orb = orb;
    }
    /* (non-Javadoc)
     * @see org.omg.CosEventComm.PushConsumerOperations#push(org.omg.CORBA.Any)
     */
    public void push(Any data)
        throws Disconnected
    {
        System.out.println("received: " + data.extract_string());    

    }

    /* (non-Javadoc)
     * @see org.omg.CosEventComm.PushConsumerOperations#disconnect_push_consumer()
     */
    public void disconnect_push_consumer()
    {
        orb.shutdown(false);    

    }

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.NotifyPublishOperations#offer_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
     */
    public void offer_change(_EventType[] added, _EventType[] removed)
        throws InvalidEventType
    {
        throw new NO_IMPLEMENT();
   

    }

}
