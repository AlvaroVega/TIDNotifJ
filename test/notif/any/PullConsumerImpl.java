/*
 * Created on 18-jun-2008
 * 
 */

import org.omg.CORBA.Any;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.PullConsumerPOA;

/**
 * @author avega
 *
 */
public class PullConsumerImpl extends PullConsumerPOA
{
    ORB orb;
    
    

    /**
     * @param orb
     */
    public PullConsumerImpl(ORB orb)
    {
        this.orb = orb;
    }
//     /* (non-Javadoc)
//      * @see org.omg.CosEventComm.PullConsumerOperations#push(org.omg.CORBA.Any)
//      */
//     public void push(Any data)
//         throws Disconnected
//     {
//         System.out.println("received: " + data.extract_string());    

//     }

    /* (non-Javadoc)
     * @see org.omg.CosEventComm.PullConsumerOperations#disconnect_push_consumer()
     */
    public void disconnect_pull_consumer()
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
