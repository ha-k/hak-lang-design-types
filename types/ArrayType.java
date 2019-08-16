//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

import java.util.HashMap;
import java.util.HashSet;
import hlt.language.design.kernel.ParameterStack;

/**
 * This class denotes the type of arrays. Types of this kind consist of
 * unidimensional array types whose elements are of any base type (including arrays -
 * thus enabling multidimensional arrays - and type parameters - thus enabling
 * polymorphic arrays).
 *
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

public class ArrayType extends ConstructedType
{
  /**
   * Viewing this type as a unidimensional vector's, this is the type of one of its
   * components.  Thus, the base type of array type <tt>int[][][]</tt> is <tt>int[][]</tt>.
   */
  private Type _baseType;

  /**
   * This is the type of the set of values where indices come from. Kinds that are allowed
   * for this type are Type.INT(), Type.INT_RANGE, and SetType.
   */
  private Type _indexSetType;

  /**
   * Constructs a polymorphic array type; <i>i.e.</i>, one whose base type is a type
   * parameter.
   */
  public ArrayType ()
    {
      _baseType = new TypeParameter();
      _indexSetType = new TypeParameter();
    }

  /**
   * Constructs an array type with the specified base type.
   */
  public ArrayType (Type baseType)
    {
      if (baseType.value().isVoid())
        throw new TypingErrorException("void array base type");

      _baseType = baseType;
      _indexSetType = new TypeParameter();
    }

  /**
   * Constructs an array type with the specified base type and index-set type.
   */
  public ArrayType (Type baseType, Type indexSetType)
    {
      this(baseType);
      _indexSetType = indexSetType;
    }

  public final int numberOfTypeComponents ()
    {
      return 2;
    }

  public final Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      switch (n)
        {
        case 0:
          return _baseType;
        case 1:
          return _indexSetType;
        }

      throw new NoSuchTypeComponentException(this,n);
    }

  public final void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      switch (n)
        {
        case 0:
          _baseType = type;
          return;
        case 1:
          _indexSetType = type;
          return;
        }

      throw new NoSuchTypeComponentException(this,n);
    }

  /**
   * Returns the constant identifying array types; <i>i.e.</i>, <tt>Type.ARRAY</tt>.
   */
  public final byte kind ()
    {
      return ARRAY;
    }

  /**
   * Returns the base type's value. This is the type binding of the array's
   * components when the array is viewed as a unidimensional vector; <i>e.g.</i>,
   * for array type instantiated to <tt>int[][]</tt>, this returns <tt>int[]</tt>.
   */
  public final Type baseType ()
    {
      return _baseType.value();
    }

  /**
   * Returns the index-set type's value.
   */
  public final Type indexSetType ()
    {
      return _indexSetType.value();
    }

  /**
   * Returns <tt>true</tt> iff this array type is that of an indexed map.
   */
  public final boolean isMap ()
    {
      return !indexSetType().isInt();
    }

  /**
   * Returns the index type's value; <i>i.e.</i>, this is the type that an actual index
   * will have. NB: This assumes that the index-set type is of correct kind - which is
   * guaranteed when type-checking array slots where this method is called.
   */
  public final Type indexType ()
    {
      Type indexSetType = _indexSetType.value();

      if (indexSetType.isInt() || indexSetType == Type.INT_RANGE)
        return Type.INT();

      return ((SetType)indexSetType).baseType();
    }

  /**
   * Returns <tt>true</tt> iff this array type's base type is polymorphic.
   * Note that this lets the index set type be polymorphic - because it can
   * never be an unbound type parameter and if it is a set type, its base type
   * does not matter.
   */
  public final boolean isPolymorphic ()
    {
      return baseType().isPolymorphic();
    }

  /**
   * Returns the innermost type. This is the type of the array's innermost elements;
   * <i>e.g.</i>, for an array type instantiated to <tt>int[][]</tt>, this returns
   * <tt>int</tt>.
   */
  public final Type innermostType ()
    {
      Type type = baseType();

      if (type.kind() == ARRAY)
        return ((ArrayType)type).innermostType();

      return type;
    }

  /**
   * Returns the inner type up to depth <tt>n</tt>. If <tt>n=0</tt>, it is this type; if
   * <tt>n=1</tt>, it is the base type; if <tt>n=2</tt> and the base type is an array
   * type, this is the base type's base type; <i>etc.</i>. For example, for an array type
   * instantiated to <tt>int[][][]</tt>, this returns <tt>int[][][]</tt> for <tt>n=0</tt>,
   * <tt>int[][]</tt> for <tt>n=1</tt>, <tt>int[]</tt> for <tt>n=2</tt>, and <tt>int</tt>
   * for <tt>n=3</tt>.
   */
  public final Type innerType (int depth)
    {
      if (depth == 0)
        return this;

      Type type = baseType();

      if (type.kind() == ARRAY)
        return ((ArrayType)type).innerType(depth-1);

      return type;
    }

  /**
   * Returns the number of dimensions of this array type; <i>i.e.</i>, the number of
   * indices needed to dereference to the innermost component. For example, the dimension
   * of <tt>int[][][]</tt> is <tt>3</tt>.
   */
  public final int dimension ()
    {
      Type base = baseType();

      if (base.kind() == ARRAY)
        return 1 + ((ArrayType)base).dimension();

      return 1;
    }

  /**
   * Returns the type of the <tt>n</tt>-th dimension, counting from <tt>0</tt>.
   * For example, for array type <tt>int[int][{string}][int..int]</tt>,
   * <tt>dimension(1)</tt> is <tt>{string}</tt>. If the specified number is more
   * than the number of actual dimensions, this returns <tt>null</tt>.
   */
  public final Type dimension (int n)
    {
      int depth = dimension()-1;

      if (n > depth)
        return null;

      return relativeDimension(depth-n,depth);
    }

  final Type relativeDimension (int position, int depth)
    {
      if (position == depth)
        return indexSetType();

      return ((ArrayType)baseType()).relativeDimension(position,depth-1);
    }

  /**
   * Returns the type obtained after projecting out the first <tt>n</tt> indices.
   * For example, <tt>projection(2)</tt> of array type <tt>int[int..int][int][{char}][int]</tt>
   * is <tt>int[{char}][int]</tt>. If the specified number is greater than the number of actual
   * dimensions, this returns <tt>null</tt>.
   */
  public final Type projection (int n)
    {
      if (n <= 0) return this;

      Type base = baseType();

      if (n == 1) return base;

      if (base.kind() == ARRAY)
        return ((ArrayType)base).projection(n-1);

      return null;
    }

  public final Type flatten ()
    {
      _baseType = baseType().flatten();
      _indexSetType = indexSetType().flatten();
      return this;
    } 

  public final Type copy (HashMap parameters)
    {
      return new ArrayType(baseType().copy(parameters),indexSetType().copy(parameters));
    }

  public Type instantiate (HashMap substitution)
    {
      return new ArrayType(baseType().instantiate(substitution),indexSetType().instantiate(substitution));
    }

  /**
   * Unifies this array type against the specifed type in the context of the given type
   * checker. Note that the advantage of representing array types systematically as
   * one-dimensional vectors allows for unifying array types of different dimensions
   * when the one of lesser dimension is polymorphic (<i>i.e.</i>, when its base type
   * is a type parameter).
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

        case ARRAY:
          baseType().unify(((ArrayType)type).baseType(),typeChecker);

          if (baseType().isVoid())
            typeChecker.error(new TypingErrorException("void array base type"));

          indexSetType().unify(((ArrayType)type).indexSetType(),typeChecker);
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

        case ARRAY:
          return baseType().unify(((ArrayType)type).baseType())
              && indexSetType().unify(((ArrayType)type).indexSetType());
        }

      return false;
    }

  public final void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
      baseType().checkOccurrence(parameter,context,typeChecker);
      indexSetType().checkOccurrence(parameter,context,typeChecker);
    }

  public final HashSet getParameters (HashSet set)
    {
      return indexSetType().getParameters(baseType().getParameters(set));
    }

  public final int eqCode ()
    {
      return kind() + baseType().eqCode() + indexSetType().eqCode();
    }

  /**
   * Returns true iff this type is structurally isomorphic to the
   * specified type. Type parameters that occur in all types will be
   * considered equal only if they are identical objects (i.e., same pointers).
   */
  public final boolean isEqualTo (Type type)
    {
      return this == type
          || type.kind() == ARRAY
             && baseType().isEqualTo(((ArrayType)type).baseType())
             && indexSetType().isEqualTo(((ArrayType)type).indexSetType());
    }

  /**
   * Returns true iff this type is structurally isomorphic to the
   * specified type, under the renaming of type parameters specified
   * by the given HashMap.
   */
  public final boolean isEqualTo (Type type, HashMap parameters)
    {
      return this == type
          || type.kind() == ARRAY
             && baseType().isEqualTo(((ArrayType)type).baseType(),parameters)
             && indexSetType().isEqualTo(((ArrayType)type).indexSetType(),parameters);
    }

  public final String toString ()
    {
      StringBuilder buf = new StringBuilder("[").append(indexSetType()).append("]");
      Type type = baseType();

      while (type.kind() == ARRAY)
        {
          buf.append("[").append(((ArrayType)type).indexSetType()).append("]");
          type = ((ArrayType)type).baseType();
        }

      return type + buf.toString();        
    }

}
