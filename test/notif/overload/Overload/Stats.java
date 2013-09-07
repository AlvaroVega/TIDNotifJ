//
// Stats.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

public class Stats
   implements org.omg.CORBA.portable.IDLEntity {

  public int requests_received;
  public int requests_discarded;
  public int requests_accepted;
  public double error_rate;

  public Stats() {
  }

  public Stats(int requests_received, int requests_discarded, int requests_accepted, double error_rate) {
    this.requests_received = requests_received;
    this.requests_discarded = requests_discarded;
    this.requests_accepted = requests_accepted;
    this.error_rate = error_rate;
  }

}
