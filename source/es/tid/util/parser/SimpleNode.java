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

/* Generated By:JJTree: Do not edit this line. SimpleNode.java */

package es.tid.util.parser;

public class SimpleNode extends Object implements Node, TIDConstraintMsg
{
  public int node_type = NodeType.UNKNOWN_NODE;

  protected String repository_id = null;

  protected Node parent;
  protected Node[] children;
  protected int id;
  protected ConstraintParser parser;

  public SimpleNode(int i) {
    id = i;
  }

  public SimpleNode(ConstraintParser p, int i) {
    this(i);
    parser = p;
  }

  public void jjtOpen() {
  }

  public void jjtClose() {
  }
  
  public void jjtSetParent(Node n) { parent = n; }
  public Node jjtGetParent() { return parent; }

  public void jjtAddChild(Node n, int i) {
    if (children == null) {
      children = new Node[i + 1];
    } else if (i >= children.length) {
      Node c[] = new Node[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = n;
  }

  public Node jjtGetChild(int i) {
    return children[i];
  }

  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  public String toString() { return ConstraintParserTreeConstants.jjtNodeName[id]; }
  public String toString(String prefix) { return prefix + toString(); }

  /* Override this method if you want to customize how the node dumps
     out its children. */

  public void dump(String prefix) {
    TIDParser.print(TIDParser.DEBUG, toString(prefix));
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
	SimpleNode n = (SimpleNode)children[i];
	if (n != null) {
	  n.dump(prefix + " ");
	}
      }
    }
  }

  /* Override this method if you want to customize how the node dumps
     out its children. */

  public String repositoryId() throws java.lang.NullPointerException
  {
    throw new java.lang.NullPointerException();
  }

  public TypeValuePair evaluate( org.omg.DynamicAny.DynAny dynany ) 
  {
    TIDParser.print(TIDParser.DEBUG, SN_EVALUATE);
    return new TypeValuePair();
  }

  public TypeValuePair evaluate_in( TypeValuePair data, 
                                    org.omg.DynamicAny.DynAny dynany ) 
  {
    TIDParser.print(TIDParser.DEBUG, SN_EVALUATE_IN);
    return new TypeValuePair();
  }

  public TypeValuePair check_event( org.omg.CORBA.Any event ) 
  {
    TIDParser.print(TIDParser.DEBUG, SN_CHECK_EVENT);
    return new TypeValuePair();
  }
}