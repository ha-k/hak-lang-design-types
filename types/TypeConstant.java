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

public class TypeConstant extends NamedType
{
  protected byte _sort = OBJECT_SORT;
  protected boolean _isBoxedType = true;

  /**
   * <a name="shadowType"></a>
   * This is used in <a href="../kernel/Homomorphism.html#typeChecking">
   * <tt>Homomorphism</tt></a> for typing irregular monoid operations in
   * the absence of a real mechanism for subtyping.
   */
  private Type _shadowType = this;

  public final Type shadowType ()
    {
      return _shadowType;
    }

  public final void setShadowType (Type type)
    {
      _shadowType = type;
    }

  public int numberOfTypeComponents ()
    {
      return _shadowType == this ? 0 : 1;
    }

  public Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      if (n == 0 && _shadowType != this)
        return _shadowType;

      throw new NoSuchTypeComponentException(this,n);
    }

  public void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      if (n == 0 && _shadowType != this)
        _shadowType = type;
      else
        throw new NoSuchTypeComponentException(this,n);
    }

  public TypeConstant (String name)
    {
      _name = name.intern();
    }

  public TypeConstant (String name, byte sort)
    {
      this(name);
      _sort = sort;
    }

  public TypeConstant (String name, byte sort, boolean isBoxedType)
    {
      this(name,sort);
      _isBoxedType = isBoxedType;
    }

  public byte kind ()
    {
      return CONSTANT;
    }

  public final byte sort ()
    {
      return _sort;
    }

  public final boolean isBoxedType ()
    {
      return _isBoxedType;
    }

  public void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      switch (type.kind())
        {
        case PARAMETER:
          type.unify(this,typeChecker);
          return;
        case BOXABLE:
          unify(((BoxableTypeConstant)type).type(),typeChecker);
          return;
//          case CONSTANT:
//            if (TypeChecker.ALLOWS_LIBERAL_NUMBER_TYPING
//                && (this == INT && type == REAL || this == REAL && type == INT))
//              return;
        default:
          typeChecker.error(new TypeClashException(this,type));
        }
    }

  public boolean unify (Type type)
    {
      if ((type = type.findValue()) == this)
        return true;

      switch (type.kind())
        {
        case PARAMETER:
          ((TypeParameter)type).bind(this);
          return true;
        case BOXABLE:
          return unify(((BoxableTypeConstant)type).type());
//          case CONSTANT:
//            return TypeChecker.ALLOWS_LIBERAL_NUMBER_TYPING
//                && (this == INT && type == REAL || this == REAL && type == INT);
        }

      return false;
    }

  /**
   * Returns true iff this type constant and the specified type
   * are identical objects (<i>i.e.</i>, same pointers).
   */
  public final boolean isEqualTo (Type type)
    {
      return (type == this);
    }

  /**
   * Returns true iff this type constant and the specified type
   * are identical objects (<i>i.e.</i>, same pointers).
   */
  public final boolean isEqualTo (Type type, HashMap parameters)
    {
      return isEqualTo(type);
    }

  public final String toString ()
    {
      return _name;
    }

}
