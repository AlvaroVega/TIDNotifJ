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
import org.omg.CosNotifyComm.PushConsumerPOA;
import org.omg.CosNotifyComm.PushSupplierPOA;

/**
 * @author caceres
 *
 */
public class PushSupplierImpl extends PushSupplierPOA
{
    ORB orb;
    
    

    /**
     * @param orb
     */
    public PushSupplierImpl(ORB orb)
    {
        this.orb = orb;
    }
    

    /* (non-Javadoc)
     * @see org.omg.CosEventComm.PushConsumerOperations#disconnect_push_consumer()
     */
    public void disconnect_push_supplier()
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
