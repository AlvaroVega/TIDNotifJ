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

package es.tid.corba.TIDNotif;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.omg.CORBA.Any;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterAdminOperations;
import org.omg.CosNotifyFilter.FilterNotFound;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;

/**
 * @author jprojas
 *
 */
public class FilterAdminImpl implements FilterAdminOperations, Serializable {
	
	private SortedMap filters;

	public FilterAdminImpl(){
		this.filters = Collections.synchronizedSortedMap( new TreeMap() );
	}
	
	/**************************************************************************
	 * PRIVATE METHODS 
	 *************************************************************************/
	private Integer nextKey(){
		Integer lastKey, nextKey;
		if ( this.filters.isEmpty() ) {
			nextKey = new Integer( 0 );
		} else {
			lastKey = (Integer)this.filters.lastKey();
			nextKey = new Integer( lastKey.intValue() + 1 );
		}
		return nextKey;
	}//nextKey
	
	/**************************************************************************
	 * METHODS INHERITED FROM java.io.Serializable
	 *************************************************************************/

	/**************************************************************************
	 * METHODS INHERITED FROM org.omg.CosNotifyFilter.FilterAdminOperations
	 *************************************************************************/
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#add_filter(org.omg.CosNotifyFilter.Filter)
	 */
	public int add_filter(Filter new_filter) {
		Integer nextKey; 
		nextKey = this.nextKey();
		this.filters.put( nextKey, new_filter );
		return nextKey.intValue();
	}//add_filter

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#remove_filter(int)
	 */
	public void remove_filter(int filter) throws FilterNotFound {
		Filter existingFilter;
		existingFilter = ( Filter )this.filters.remove( new Integer( filter ) );
		if ( existingFilter == null ){
			throw new FilterNotFound();
		}
	}//remove_filter

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_filter(int)
	 */
	public Filter get_filter(int filter) throws FilterNotFound {
		Filter existingFilter;
		existingFilter = ( Filter )this.filters.get( new Integer( filter ) );
		if ( existingFilter == null ){
			throw new FilterNotFound();
		} 
		return existingFilter;
	}//get_filter

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_all_filters()
	 */
	public int[] get_all_filters() {
		int[]	  keyInts;
		Integer[] keyIntegers;
		keyIntegers = (Integer[])this.filters.keySet().toArray( new Integer[0] );
		keyInts = new int[ keyIntegers.length ];
		for ( int i=0; i < keyIntegers.length; i++ ){
			keyInts[ i ] = keyIntegers[ i ].intValue();
		}//for
		return keyInts; 
	}//get_all_filters

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#remove_all_filters()
	 */
	public void remove_all_filters() {
		this.filters.clear();
	}

    /**
     * @param event
     * @return
     */
    public boolean match(Any event)
    	throws UnsupportedFilterableData
    {       
		Iterator iterator = filters.values().iterator();
		Filter filter = null;
		while(iterator.hasNext()) {
		    filter = (Filter) iterator.next();
		    if(!filter.match(event)) {
		        return false;
		    }
		}
		
		return true;
    }
    
}
