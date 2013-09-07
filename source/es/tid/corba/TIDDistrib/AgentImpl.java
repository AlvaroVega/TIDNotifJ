/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 3 $
* Date: $Date: 2006-01-17 17:42:13 +0100 (Tue, 17 Jan 2006) $
* Last modified by: $Author: aarranz $
*
* (C) Copyright 2005 Telefónica Investigación y Desarrollo
*     S.A.Unipersonal (Telefónica I+D)
*
* Info about members and contributors of the MORFEO project
* is available at:
*
*   http://www.morfeo-project.org/TIDNotifJ/CREDITS
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
*   http://www.morfeo-project.org/TIDNotifJ/Licensing
*/ 

package es.tid.corba.TIDDistrib;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;

import es.tid.corba.TIDDistribAdmin.AgentPackage.CannotProceed;

public class AgentImpl extends es.tid.corba.TIDDistribAdmin.AgentPOA implements AgentMsg
{
  /**
   * The current orb.
   */
  private org.omg.CORBA.ORB _orb;

  /**
   * Agents Service Manager library.
   */
  ServiceManager _manager;

  AgentImpl( org.omg.CORBA.ORB orb, ServiceManager manager )
  {
    _orb = orb;
    _manager  = manager;
  }

/*
  public void start() throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, START);
    _manager.start();
  }

  public void stop() throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, STOP);
    _manager.stop();
  }

  public void resume() throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, RESUME);
    _manager.resume();
  }
*/

  public void trace_on(es.tid.corba.TIDDistribAdmin.TraceLevel level)
                                                           throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, TRACE_ON);
    _manager.trace_on(level);
  }

  public void trace_off() throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, TRACE_OFF);
    _manager.trace_off();
  }

  public org.omg.DistributionChannelAdmin.DistributionChannelFactory
                                         channel_factory() throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, CHANNEL_FACTORY);
    return _manager.channel_factory();
  }

  // Returns a list with the channels.
  public org.omg.DistributionChannelAdmin.DistributionChannel[] 
                                           list_channels() throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, LIST_CHANNELS);
    return _manager.list_channels();
  }

  public void shutdown_service() throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, SHUTDOWN);
    _manager.shutdown();
    _orb.shutdown(false);
  }

  public void shutdown() throws CannotProceed
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, SHUTDOWN);
    _manager.shutdown();
    _orb.shutdown(false);
  }
} // End of public class AgentImpl
