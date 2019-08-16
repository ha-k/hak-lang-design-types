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
import java.util.AbstractList;

import hlt.language.design.kernel.ParameterStack;

/**
 * This is the type of tuples whose components are identified by position.
 */
public class TupleType extends ConstructedType
{
  protected Type[] _components;

  public final int numberOfTypeComponents ()
    {
      return dimension();
    }

  public final Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      if (n >= 0 && n < dimension())
        return _components[n];

      throw new NoSuchTypeComponentException(this,n);
    }

  public final void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      if (n >= 0 && n < dimension())
        _components[n] = type;
      else
        throw new NoSuchTypeComponentException(this,n);
    }

  protected TupleType ()
    {
    }

  public final static TupleType EMPTY = new TupleType();

  private TupleType (Type[] components)
    {
      _components = components;
    }

  public final static TupleType newTupleType (Type[] components)
    {
      if (components == null || components.length == 0)
        return EMPTY;

      for (int i=0; i<components.length; i++)
        if (components[i].value().isVoid())
          throw new TypingErrorException("void tuple component type");

      return new TupleType(components);
    }

  public final static TupleType newTupleType (AbstractList components)
    {
      if (components == null || components.size() == 0)
        return EMPTY;

      Type[] types = new Type[components.size()];
      for (int i=0; i<types.length; i++)
        if ((types[i] = (Type)components.get(i)).value().isVoid())
          throw new TypingErrorException("void tuple component type");

      return new TupleType(types);
    }

  public byte kind ()
    {
      return TUPLE;
    }

  public final Type component (int i)
    {
      return _components[i].value();
    }

  public final Type[] components ()
    {
      return _components;
    }

  public final int dimension ()
    {
      return _components == null ? 0 : _components.length;
    }

  public void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      if (TypeChecker.ALLOWS_UNIFYING_OPAQUE_TUPLES && type.kind() == DEFINED)
        {
          Type definition = type.actualType();
          if (definition.kind() == TUPLE || definition.kind() == NAMED_TUPLE)
            type = definition;
        }

      switch (type.kind())
        {
        case PARAMETER:
          type.unify(this,typeChecker);
          return;

        case TUPLE:
          {
            TupleType other = (TupleType)type;

            if (other.dimension() != dimension())
              typeChecker.error(new TypeClashException(this,other));

            for (int i=dimension(); i-->0;)
              {
                component(i).unify(other.components()[i],typeChecker);
                if (component(i).isVoid())
                  typeChecker.error(new TypingErrorException("void tuple component type"));
              }
            
            return;
          }

        case NAMED_TUPLE:
          if (TypeChecker.ALLOWS_POSITIONAL_NAMED_TUPLES)
            {
              NamedTupleType other = (NamedTupleType)type;

              if (other.dimension() != dimension())
                typeChecker.error(new TypeClashException(this,other));

              for (int i=dimension(); i-->0;)
                {
                  component(i).unify(other.components()[other.fields()[i].index()],typeChecker);
                  if (component(i).isVoid())
                    typeChecker.error(new TypingErrorException("void tuple component type"));
                }

              return;
            }

        default:
          typeChecker.error(new TypeClashException(this,type));
        }
    }

  public boolean unify (Type type)
    {
      if ((type = type.findValue()) == this)
        return true;

      if (TypeChecker.ALLOWS_UNIFYING_OPAQUE_TUPLES && type.kind() == DEFINED)
        {
          Type definition = type.actualType();
          if (definition.kind() == TUPLE || definition.kind() == NAMED_TUPLE)
            type = definition;
        }

      switch (type.kind())
        {
        case PARAMETER:
          ((TypeParameter)type).bind(this);
          return true;

        case TUPLE:
          TupleType other = (TupleType)type;
          int position = dimension();
          boolean ok = (position == other.dimension());

          while (ok && position-->0)
            ok &= _components[position].findValue().unify(other.components()[position]);

          return ok;
        }

      return false;
    }

  public final void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
      for (int i=dimension(); i-->0;)
        component(i).checkOccurrence(parameter,context,typeChecker);
    }

  public final HashSet getParameters (HashSet set)
    {
      for (int i=dimension(); i-->0;)
        component(i).getParameters(set);
      return set;
    }

  public final Type flatten ()
    {
      for (int i=dimension(); i-->0;)
        _components[i] = component(i).flatten();
      return this;
    } 

  public Type copy (HashMap parameters)
    {
      if (dimension() == 0)
        return this;

      Type[] newComponents = new Type[dimension()];

      for (int i=dimension(); i-->0;)
        newComponents[i] = component(i).copy(parameters);

      return new TupleType(newComponents);
    }

  public Type instantiate (HashMap substitution)
    {
      if (dimension() == 0)
        return this;

      Type[] newComponents = new Type[dimension()];

      for (int i=dimension(); i-->0;)
        newComponents[i] = component(i).instantiate(substitution);

      return new TupleType(newComponents);
    }

  public final int eqCode ()
    {
      int code =  kind() + dimension();

      for (int i=dimension(); i-->0;)
        code += (i+1)*component(i).eqCode();

      return code;
    }

  public boolean isEqualTo (Type type)
    {
      if (this == type)
        return true;

      if (!(type instanceof TupleType))
        return false;

      TupleType other = (TupleType)type;
          
      if (dimension() != other.dimension())
        return false;

      for (int i=dimension(); i-->0;)
        if (!component(i).isEqualTo(other.component(i)))
          return false;

      return true;
    }

  public boolean isEqualTo (Type type, HashMap parameters)
    {
      if (this == type)
        return true;

      if (!(type instanceof TupleType))
        return false;

      TupleType other = (TupleType)type;
          
      if (dimension() != other.dimension())
        return false;

      for (int i=dimension(); i-->0;)
        if (!component(i).isEqualTo(other.component(i),parameters))
        return false;

      return true;
    }

  public String toString ()
    {
      StringBuilder buf = new StringBuilder("<");

      for (int i=0; i<dimension(); i++)
        buf.append(component(i)+(i==dimension()-1?"":","));

      return buf.append(">").toString();
    }

}

