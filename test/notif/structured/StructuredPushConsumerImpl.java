/*
 * Created on 27-jun-2005
 *
 */
//package test.structured;

import org.omg.CORBA.Any;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.PushConsumerPOA;
import org.omg.CosNotifyComm.StructuredPushConsumerPOA;

/**
 * @author caceres
 *
 */
public class StructuredPushConsumerImpl extends StructuredPushConsumerPOA
{
    ORB orb;
    
    

    /**
     * @param orb
     */
    public StructuredPushConsumerImpl(ORB orb)
    {
        this.orb = orb;
    }
    /* (non-Javadoc)
     * @see org.omg.CosEventComm.PushConsumerOperations#push(org.omg.CORBA.Any)
     */

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.NotifyPublishOperations#offer_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
     */
    public void offer_change(_EventType[] added, _EventType[] removed)
        throws InvalidEventType
    {
        throw new NO_IMPLEMENT();
    }
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyComm.StructuredPushConsumerOperations#push_structured_event(org.omg.CosNotification.StructuredEvent)
	 */
	public void push_structured_event(StructuredEvent event ) throws Disconnected {
		System.out.println( "Received message " + event.filterable_data[ 0 ].value.extract_long() );
		
	}
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyComm.StructuredPushConsumerOperations#disconnect_structured_push_consumer()
	 */
	public void disconnect_structured_push_consumer() {
		System.out.println( "disconnect_structured_push_consumer" );
	}

}
