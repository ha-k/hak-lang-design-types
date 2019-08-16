//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Sun Oct 21 16:49:06 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.HashMap;
import hlt.language.tools.Misc;

/**
 * All the methods redefined here are inherited from Type and are
 * disabled in this class extension of <a
 * href="Type.html"><tt>Type</tt></a>.
 *
 * <p>
 *
 * <i><b>Note to myself:</b> This class does not make any sense and BTW
 * it is not used anywhere! For it to be of any utility, it should be
 * extended by the <tt>Type</tt> class and used as a superclass of all
 * classes that extend <tt>Type</tt> instead of <tt>Type</tt> (exactly
 * in the same way the class <tt>ProtoExpression</tt> is defined as as
 * subclass of <tt>Expression</tt> and extended by expression classes
 * rather than <tt>Expression</tt>.</i>
 */
public abstract class ProtoType extends Type
{
  public int numberOfTypeComponents ()
    {
      throw new UnsupportedOperationException("numberOfTypeComponents() in "+
                                              Misc.simpleClassName(this));
    }

  public Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      throw new UnsupportedOperationException("typeRefComponent(int) in "+
                                              Misc.simpleClassName(this));
    }

  public void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      throw new UnsupportedOperationException("setTypeRefComponent(int,Type) in "+
                                              Misc.simpleClassName(this));
    }

  public Type baseTypeRef ()
    {
      throw new UnsupportedOperationException("baseTypeRef() in "+
                                              Misc.simpleClassName(this));
    }

  public Type baseType ()
    {
      throw new UnsupportedOperationException("baseType() in "+
                                              Misc.simpleClassName(this));
    }

  public boolean isPolymorphic ()
    {
      throw new UnsupportedOperationException("isPolymorphic() in "+
                                              Misc.simpleClassName(this));
    }

//    public Type value ()
//      {
//        throw new UnsupportedOperationException("value() in "+
//                                                Misc.simpleClassName(this));
//      }

  public Type findValue ()
    {
      throw new UnsupportedOperationException("findValue() in "+
                                              Misc.simpleClassName(this));
    }

  public byte sort ()
    {
      throw new UnsupportedOperationException("sort() in "+
                                              Misc.simpleClassName(this));
    }

  public boolean isBoxedType ()
    {
      throw new UnsupportedOperationException("isBoxedType() in "+
                                              Misc.simpleClassName(this));
    }

  public Type setBoxed (boolean flag)
    {
      throw new UnsupportedOperationException("setBoxed(boolean) in "+
                                              Misc.simpleClassName(this));
    }

  public byte kind ()
    {
      throw new UnsupportedOperationException("kind() in "+
                                              Misc.simpleClassName(this));
    }

  public void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      throw new UnsupportedOperationException("unify(Type,TypeChecker) in "+
                                              Misc.simpleClassName(this));
    }

  public boolean unify (Type type)
    {
      throw new UnsupportedOperationException("unify(Type) in "+
                                              Misc.simpleClassName(this));
    }

  public void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
      throw new UnsupportedOperationException("checkOccurrence(parameter,Type,TypeChecker) in "+
                                              Misc.simpleClassName(this));
    }

  public void curry (int depth, TypeChecker typeChecker)
    {
      throw new UnsupportedOperationException("curry(int,TypeChecker) in "+
                                              Misc.simpleClassName(this));
    }

  public Type flatten ()
    {
      throw new UnsupportedOperationException("flatten() in "+
                                              Misc.simpleClassName(this));
    } 

  public Type copy (HashMap parameters)
    {
      throw new UnsupportedOperationException("copy(HashMap) in "+
                                              Misc.simpleClassName(this));
    }

  public Type copy ()
    {
      throw new UnsupportedOperationException("copy() in "+
                                              Misc.simpleClassName(this));
    }
  
  public Type instantiate (HashMap substitution)
    {
      throw new UnsupportedOperationException("instantiate(HashMap) in "+
                                              Misc.simpleClassName(this));
    }

  public int eqCode ()
    {
      throw new UnsupportedOperationException("eqCode() in "+
                                              Misc.simpleClassName(this));
    }

  public boolean equals (Object object)
    {
      throw new UnsupportedOperationException("equals(Object) in "+
                                              Misc.simpleClassName(this));
    }

  public boolean isEqualTo (Type type)
    {
      throw new UnsupportedOperationException("isEqualTo(Type) in "+
                                              Misc.simpleClassName(this));
    }

  public boolean isEqualTo (Type type, HashMap parameters)
    {
      throw new UnsupportedOperationException("isEqualTo(Type,HashMap) in "+
                                              Misc.simpleClassName(this));
    }
}

