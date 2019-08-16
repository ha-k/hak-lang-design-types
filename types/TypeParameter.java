//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Sun Oct 21 17:05:34 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import hlt.language.util.ObjectToIntMap;
import hlt.language.design.kernel.ParameterStack;

public class TypeParameter extends StaticType implements Bindable
{
  /**
   * This is set to this type parameter's dereferenced value if bound, or itself.
   * (See the <tt>value()</tt> and the <tt>valueRef()</tt> methods.)
   */
  private Type _value = this;

  /**
   * This is used to order type parameters in the header of a type's quantified string.
   */
  private int _index = -1;

  /**
   * This records the set of residuations attached to this type parameter.
   * It associates to each residuation the value of its reference count current
   * at the time the residuation is attached to this parameter. This value is
   * used to reset the residuations' refererence count to its value upon unbinding
   * this.
   */
  private ObjectToIntMap _residuations;

  public final ObjectToIntMap residuations ()
    {
      return _residuations;
    }

  public final int numberOfTypeComponents ()
    {
      return 0;
    }

  public final Type typeRefComponent (int n) throws NoSuchTypeComponentException
    {
      throw new NoSuchTypeComponentException(this,n);
    }

  public final void setTypeRefComponent (int n, Type type) throws NoSuchTypeComponentException
    {
      throw new NoSuchTypeComponentException(this,n);
    }

  public final byte kind ()
    {
      return PARAMETER;
    }    

  // /**
  //  * This method returns the possibly dereferenced type value of this
  //  * type parameter. (Note to myself: This is never used anywhere as far
  //  * as I know. This is why it is commented out.)
  //  */
  // public final Type valueRef ()
  //   {
  //     return _value;
  //   }

  /**
   * This method acts as a type parameter dereferencing for possibly
   * bound type parameters.  Type parameters act as logical variable in
   * Prolog and may get bound by type unification during type
   * checking/inference.
   */
  public final Type value ()
    {
      return _value == this ? _value : _value.value();
    }

  /**
   * This returns the type value this is bound to and side-effects the
   * binding chains by shortening them (<i>i.e.</i>, like the
   * <i>FIND</i> operation of the <i>"UNION/FIND"</i> algorithm.  In
   * other words, it returns the same value as the <tt>value()</tt>
   * method, but with a side-effect of setting its <tt>_value</tt> field
   * to the type it is bound to (which is itself if unbound)
   */
  public final Type findValue ()
    {
      return _value == this ? _value : (_value = _value.findValue());
    }

  public final boolean isBound ()
    {
      return this != _value;
    }

  public final boolean isUnbound ()
    {
      return this == _value;
    }

  /**
   * Binds this type parameter to the given valuable in the context of
   * the given prover. This binding is appropriately trailed in the
   * contextual prover for undoing purposes. Returns <tt>true</tt>.
   */
  public final boolean bind (Valuable valuable, GoalProver prover)
    {
      valuable = valuable.getValue();
      if (this != valuable)
        {
          _value = (Type)valuable;
          prover.trail(this);
        }
      _releaseResiduations(_value,prover);
      return true;
    }

  public final boolean bind (Valuable value)
    {
      _value = (Type)value;
      return true;
    }

  /**
   * This unbinds this type parameter and resets the reference counts for all residuations
   * this has a trigger link with, if any.
   */
  public final void unbind ()
    {
      _value = this;

      if (_residuations != null)
        for (Iterator i=_residuations.iterator(); i.hasNext();)
          {
            ObjectToIntMap.Entry entry = (ObjectToIntMap.Entry)i.next();
            ResiduatedGoal residuation = (ResiduatedGoal)entry.key();
            int refCount = entry.value();
            residuation.setRefCount(refCount);
            if (refCount == 0)
              _residuations.remove(residuation);
          }
    }

  /**
   * Installs the specified residuation to be triggered by this type parameter,
   * saving the specified reference count to reinstate for the residuation upon
   * unbinding this type parameter.
   */
  public final boolean addResiduation (ResiduatedGoal residuation, int refCount)
    {
      if (_residuations == null)
        _residuations = new ObjectToIntMap();

      boolean actuallyAdded = !_residuations.containsKey(residuation)
                            | _residuations.put(residuation,refCount) != refCount;
      return actuallyAdded;
    }

  public final HashSet getParameters (HashSet set)
    {
      set.add(this);
      return set;
    }

  public final boolean isPolymorphic ()
    {
      return (_value == this ? true : _value.isPolymorphic());
    }

  public final void curry (int depth, TypeChecker typeChecker)
    {
      if (isBound())
        _value.curry(depth,typeChecker);
    }
    
  /**
   * Flattens this type parameter's value by uncurrying all the domains
   * of all function types that occur in it. This is the canonical for
   * used for all types of defined symbols.
   */
  public final Type flatten ()
    {
      if (isBound())
        return _value.flatten();

      return this;
    }    

  /**
   * This returns this parameter's index in the header array of a type's
   * quantifies string where this appears.
   */
  public final int parameterIndex ()
    {
      return _index;
    }

  /**
   * Returns a copy of this <tt>TypeParameter</tt> as per the renaming given by
   * the specified <tt>HashMap</tt>. If this <tt>TypeParameter</tt> is already
   * renamed by the parameters, then the renamed copy is returned. Otherwise, a
   * fresh <tt>TypeParameter</tt> is entered as a renaming for it in the
   * parameters and returned.
   */
  public final Type copy (HashMap parameters)
    {
      if (isBound()) return value().copy(parameters);

      TypeParameter copy = (TypeParameter)parameters.get(this);

      if (copy == null)
        {
          _index = parameters.size();
          parameters.put(this,copy = new TypeParameter());
        }

      return copy;
    }

  /**
   * Returns the instance value given for this TypeParameter by the specified
   * substitution, or this if none is given.
   */
  public final Type instantiate (HashMap substitution)
    {
      if (isBound())
        return value().instantiate(substitution);
      Type value = (Type)substitution.get(this);
      return value == null ? this : value;
    }

  /**
   * This returns this type parameter unchanged. Note the difference of
   * this method and its namesake <tt>copy(HashMap)</tt> taking a
   * renaming table, and which returns a distinct copy. This difference
   * is very important as an unquantified <tt>TypeParameter</tt> must be
   * regarded as a constant type that may be shared in a context. In the
   * absence of this context (provided by a renaming table in the method
   * <tt>copy(HashMap)</tt>), it must be assumed that the renaming is
   * the identity to capture consistently several occurrences.
   */
  public final Type copy ()
    {
      return this;
    }

  /**
   * Unifies this type parameter with the given type in the context of
   * the given type-checker. If this occurs in the specified type,
   * unification fails by throwing a FailedUnificationException.
   * Otherwise this get bound to the specified type. This binding is
   * appropriately trailed in the contextual type-checker for undoing
   * purposes.
   */
  public final void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      type.checkOccurrence(this,type,typeChecker);
      typeChecker.trail(this);
      _releaseResiduations(_value=type,typeChecker);
    }

  private final void _releaseResiduations (Type type, GoalProver prover)
    {
      if (_residuations != null)
        for (Iterator i=_residuations.keys(); i.hasNext();)
          ((ResiduatedGoal)i.next()).release(type,prover);
    }

  /**
   * <b>NB:</b> does not perform the <i>"occurs-check"</i> test.
   */
  public final boolean unify (Type type)
    {
      _value = type.findValue();
      return true;
    }

  public final void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
      if (this == parameter)
        typeChecker.error(new CircularTypeException(parameter+" occurs in "+context));
    }

  public final int eqCode ()
    {
      return kind();
    }

  /**
   * Returns true iff this type parameter is the given object.
   */
  public final boolean equals (Object object)
    {
      return this == object;
    }

  /**
   * Returns true iff this type parameter is the given one.
   */
  public final boolean isEqualTo (Type type)
    {
      return this == type;
    }

  /**
   * Returns true iff this type is parameter is equal to the given one
   * under the renaming of type parameters specified by the given HashMap.
   */
  public final boolean isEqualTo (Type type, HashMap parameters)
    {
      type = type.value();

      Type renaming = (Type)parameters.get(this);

      if (renaming != null)
        return (renaming == type);

      parameters.put(this,type);

      return true;      
    }

  //  private static int _tcount = 0;
  //  private String _tname = "?"+(_tcount++);

  public final String toString ()
    {
      return name(value());
      //return isBound() ? (name(this)+">-->"+_value) : name(this);
      //return isBound() ? _tname+">-->"+value().toString() : _tname;
      //return isBound() ? value().toString() : _tname;
    }

}
