#TRACE_DIR=/export/home/alvaro/e-force/work/CORBAServices/NotificationService_TID/es/tid/corba/TIDNotif
TRACE_DIR=.

OUT_DIR=./OUT

if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  comprobar.ksh <num.test>"
  echo
  exit 0
fi

echo "Eventos generados:" `grep ">> Request Time:" $OUT_DIR/supplier.$1.out | wc -l`
#echo

echo "Eventos recibidos por el canal:" `grep ProxyPushConsumerImpl.push $TRACE_DIR/tidnotif.out | wc -l`
#echo

echo "Eventos encolados en el canal:" `grep "ProxyPushConsumerImpl.ipush(event)" $TRACE_DIR/tidnotif.out | wc -l`
#echo

echo "Eventos tratados por el canal (SupplierAdmin):" `grep SupplierAdminImpl.push_event $TRACE_DIR/tidnotif.out | wc -l`
#echo

echo "Eventos tratados por el canal (NotificationChannel):" `grep NotificationChannelImpl.dispatchEvent $TRACE_DIR/tidnotif.out | wc -l`
#echo

echo "Eventos tratados por el canal (ConsumerAdmin):" `grep ConsumerAdminImpl.dispatchEvent $TRACE_DIR/tidnotif.out | wc -l`
#echo

echo "Eventos entregados al ProxyPushSupplier:" `grep ProxyPushSupplierImpl.push_event $TRACE_DIR/tidnotif.out | wc -l`
#echo

echo "Eventos encolados en el ProxyPushSupplier:" `grep "ProxyPushSupplierImpl.ipush(event)" $TRACE_DIR/tidnotif.out | wc -l`
#echo

echo "Eventos recibidos or los clientes:" `grep "Evento num." $OUT_DIR/consumer.$1.out | wc -l`
#echo

