//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Sun Oct 21 00:04:06 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.AbstractList;

import hlt.language.design.kernel.ParameterStack;

public abstract class TypeTerm extends NamedType implements Cloneable
{
  protected Type[] _arguments;

  public int numberOfTypeComponents ()
    {
      return arity();
    }

  public Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      if (n >= 0 && n < arity())
        return _arguments[n];

      throw new NoSuchTypeComponentException(this,n);
    }

  public void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      if (n >= 0 && n < arity())
        _arguments[n] = type;
      else
        throw new NoSuchTypeComponentException(this,n);
    }

  public final Type[] arguments ()
    {
      return _arguments;
    }

  public final Type argument (int i)
    {
      return _arguments[i].value();
    }

  public final int arity ()
    {
      if (_arguments == null)
        return 0;
      
      return _arguments.length;
    }

  public final TypeTerm setArguments (AbstractList arguments)
    {
      if (arguments != null)
        {
          _arguments = new Type[arguments.size()];
          for (int i=_arguments.length; i-->0;)
            _arguments[i] = (Type)arguments.get(i);
        }

      return this;
    }

  public final TypeTerm setArguments (Type[] arguments)
    {
      _arguments = arguments;
      return this;
    }

  public Type flatten ()
    {
      for (int i=arity(); i-->0;)
        _arguments[i] = argument(i).flatten();

      return this;
    } 

  public boolean isPolymorphic ()
    {
      for (int i=arity(); i-->0;)
        if (argument(i).isPolymorphic()) return true;

      return false;
    }

  // Note to myself: Why then implement the Cloneable interface?
  private final TypeTerm _clone ()
    {
      TypeTerm clone = null;
      try
        {
          clone = (TypeTerm)clone();
        }
      catch (Exception e)
        {
          throw new TypeDefinitionException(e);
        }
      return clone;
    }

  public Type copy (HashMap parameters)
    {
      if (arity() == 0)
        return this;

      Type[] newArguments = new Type[arity()];

      for (int i=arity(); i-->0;)
        newArguments[i] = argument(i).copy(parameters);

      return _clone().setArguments(newArguments);
    }

  public Type instantiate (HashMap substitution)
    {
      if (arity() == 0)
        return this;

      Type[] arguments = new Type[arity()];

      for (int i=arity(); i-->0;)
        arguments[i] = argument(i).instantiate(substitution);

      return _clone().setArguments(arguments);
    }

  public int eqCode ()
    {
      int code = super.eqCode() + arity();

      for (int i=arity(); i-->0;)
        code += (i+1)*argument(i).eqCode();

      return code;
    }

  /**
   * Returns true iff this type is structurally isomorphic to the
   * specified type. Type parameters that occur in all types will be
   * considered equal only if they are identical objects (i.e., same pointers).
   */
  public final boolean isEqualTo (Type type)
    {
      if (this == type)
        return true;

      if (!(type instanceof TypeTerm))
        return false;

      TypeTerm typeTerm = (TypeTerm)type;
          
      if (kind() != typeTerm.kind() || name() != typeTerm.name() || arity() != typeTerm.arity())
        return false;

      for (int i=arity(); i-->0;)
        if (!argument(i).isEqualTo(typeTerm.argument(i)))
          return false;

      return true;
    }

  /**
   * Returns <tt>true</tt> iff this type is structurally isomorphic to
   * the specified type, under the renaming of type parameters specified
   * by the given HashMap.
   */
  public final boolean isEqualTo (Type type, HashMap parameters)
    {
      if (this == type)
        return true;

      if (!(type instanceof TypeTerm))
        return false;

      TypeTerm typeTerm = (TypeTerm)type;
          
      if (kind() != typeTerm.kind() || name() != typeTerm.name() || arity() != typeTerm.arity())
        return false;

      for (int i=arity(); i-->0;)
        if (!argument(i).isEqualTo(typeTerm.argument(i),parameters))
          return false;

      return true;
    }

  public void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      if (type.kind() == PARAMETER)
        {
          type.unify(this,typeChecker);
          return;
        }

      if (type.kind() != kind())
        typeChecker.error(new TypeClashException(this,type));

      TypeTerm typeTerm = (TypeTerm)type;

      if (typeTerm.name() != name() || typeTerm.arity() != arity())
        typeChecker.error(new TypeClashException(this,typeTerm));

      for (int i=arity(); i-->0;)
        {
          argument(i).unify(typeTerm.arguments()[i],typeChecker);
          if (argument(i).isVoid())
            typeChecker.error(new TypingErrorException("void type instantiation"));
        }
    }

  public boolean unify (Type type)
    {
      if ((type = type.findValue()) == this)
        return true;

      if (type.kind() == PARAMETER)
        {
          ((TypeParameter)type).bind(this);
          return true;
        }

      boolean ok = (type.kind() == kind());

      if (ok)
        {
          TypeTerm other = (TypeTerm)type;
          int position = arity();
          ok &= position == other.arity() && name() == other.name();

          while (ok && position-->0)
            ok &= _arguments[position].findValue().unify(other.arguments()[position]);
        }

      return ok;
    }

  public void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
      for (int i=arity(); i-->0;)
        argument(i).checkOccurrence(parameter,context,typeChecker);
    }

  public HashSet getParameters (HashSet set)
    {
      for (int i=arity(); i-->0;)
        argument(i).getParameters(set);
      return set;
    }

  public final String toString ()
    {
      StringBuilder buf = new StringBuilder(_name);

      if (arity() != 0)
        {
          buf.append("(");
          for (int i=0; i<arity(); i++)
            buf.append(argument(i)+(i==arity()-1?")":","));
        }

      return buf.toString();
    }

}
