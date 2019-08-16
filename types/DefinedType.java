//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.AbstractList;
import java.util.HashMap;
import java.util.HashSet;

import hlt.language.design.kernel.ParameterStack;

/**
 * This is the class of defined <i>opaque</i> types. This is a new type
 * hiding the type that defines it. In other words, it acts as a wrapper for
 * the defining type, and behaves as an entirely new type in that it will not
 * unify against anything except itself or a type parameter.
 *
 * <p>
 * 
 * Look in <a href="Tables.html"><tt>Tables</tt></a> for the methods that will
 * define a new opaque type (<tt>DefineNewType(...)</tt>).
 *
 * @see TypeDefinition
 * @see Tables
 */

public class DefinedType extends TypeTerm
{
  private Type _definition;

  public int numberOfTypeComponents ()
    {
      return 1 + arity();
    }

  public Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      if (n == 0)
        return _definition;

      if (n > 0 && n <= arity())
        return _arguments[n-1];

      throw new NoSuchTypeComponentException(this,n);
    }

  public void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      if (n == 0)
        _definition = type;
      else
        if (n > 0 && n <= arity())
          _arguments[n-1] = type;
        else
          throw new NoSuchTypeComponentException(this,n);
    }

  /**
   * Constructs a non-polymorphic defined type with the specified name and
   * definition.
   */
  public DefinedType (String name, Type definition)
    {
      _name = name.intern();
      _definition = definition.flatten();
    }

  /**
   * Constructs a polymorphic defined type with the specified name, definition,
   * and type parameters.
   */
  public DefinedType (String name, Type definition, Type[] arguments)
    {
      this(name,definition);
      setArguments(arguments);
    }

  public DefinedType (String name, Type definition, AbstractList arguments)
    {
      this(name,definition);
      setArguments(arguments);
    }

  public final Type actualType ()
    {
      Type type = definition();
      if (type.kind() != DEFINED) return type;
      return ((DefinedType)type).actualType();      
    }

  public final Type definition ()
    {
      return _definition.value();
    }

  public final DefinedType setDefinition (Type type)
    {
      _definition = type;
      return this;
    }

  public final byte kind ()
    {
      return DEFINED;
    }

  public final boolean isPolymorphic ()
    {
      return definition().isPolymorphic();
    }

  public final byte sort ()
    {
      return definition().sort();
    }

  public final boolean isBoxedType ()
    {
      return definition().isBoxedType();
    }

  public final Type setBoxed (boolean flag)
    {
      definition().setBoxed(flag);
      return this;
    }

  /**
   * Normally, a defined type can only unify against a type parameter or itself.
   * However, if the type-checking mode allows it, opaque types whose actual type
   * is a (named) tuple type will unify against similar raw tuple types. 
   */
  public final void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      switch (type.kind())
        {
        case PARAMETER:
          type.unify(this,typeChecker);
          return;

        case DEFINED:
          if (type.kind() != DEFINED || _name != ((DefinedType)type).name())
            typeChecker.error(new TypeClashException(this,type));

          definition().unify(((DefinedType)type).definition(),typeChecker);
          return;

        case TUPLE:
        case NAMED_TUPLE:
          if (TypeChecker.ALLOWS_UNIFYING_OPAQUE_TUPLES)
            {
              Type definition = actualType();
              if (definition.kind() == TUPLE || definition.kind() == NAMED_TUPLE)
                {
                  definition.unify(type,typeChecker);
                  return;
                }
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

        case DEFINED:
          return type.kind() == DEFINED
              && _name == ((DefinedType)type).name()
              && _definition.unify(((DefinedType)type).definition());

        case TUPLE:
        case NAMED_TUPLE:
          if (TypeChecker.ALLOWS_UNIFYING_OPAQUE_TUPLES)
            {
              Type definition = actualType();
              return (definition.kind() == TUPLE || definition.kind() == NAMED_TUPLE)
                  && definition.unify(type);
            }
        }

      return false;
    }

  public final void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
      definition().checkOccurrence(parameter,context,typeChecker);
    }

  public final HashSet getParameters (HashSet set)
    {
      definition().getParameters(set);
      return set;
    }

  public final void curry (int depth, TypeChecker typeChecker)
    {
      definition().curry(depth,typeChecker);
    }

  public final Type flatten ()
    {
      definition().flatten();
      return this;
    } 

  public final Type copy (HashMap parameters)
    {
      return ((DefinedType)super.copy(parameters))
        .setDefinition(definition().copy(parameters));
    }

  public final Type instantiate (HashMap substitution)
    {
      return ((DefinedType)super.instantiate(substitution))
        .setDefinition(definition().instantiate(substitution));
    }

  public final int eqCode ()
    {
      return super.hashCode() + definition().eqCode();
    }


//    public final boolean isEqualTo (Type type)
//      {
//        if (type.kind() != DEFINED)
//          return false;

//        return _name == ((DefinedType)type).name()
//            && definition().isEqualTo(((DefinedType)type).definition());
//      }

//    public final boolean isEqualTo (Type type, HashMap parameters)
//      {
//        if (type.kind() != DEFINED)
//          return false;

//        return _name == ((DefinedType)type).name()
//            && definition().isEqualTo(((DefinedType)type).definition(),parameters);
//      }

}
