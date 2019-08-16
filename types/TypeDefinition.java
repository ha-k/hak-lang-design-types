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

/**
 * This class encapsulates the information associated to a type term acting as an
 * <i>alias</i> (or <i>synonym</i>) of another type.
 *
 * <p>
 *
 * An object of this class consists of a (type) name, possibly along with some type
 * parameters if it is polymorphic, and associated with a type defining it. Such type
 * definitions are not opaque: the type terms are fully expanded into the definition
 * they stand for and disappear altogether once this is done. In other words, using a
 * this construct is paramount to using a <i>shorthand</i> notation; importantly,
 * <i>it does not create a new type</i>. Thus, its purpose is simply to offer a
 * syntactic convenience that may be used to enhance program legibility. Once defined
 * with an object of this class, a type term with this name (possibly with type
 * arguments if this definition is polymorphic) can be used anywhere a type is
 * expected.
 *
 * <p>
 *
 * If what is sought is an <i>opaque type</i> definition, then what is needed is a
 * <a href="DefinedType.html"><tt>DefinedType</tt></a>.
 *
 * <p>
 *
 * At any rate, look in <a href="Tables.html"><tt>Tables</tt></a> for the methods that
 * will define a type alias (<tt>DefineTypeAlias(...)</tt>) or a new opaque type
 * (<tt>DefineNewType(...)</tt>).
 *
 * @see DefinedType
 * @see Tables
 */
public class TypeDefinition extends TypeTermScheme
{
  /**
   * The name of the defined type.
   */
  private String _name;

  /**
   * The defining type associated to the type term.
   */
  private Type _definition;

  /**
   * Constructs a non-polymorphic type definition with the specified name and
   * definition.
   */
  TypeDefinition (String name, Type definition)
    {
      _name = name.intern();
      _definition = definition.flatten();
    }

  /**
   * Constructs a polymorphic type definition with the specified name, definition,
   * and type parameters.
   */
  TypeDefinition (String name, Type definition, TypeParameter[] parameters)
    {
      this(name,definition);
      _parameters = parameters;
    }

  /**
   * Constructs a polymorphic type definition with the specified name, definition,
   * and type parameters.
   */
  TypeDefinition (String name, Type definition, AbstractList parameters)
    {
      this(name,definition);

      if (parameters != null && !parameters.isEmpty())
        {
          _parameters = new TypeParameter[parameters.size()];
          for (int i=0; i<_parameters.length; i++)
            _parameters[i] = (TypeParameter)parameters.get(i);
        }
    }

  /**
   * Returns a fresh copy of the type defining this definition's type term.
   */
  public final Type definition ()
    {
      return _definition.copy();
    }

  /**
   * Returns a fresh copy of the type definition instantiated with the specified types.
   * It checks that the arities correspond. If no parameters exist for this definition
   * but the type instances have a positive arity, an array of parameters is created
   * with this arity. This is necessary in order to enforce that all instances of this
   * type have the same arity.
   */ 
  public final Type instantiate (AbstractList types) throws StaticSemanticsErrorException
    {
      HashMap substitution = new HashMap();

      int size = types == null ? 0 : types.size();

      if (size != arity())
        {
          if (_parameters == null)
            {
              _parameters = new TypeParameter[size];
              for (int i = size; i-->0;)
                _parameters[i] = new TypeParameter();
            }
          else
            throw  new TypeDefinitionException("wrong number of arguments for type "+_name+
                                               "; expected: "+arity()+", found: "+size);
        }

      for (int i = size; i-->0;)
        {
          Type type = (Type)types.get(i);
          if (type.isVoid())
            throw new TypingErrorException("void type instantiation");
          substitution.put(_parameters[i],type);
        }

      return _definition.instantiate(substitution);
    }

  public final String toString ()
    {
      Type definition = _definition.kind() == Type.DEFINED
                      ? ((DefinedType)_definition).definition()
                      : _definition;

      Type.resetNames();
      StringBuilder buf = new StringBuilder(_name);

      for (int i=0; i<arity(); i++)
        {
          if (i == 0) buf.append("(");
          buf.append(_parameters[i]);
          buf.append(i == arity()-1 ? ")" : ",");
        }

      return buf.append(" = ").append(definition.toFullString()).toString();
    }
}
