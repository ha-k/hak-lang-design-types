//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

/**
 * This is the class of constant types that are also collection types.
 */
public class CollectionTypeConstant extends TypeConstant implements Collection
{
  private Type _baseType;

  public final int numberOfTypeComponents ()
    {
      return 1;
    }

  public final Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      if (n == 0)
        return _baseType;

      throw new NoSuchTypeComponentException(this,n);
    }

  public final void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      if (n == 0)
        _baseType = type;
      else
        throw new NoSuchTypeComponentException(this,n);
    }

  public CollectionTypeConstant (String name, Type type)
    {
      super(name);
      _baseType = type;
    }

  public final byte kind ()
    {
      return COLLECTION;
    }

  /**
   * Returns the undereferenced base type.
   */
  public Type baseTypeRef ()
    {
      return _baseType;
    }

  /**
   * Returns the base type's value; that is, the current type binding of the
   * collection's elements.
   */
  public Type baseType ()
    {
      return _baseType.value();
    }

  public final void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      switch (type.kind())
        {
        case PARAMETER:
          type.unify(this,typeChecker);
          return;
        case COLLECTION:
          if (_name == ((CollectionTypeConstant)type).name())
            {
              ((CollectionTypeConstant)type).baseType().unify(_baseType,typeChecker);
              return;
            }
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
        case COLLECTION:
          return _name == ((CollectionTypeConstant)type).name()
              && ((CollectionTypeConstant)type).baseTypeRef().findValue().unify(_baseType);
        }

      return false;
    }

}
