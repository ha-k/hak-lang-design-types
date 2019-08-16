//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Thu Mar 24 12:00:15 2016 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import hlt.language.design.kernel.Expression;
import hlt.language.design.kernel.Constant;
import hlt.language.design.kernel.ParameterStack;

import hlt.language.util.ArrayList;

import hlt.language.tools.Misc;


public abstract class Type implements Valuable
{
  public static final byte CONSTANT    = 0;
  public static final byte BOXABLE     = 1;
  public static final byte PARAMETER   = 2;
  public static final byte FUNCTION    = 3;
  public static final byte ARRAY       = 4;
  public static final byte TUPLE       = 5;
  public static final byte NAMED_TUPLE = 6;
  public static final byte SET         = 7;
  public static final byte BAG         = 8;
  public static final byte LIST        = 9;
  public static final byte CLASS       = 10;
  public static final byte COLLECTION  = 11;
  public static final byte DEFINED     = 12;
  
  public static final byte VOID_SORT   = 0;
  public static final byte INT_SORT    = 1;
  public static final byte REAL_SORT   = 2;
  public static final byte OBJECT_SORT = 3;

  public static final TypeConstant VOID       = new TypeConstant("void",VOID_SORT,false);
  public static final TypeConstant INT        = new TypeConstant("int",INT_SORT,false);
  public static final TypeConstant REAL       = new TypeConstant("real",REAL_SORT,false);
  public static final TypeConstant CHAR       = new TypeConstant("char",INT_SORT,false);
  public static final TypeConstant BOOLEAN    = new TypeConstant("boolean",INT_SORT,false);
  public static final TypeConstant STRING     = new TypeConstant("string");
  public static final TypeConstant INT_RANGE  = new CollectionTypeConstant("int..int",INT());
  public static final TypeConstant REAL_RANGE = new TypeConstant("real..real");

  /**
   * Returns the number of type components making up this type.
   */
  public abstract int numberOfTypeComponents ();  

  /**
   * Returns the <tt>n</tt>-th undereferenced type component in this type, if any; throws a
   * <tt>NoSuchTypeComponentException</tt> otherwise.
   */
  public abstract Type typeRefComponent (int n) throws NoSuchTypeComponentException;

  /**
   * Sets the <tt>n</tt>-th undereferenced type component of this type to the specified type;
   * throws a <tt>NoSuchTypeComponentException</tt> otherwise.
   */
  public abstract void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException;

  /**
   * Returns the <tt>n</tt>-th derefenced type component in this type, if any; throws a
   * <tt>NoSuchTypeComponentException</tt> otherwise.
   */
  public final Type typeComponent (int n) throws NoSuchTypeComponentException
    {
      return typeRefComponent(n).value();
    }

  /**
   * This creates and returns a new type for boolean that may
   * be boxed by the compiler as the situation dictates.
   */
  public static final BoxableTypeConstant BOOLEAN ()
    {
      return new BoxableTypeConstant(BOOLEAN,false);
    }

  /**
   * This creates and returns a new type for integers that may
   * be boxed by the compiler as the situation dictates.
   */
  public static final BoxableTypeConstant INT ()
    {
      return new BoxableTypeConstant(INT,false);
    }

  /**
   * This creates and returns a new type for real numbers that may
   * be boxed by the compiler as the situation dictates.
   */
  public static final BoxableTypeConstant REAL ()
    {
      return new BoxableTypeConstant(REAL,false);
    }

  /**
   * This creates and returns a new type for characters that may
   * be boxed by the compiler as the situation dictates.
   */
  public static final BoxableTypeConstant CHAR ()
    {
      return new BoxableTypeConstant(CHAR,false);
    }

  /**
   * This creates and returns a new type for boolean that may
   * be unboxed by the compiler as the situation dictates.
   */
  public static final BoxableTypeConstant BOXED_BOOLEAN ()
    {
      return new BoxableTypeConstant(BOOLEAN,true);
    }

  /**
   * This creates and returns a new type for integers that may
   * be unboxed by the compiler as the situation dictates.
   */
  public static final BoxableTypeConstant BOXED_INT ()
    {
      return new BoxableTypeConstant(INT,true);
    }

  /**
   * This creates and returns a new type for real numbers that may
   * be unboxed by the compiler as the situation dictates.
   */
  public static final BoxableTypeConstant BOXED_REAL ()
    {
      return new BoxableTypeConstant(REAL,true);
    }

  /**
   * This creates and returns a new type for integers that may
   * be unboxed by the compiler as the situation dictates.
   */
  public static final BoxableTypeConstant BOXED_CHAR ()
    {
      return new BoxableTypeConstant(CHAR,true);
    }

  public final boolean isCollection ()
    {
      return (this instanceof Collection);
    }

  public final boolean isPrimitive ()
    {
      return (this == VOID || this == BOOLEAN || this == INT || this == REAL || this == CHAR
              || kind() == DEFINED && ((DefinedType)this).definition().isPrimitive());
    }

  public final boolean is (Type type)
    {
      return this == type
          || kind() == DEFINED
          && ((DefinedType)this).definition().is(type)
          || kind() == BOXABLE
          && ((BoxableTypeConstant)this).type() == type;
    }

  public final boolean isVoid ()
    {
      return this == VOID;
    }

  public final boolean isBoolean ()
    {
      return is(BOOLEAN);
    }

  public final boolean isInt ()
    {
      return is(INT);
    }

  public final boolean isReal ()
    {
      return is(REAL);
    }

  public final boolean isChar ()
    {
      return is(CHAR);
    }

  public final boolean isString ()
    {
      return this == STRING;
    }

  public final boolean isSet ()
    {
      return kind() == SET;
    }

  public final boolean isBag ()
    {
      return kind() == BAG;
    }

  public final boolean isList ()
    {
      return kind() == LIST;
    }

  /**
   * Returns this when this does not have a base type; or the (undereferenced)
   * base type otherwise.
   */
  public Type baseTypeRef ()
    {
      return this;
    }

  /**
   * Returns the value of this when this does not have a base type; or that of
   * its base type otherwise.
   */
  public Type baseType ()
    {
      return value();
    }

  /**
   * Returns the value of this when this does not have a defining type; or that of
   * its defining type otherwise. This is overriden in <a href="DefinedType.html">
   * <tt>DefinedType</tt></a>.
   */
  public Type actualType ()
    {
      return this;
    }

  /**
   * <a name="shadowType"></a>
   * This is used in <a href="../kernel/Homomorphism.html#typeChecking"><tt>Homomorphism</tt></a>
   * for typing irregular monoid operations in the absence of a real mechanism for subtyping.
   * Returns this type's value by default.
   * Only a <a href="TypeConstant.html#shadowType"><tt>TypeConstant</tt></a>
   * may redefine its shadow type.
   */
  public Type shadowType ()
    {
      return value();
    }

  /**
   * Returns an integer measuring the collection rank of this type. It is defined
   * inductively as follows: it is equal to <tt>0</tt> for non-collection types;
   * for a collection type with base type <tt>T</tt>, it is equal to <tt>1</tt>
   * plus the rank of <tt>T</tt>.
   */
  public final int rank ()
    {
      if (isCollection())
        return 1+baseType().rank();
      return 0;
    }

  /**
   * Returns an array type of the specified dimension with this type as base type.
   */
  public final ArrayType array (Expression[] dimension) 
    {
      return _array(dimension,0);
    }

  /**
   * Returns a partial (up to specified subdimension depth) array type of the specified
   * dimension with this type as base type.
   */
  private final ArrayType _array (Expression[] dimension, int depth) 
    {
      return new ArrayType(depth >= dimension.length-1 ? this : _array(dimension,depth+1),
                           dimension[depth].type());
    }

  /**
   * Returns an array type of the specified dimension number (with unspecified dimension types)
   * with this type as base type.
   */
  public final ArrayType array (int dimension) 
    {
      return _array(dimension,0);
    }

  /**
   * Returns a partial (up to specified subdimension depth) array type of the specified dimension
   * number (with unspecified dimension types) with this type as base type.
   */
  private final ArrayType _array (int dimension, int depth) 
    {
      return new ArrayType(depth >= dimension-1 ? this : _array(dimension,depth+1),
                           new TypeParameter());
    }

  /**
   * Returns <tt>true</tt> iff this type has a structure that may be modified internally at
   * a place that has a polymorphic type.  Objects of such types should not be allowed to be
   * defined globally since this may lead to unsafe typing. For example, if <tt>a</tt> is a
   * <i>globally</i> defined polymorphic array of type <tt><b>forall</b> A . A[]</tt>, then
   * the type checker has no way of disallowing certain illegal situations such as <tt>a[0]
   * = true; a[1] = "foo";</tt> because the two occurrences of the <i>global</i> name
   * <tt>a</tt> have two different instances of its type.  Note however that this
   * pathology does not arise when <tt>a</tt> is a <i>local</i> array name, because it
   * then refers to the same entity, and thus to a single type instance. In other words,
   * for a polymorphic <i>local</i> <tt>a</tt>, the example given can thus be flagged as
   * a type clash.
   *
   * <p>
   *
   * A type will be unsafe for global definitions if it is:
   * <ul>
   * <li> a non-functional polymorphic type;
   * <li> an array type whose base type is polymorphic or itself unsafe;
   * <li> a  tuple that has at least one component type that is polymorphic or itself unsafe;
   * <li> a class type that is either not yet declared or has at least one field whose value
   *      type is polymorphic or itself unsafe.
   * </ul>
   * Note that this last criterion entails that a polymorphic class type may be safe, and
   * that a non-polymorphic class type may not be so.
   */
    public final boolean isGlobalUnsafe ()
    {
      switch (kind())
        {
        case DEFINED:
          return ((DefinedType)this).definition().isGlobalUnsafe();
        case FUNCTION:
          return false;
        case ARRAY:
          Type type = ((ArrayType)this).baseType();
          return type.isPolymorphic() || type.isGlobalUnsafe();
        case SET:
          type = ((SetType)this).baseType();
          return type.isPolymorphic() || type.isGlobalUnsafe();
        case TUPLE:
        case NAMED_TUPLE:
          TupleType tupleType = (TupleType)this;
          for (int i = 0; i < tupleType.dimension(); i++)
            {
              type = tupleType.component(i);
              if (type.isPolymorphic() || type.isGlobalUnsafe())
                return true;
            }
          return false;
        case CLASS:
          ClassType classType = (ClassType)this;
          if (!classType.isDeclared())
            return true;

          if (_isDejaVu(this))
            return false;
          
          DefinedEntry[] fields = classType.fields();
          boolean result = false;

          classType.bindArguments();
          for (int i=0; i<fields.length; i++)
            {
              type = fields[i].fieldType();
              if (type.isPolymorphic() || type.isGlobalUnsafe())
                {
                  result = true;
                  break;
                }
            }
          classType.unbindArguments();
          return result;
        default:
          return isPolymorphic();
        }
    }

  private static ArrayList _dejaVu = new ArrayList();

  private static final boolean _isDejaVu (Type type)
    {
      for (int i=_dejaVu.size(); i-->0;)
        if (type.isEqualTo((Type)_dejaVu.get(i))) return true;
      _dejaVu.add(type);
      return false;
    }       

  public boolean isPolymorphic ()
    {
      return false;
    }

  /**
   * This returns the type value this is bound to (itself by default
   * - overridden by <a href="TypeParameter.html"><tt>TypeParameter</tt></a>).
   */
  public Type value ()
    {
      return this;
    }

  /**
   * This returns the type value this is bound to and side-effects the binding
   * chains by shortening them (<i>i.e.</i>, like the <i>FIND</i> operation of
   * the <i>UNION/FIND</i> algorithm (returns itself by default - overridden
   * by <a href="TypeParameter.html"><tt>TypeParameter</tt></a>).
   */
  public Type findValue ()
    {
      return this;
    }

  /**
   * This is identical to the <tt>value</tt> method but returns a
   * <tt>Valuable</tt> as mandated as it implements this interface.
   * It is never used on <tt>Type</tt> objects - only <tt>value()</tt>
   * or <tt>findValue()</tt> are.
   */
  public final Valuable getValue ()
    {
      return (Valuable)value();
    }

  /**
   * This returns the runtime sort for this type (<tt>OBJECT_SORT</tt>
   * by default - overridden by <a href="TypeConstant.html"><tt>TypeConstant</tt></a>).
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public byte sort ()
    {
      return OBJECT_SORT;
    }

  /**
   * This returns the runtime sort for this type unless this type
   * has been marked as boxed - in which case it returns <tt>OBJECT_SORT</tt>.
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public final byte boxSort ()
    {
      return isBoxedType() ? OBJECT_SORT : sort();
    }

  /**
   * This returns the built-in function that is appropriate for
   * turning a value with primitive type into an object.
   */
  public final Expression wrapper ()
    {
      if (sort() == INT_SORT)
        return Constant.WRAP_INT;
      else
        return Constant.WRAP_REAL;
    }

  /**
   * This returns the built-in function that is appropriate for
   * turning a boxed value with primitive type into that value.
   */
  public final Expression unwrapper ()
    {
      if (sort() == INT_SORT)
        return Constant.UNWRAP_INT;
      else
        return Constant.UNWRAP_REAL;
    }

  /**
   * This returns <tt>true</tt> iff this type has been marked as boxed
   * (<tt>true</tt> by default - overridden by <a href="TypeConstant.html">
   * <tt>TypeConstant</tt></a>).
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public boolean isBoxedType ()
    {
      return true;
    }

  /**
   * With <tt>true</tt> (resp. <tt>false</tt>), this marks this type as boxed
   * (resp., unboxed), if it is boxable. Does nothing by default.
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public Type setBoxed (boolean flag)
    {
      return this;
    }

  /**
   * This returns the value identifying what kind of type this is.
   */
  public abstract byte kind ();

  /**
   * This returns the string describing the identifying kind this type.
   */
  public final String kindString ()
    {
      switch (kind())
        {
        case CONSTANT:    return "CONSTANT";
        case BOXABLE:     return "BOXABLE";
        case PARAMETER:   return "PARAMETER";
        case FUNCTION:    return "FUNCTION";
        case ARRAY:       return "ARRAY";
        case TUPLE:       return "TUPLE";
        case NAMED_TUPLE: return "NAMED_TUPLE";
        case SET:         return "SET";
        case BAG:         return "BAG";
        case LIST:        return "LIST";
        case CLASS:       return "CLASS";
        case DEFINED:     return "DEFINED";
        }
      return "ILLEGAL";
    }  

  /**
   * This unifies this type with the specified type in the context
   * of the specified <a href="TypeChecker.html"> <tt>TypeChecker</tt></a>.
   */
  public abstract void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException;

  /**
   * This unifies this type with the specified type with no possibility of undoing any
   * effects. This method is meant to be used on types that are fresh copies independent of
   * any type-checker because it will side-effect the structures it traverses. It returns
   * <tt>true</tt> iff unification succeeds. <b>NB:</b> This method does not perform the
   * <i>"occurs-check"</i> test.
   */
  public abstract boolean unify (Type type);

  /**
   * Throws a <tt>FailedUnificationException</tt> if the specified type parameter occurs
   * in this type. The type <tt>context</tt> is the outermost type enclosing this.
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
    }

  /**
   * If this this a function type, transforms the type by splitting the
   * domains at the specified depth, and records the old form in the
   * appropriate trail of the specified type-checker for undoing
   * purposes. For example, currying A[1],...,A[n]n -&gt; A at depth k&lt;n,
   * transforms it into A[1],...,A[k] -&gt; (A[k+1],...,A[n] -&gt; A). If this
   * type is a type parameter, this will curry its value if any.
   * Otherwise, does nothing.
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public void curry (int depth, TypeChecker typeChecker)
    {
    }

  /**
   * Returns a flattened form of this type by uncurrying all the domains of all
   * function types that occur in it. It also trims all binding chains of type
   * parameters that may occur in it. The form returned is the canonical form
   * used for all types of defined symbols and is assumed for type equality
   * tests to work.
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are generally
   * expected to override this method.</i>
   */
  public Type flatten ()
    {
      return this;
    } 

  /**
   * Returns a copy of this type with a consistent renaming of all free type
   * parameters, and identyfing bound type parameters with their values.
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public Type copy (HashMap parameters)
    {
      return this;
    }

  /**
   * Returns a copy of this type with an empty renaming.
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public Type copy ()
    {
      return copy(new HashMap());
    }
  
  /**
   * Returns a copy of this type with all free type parameters bound as per the
   * specified type substitution.
   *
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public Type instantiate (HashMap substitution)
    {
      return copy(substitution);
    }

  /**
   * Returns a standard form for this type by creating a flattened copy
   * where all free type parameters, if any, are renamed.
   */
  public final Type standardize ()
    {
      return copy().flatten();
    }

  /**
   * Returns the specified set augmented with all the unbound type parameters that
   * occur in this type.
   */
  public HashSet getParameters (HashSet set)
    {
      return set;
    }

  /**
   * Returns the set of unbound type parameters that occur in this type.
   */
  public final HashSet getParameters ()
    {
      return getParameters(new HashSet());
    }

  /**
   * Returns a hash code for this type - the reason for using the name <tt>eqCode</tt>
   * (rather than overriding <tt>hashCode</tt>), is that it is important to preserve
   * the implementation of <tt>java.lang.Object.hashCode</tt> for type parameters (to
   * be compatible with <tt>==</tt>).  All we require with this new code is that it be
   * compatible with type equality as defined by <tt>isEqualTo</tt> - and it is!
   * Incidentally, the same reason goes for preserving <tt>equals</tt> for type parameters
   * to be compatible with <tt>==</tt> - and it is (see <a href="TypeParameter.html">
   * <tt>TypeParameter</tt></a>).
   */
  abstract public int eqCode ();

  public boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof Type))
        return false;

      return isEqualTo((Type)object,new HashMap());
    }

  /**
   * Returns true iff this type is structurally isomorphic to the specified type.
   * Two type parameters will be considered equal only if they are the same object
   * (<i>i.e.</i>, equal pointers).
   */
  public abstract boolean isEqualTo (Type type);

  /**
   * Returns true iff this type is structurally isomorphic to the
   * specified type, under the renaming of type parameters specified
   * by the given HashMap.
   */
  public abstract boolean isEqualTo (Type type, HashMap parameters);

  /**
   * This is used for printing type parameters as names.
   */
  private static final HashMap _names = new HashMap();

  /**
   * Returns the name of the given type parameter. This is used only
   * in <a href="TypeParameter.html"><tt>TypeParameter</tt></a> for
   * printing. (The type of the argument is <tt>Type</tt> rather than
   * <tt>TypeParameter</tt> because it is called with the <tt>value()</tt>
   * method which returns a <tt>Type</tt.)
   */
  protected static final String name (Type v)
    {
      String name = (String)_names.get(v);
      if (name==null) _names.put(v,name=_newVarName());
      return name;
    }

  /**
   * A global counter for generating new parameter names.
   */
  private static int _varCount = 0;

  /**
   * Returns a new parameter name. The generated names are, successively,
   * <tt>A, B, ..., Z, AA, AB, ..., AZ, BA, BB, ..., BZ, ..., </tt>, <i>etc.</i>
   */
  private static String _newVarName ()
    {
      String s = "";

      int n = _varCount++;
      do
        {
          s = String.valueOf((char)('A'+n%26)) + s;
          n = n/26-1;
        }
      while (n >= 0);

      return s;
    }

  /**
   * Clears the names of parameters.
   */
  public static final void resetNames ()
    {
      _names.clear();
      _varCount = 0;
    }

  /**
   * <p><i><b>N.B.:</b> The following definition specifies only the
   * <b>default</b> behavior for this method. Specific subclasses are
   * generally expected to override this method.</i>
   */
  public String toFullString ()
    {
      return toString();
    }    

  /**
   * Returns a string for this type with an explicit header for universally
   * quantified type parameters if there are any in this type. If the specified
   * boolean is true, the full string is quantified.  If this contains free
   * type parameters, they are gathered in an array in the canonical order of
   * their appearance in the depth-first traversal of the type.
   */
  public final String toQuantifiedString (boolean full)
    {
      HashMap parameters = new HashMap();
      Type copy = copy(parameters);

      if (parameters.isEmpty())
        return full ? toFullString() : toString();
      
      return Misc.arrayToString(_mapToArray(parameters),"forall ",",",". ") +
             (full ? copy.toFullString() : copy.toString());
    }

  private final TypeParameter[] _mapToArray (HashMap map)
    {
      TypeParameter[] array = new TypeParameter[map.size()];

      for (Iterator i=map.entrySet().iterator(); i.hasNext();)
        {
          Map.Entry entry = (Map.Entry)i.next();
          array[((TypeParameter)entry.getKey()).parameterIndex()] = (TypeParameter)entry.getValue();
        }

      return array;
    }
    
  public final String toQuantifiedString ()
    {
      return toQuantifiedString(false);
    }

}
