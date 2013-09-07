/*
 * Created on 27-jun-2005
 *
 */
//package test.structured;

import org.omg.CORBA.Any;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.PushConsumerPOA;
import org.omg.CosNotifyComm.PushSupplierPOA;
import org.omg.CosNotifyComm.StructuredPushSupplierPOA;

/**
 * @author caceres
 *
 */
public class StructuredPushSupplierImpl extends StructuredPushSupplierPOA
{
    ORB orb;
    
    

    /**
     * @param orb
     */
    public StructuredPushSupplierImpl(ORB orb)
    {
        this.orb = orb;
    }
    

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.NotifySubscribeOperations#subscription_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
     */
    public void subscription_change(_EventType[] added, _EventType[] removed)
        throws InvalidEventType
    {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.StructuredPushSupplierOperations#disconnect_structured_push_supplier()
     */
    public void disconnect_structured_push_supplier() 
    {
        // TODO Auto-generated method stub
        
    }

}
