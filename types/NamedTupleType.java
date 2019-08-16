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
import java.util.AbstractList;

import hlt.language.tools.Misc;
import hlt.language.design.kernel.TupleFieldName;

/**
 * This is the type of tuples whose components are identified by field names. It derives
 * from <a href="TupleType.html"><tt>TupleType</tt></a>, and in addition keeps an
 * array of <a href="../kernel/TupleFieldName.html"><tt>TupleFieldName</tt></a>s
 * recording the names. The reason for using <tt>TupleFieldName</tt>s rather than
 * <tt>String</tt>s is so that the array of field names may be sorted lexicographically
 * for the purpose of normalizing all named tuples and named tuple types, while
 * recording their original indices for displaying components in their "original"
 * order. Note that this notion is rather fuzzy as it corresponds to the ordering of
 * components found by the constructor (which is meant to be used by a parser) and can
 * only be that of a specific written named tuple (type) occurrence. Thus, this order
 * may only be <i>one</i> among many if distinct occurrences of this type, or tuples of
 * this type, are written using differring orders of components. Nevertheless, for
 * consistently written tuples, the components will be displayed in the expected order.
 */

public class NamedTupleType extends TupleType
{
  private TupleFieldName[] _fields;
  private int[] _index;

  /**
   * This trusts the component types to be non void. It is public (even though it is not
   * meant for public use) because it is called from <a href="../kernel/NamedTuple.html">
   * <tt>NamedTuple</tt></a> and Java's access restriction is too coarse to handle limited
   * visibility across packages.
   */
  public NamedTupleType (Type[] components, TupleFieldName[] fields)
    {
      _components = components;
      _fields = fields;
    }

  public NamedTupleType (AbstractList components, AbstractList names)
    {
      // NB: this assumes that both lists are non-empty, and have the same size.

      _fields = new TupleFieldName[names.size()];
      for (int i=_fields.length; i-->0;)
        _fields[i] = new TupleFieldName((String)names.get(i),i);
      Misc.sort(_fields);

      _components = new Type[components.size()];
      for (int i=_components.length; i-->0;)
        if ((_components[i] = (Type)components.get(_fields[i].index())).value().isVoid())
          throw new TypingErrorException("void tuple component type");
    }

  public final TupleFieldName[] fields ()
    {
      return _fields;
    }

  public final int[] index ()
    {
      if (_index == null)
        {
          _index = new int[dimension()];

          for (int i=_index.length; i-->0;)
            _index[_fields[i].index()] = i;
        }

      return _index;      
    }

  public final String fieldSet ()
    {
      StringBuilder buf = new StringBuilder("{");

      for (int i=0; i<dimension(); i++)
        buf.append(_fields[index()[i]]).append(i==dimension()-1?"":", ");

      return buf.append("}").toString();
    }

  /**
   * Returns the position of the field whose name is <tt>name</tt>.
   * <b>NB:</b> Positions are counted starting from 1, not 0.
   */
  public final int position (String name)
    {
      for (int i=0; i<_fields.length; i++)
        if (_fields[i].isEqualTo(name))
          return i+1;

      return 0;
    }

  /**
   * Returns the field name originally in the specified position; <tt>null</tt>
   * if position is not legal.
   */
  public final TupleFieldName field (int n)
    {
      for (int i=0; i<_fields.length; i++)
        if (_fields[i].index() == n)
          return _fields[i];

      return null;
    }

  /**
   * Returns the position of the field whose original index is <tt>n-1</tt>.
   * <b>NB:</b> Positions are counted starting from 1, not 0.
   */
  public final int fieldPosition (int n)
    {
      n = n-1;
      for (int i=0; i<_fields.length; i++)
        if (_fields[i].index() == n)
          return i+1;

      return 0;
    }

  public final byte kind ()
    {
      return NAMED_TUPLE;
    }

  public final void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
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

        case NAMED_TUPLE:
          {
            NamedTupleType other = (NamedTupleType)type;

            if (other.dimension() != dimension())
              typeChecker.error(new TypeClashException(this,other));

            for (int i=dimension(); i-->0;)
              {
                if (!_fields[i].equals(other.fields()[i]))
                  typeChecker.error(new TypeClashException(this,other));

                component(i).unify(other.components()[i],typeChecker);
              }
            return;
          }

        case TUPLE:
          if (TypeChecker.ALLOWS_POSITIONAL_NAMED_TUPLES)
            {
              TupleType other = (TupleType)type;

              if (other.dimension() != dimension())
                typeChecker.error(new TypeClashException(this,other));

              for (int i=dimension(); i-->0;)
                component(_fields[i].index()).unify(other.components()[i],typeChecker);
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

        case NAMED_TUPLE:
          NamedTupleType other = (NamedTupleType)type;
          int position = dimension();
          boolean ok = (position == other.dimension());

          while (ok && position-->0)
            ok &= _fields[position].equals(other.fields()[position])
               && _components[position].findValue().unify(other.components()[position]);

          return ok;
        }

      return false;
    }

  public final Type copy (HashMap parameters)
    {
      if (dimension() == 0)
        return this;

      Type[] newComponents = new Type[dimension()];

      for (int i=dimension(); i-->0;)
        newComponents[i] = component(i).copy(parameters);

      return new NamedTupleType(newComponents,_fields);
    }

  public final boolean isEqualTo (Type type)
    {
      if (this == type)
        return true;

      if (!(type instanceof NamedTupleType))
        return false;

      NamedTupleType other = (NamedTupleType)type;
          
      if (dimension() != other.dimension())
        return false;

      for (int i=dimension(); i-->0;)
        if (!_fields[i].equals(other.fields()[i])
            || !component(i).isEqualTo(other.component(i)))
          return false;

      return true;
    }

  public final boolean isEqualTo (Type type, HashMap parameters)
    {
      if (this == type)
        return true;

      if (!(type instanceof NamedTupleType))
        return false;

      NamedTupleType other = (NamedTupleType)type;
          
      if (dimension() != other.dimension())
        return false;

      for (int i=dimension(); i-->0;)
        if (!_fields[i].equals(other.fields()[i])
            || !component(i).isEqualTo(other.component(i),parameters))
          return false;

      return true;
    }

  public final String toString ()
    {
      StringBuilder buf = new StringBuilder("<");

      for (int i=0; i<dimension(); i++)
        buf.append(_fields[index()[i]])
           .append(":")
           .append(component(index()[i]))
           .append(i==dimension()-1?"":",");

      return buf.append(">").toString();
    }
}
