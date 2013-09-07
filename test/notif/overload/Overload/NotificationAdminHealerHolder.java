//
// NotificationAdminHealerHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

final public class NotificationAdminHealerHolder
   implements org.omg.CORBA.portable.Streamable {

  public NotificationAdminHealer value; 
  public NotificationAdminHealerHolder() {
  }

  public NotificationAdminHealerHolder(NotificationAdminHealer initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = Overload.NotificationAdminHealerHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    Overload.NotificationAdminHealerHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return Overload.NotificationAdminHealerHelper.type();
  };

}
