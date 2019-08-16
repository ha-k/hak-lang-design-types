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
import java.util.HashSet;

import hlt.language.design.kernel.ParameterStack;

/**
 * This is the mother class of all types denoting collections. Types deriving
 * from this class consist of collection types whose elements are of any base
 * type (including collection types - thus enabling higher-order collections -
 * and type parameters - thus enabling polymorphic collections).
 */

public abstract class CollectionType extends ConstructedType implements Collection
{
  /**
   * This is the type of one the collection's elements.
   */
  protected Type _baseType;

  /**
   * Returns a new collection type of same kind with the specified base type.
   */
  public abstract CollectionType newCollectionType (Type baseType);

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

  /**
   * Returns the undereferenced base type.
   */
  public final Type baseTypeRef ()
    {
      return _baseType;
    }

  /**
   * Returns the base type's value; that is, the current type binding of the
   * collection's elements.
   */
  public final Type baseType ()
    {
      return _baseType.value();
    }

  /**
   * Returns <tt>true</tt> iff this collection type's base type is polymorphic.
   */
  public final boolean isPolymorphic ()
    {
      return baseType().isPolymorphic();
    }

  public final Type flatten ()
    {
      _baseType = baseType().flatten();
      return this;
    } 

  public final Type copy (HashMap parameters)
    {
      return newCollectionType(baseType().copy(parameters));
    }

  public final Type instantiate (HashMap substitution)
    {
      return newCollectionType(baseType().instantiate(substitution));
    }

  /**
   * Unifies this collection type against the specifed type in the context of the given type checker.
   */
  public final void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      if (type.kind() == PARAMETER)
        {
          type.unify(this,typeChecker);
          return;
        }
      
      if (type instanceof CollectionType && kind() == type.kind())
        {
          baseType().unify(((CollectionType)type).baseType(),typeChecker);
          if (baseType().isVoid())
            typeChecker.error(new TypingErrorException("void collection base type"));
          return;
        }

      typeChecker.error(new TypeClashException(this,type));
    }

  public final boolean unify (Type type)
    {
      if ((type = type.findValue()) == this)
        return true;

      if (type.kind() == PARAMETER)
        {
          ((TypeParameter)type).bind(this);
          return true;
        }
      
      if (type instanceof CollectionType && kind() == type.kind())
        return baseType().unify(((CollectionType)type).baseType());

      return false;
    }

  public final void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
      baseType().checkOccurrence(parameter,context,typeChecker);
    }

  public final HashSet getParameters (HashSet set)
    {
      return baseType().getParameters(set);
    }

  public final int eqCode ()
    {
      return kind() + baseType().eqCode();
    }

  /**
   * Returns true iff this type is structurally isomorphic to the
   * specified type. Type parameters that occur in all types will be
   * considered equal only if they are identical objects (i.e., same pointers).
   */
  public final boolean isEqualTo (Type type)
    {
      return this == type
          || kind() == type.kind()
             && baseType().isEqualTo(((CollectionType)type).baseType());
    }

  /**
   * Returns true iff this type is structurally isomorphic to the
   * specified type, under the renaming of type parameters specified
   * by the given HashMap.
   */
  public final boolean isEqualTo (Type type, HashMap parameters)
    {
      return this == type
          || kind() == type.kind()
             && baseType().isEqualTo(((CollectionType)type).baseType(),parameters);
    }

}
