//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */


import java.util.HashMap;
import hlt.language.design.kernel.ParameterStack;

public class BoxableTypeConstant extends ConstructedType
{
  private Type _type;
  private boolean _isBoxed = false;

  public final int numberOfTypeComponents ()
    {
      return 1;
    }

  public final Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      if (n == 0)
        return _type;

      throw new NoSuchTypeComponentException(this,n);
    }

  public final void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      if (n == 0)
        _type = type;
      else
        throw new NoSuchTypeComponentException(this,n);
    }

  public BoxableTypeConstant (Type type)
    {
      _type = type;
    }

  public BoxableTypeConstant (Type type, boolean flag)
    {
      _type = type.kind() != BOXABLE ? type : ((BoxableTypeConstant)type).type();
      _isBoxed = flag;
    }

  public final Type setBoxed (boolean flag)
    {
      _isBoxed = flag;
      return this;
    }

  public final byte sort ()
    {
      return _type.sort();
    }

  public final Type type ()
    {
      return _type;
    }

  public final boolean isBoxedType ()
    {
      return _isBoxed;
    }

  public final byte kind ()
    {
      return BOXABLE;
    }

  public final Type copy (HashMap parameters)
    {
      return new BoxableTypeConstant(_type,_isBoxed);
    }

  public final void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      switch (type.kind())
        {
        case BOXABLE:
          _type.unify(((BoxableTypeConstant)type).type(),typeChecker);
          return;
        case CONSTANT:
          _type.unify(type,typeChecker);
          return;
        case PARAMETER:
          type.unify(this,typeChecker);
          return;
        default:
          typeChecker.error(new TypeClashException(this,type));
        }
    }

  public final boolean unify (Type type)
    {
      if ((type = type.findValue()) == this)
        return true;

      switch (type.kind())
        {
        case PARAMETER:
          ((TypeParameter)type).bind(this);
          return true;

        case BOXABLE:
          return _type.unify(((BoxableTypeConstant)type).type());

        case CONSTANT:
          return _type.unify(type);
        }

      return false;
    }

  public final int eqCode ()
    {
      return kind() + _type.eqCode() * (_isBoxed ? 2 : 1);
    }

  /**
   * Returns true iff this type constant and the specified type
   * have equal underlying types and boxing information.
   */
  public final boolean isEqualTo (Type type)
    {
      if (this == type)
        return true;

      if (!(type instanceof BoxableTypeConstant))
        return false;

      return _type == ((BoxableTypeConstant)type).type();
      //          && _isBoxed == type.isBoxedType();
    }

  /**
   * Returns true iff this boxable type constant and the specified type
   * are equal (<tt>parameters</tt> is disregarded because no type parameters
   * may occur in a  boxable type constant.
   */
  public final boolean isEqualTo (Type type, HashMap parameters)
    {
      return isEqualTo(type);
    }

  public final String toString ()
    {
      String s = _isBoxed ? "["+_type+"]" : _type.toString();
      return s;
    }
}

