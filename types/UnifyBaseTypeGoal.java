//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.util.Locatable;
import hlt.language.design.kernel.Expression;

/**
 * A <tt>UnifyBaseTypeGoal</tt> encapsulates a pair consisting of a type and
 * a base type. Such a goal is <i>proven</i> in the context of a <tt>TypeChecker</tt>
 * by unifying the type's base type with the base type. The base type of
 * a type is defined for collection types and array types to be the type of the
 * elements of the collection or the array; the base type of any other type is
 * the type itself.
 */
public class UnifyBaseTypeGoal extends Goal
{
  private Type _type;
  private Type _baseType;
  private Locatable _extent;

  public UnifyBaseTypeGoal (Type type, Type baseType, Locatable extent)
    {
      _type = type;
      _baseType = baseType;
      _extent = extent;
    }

  final Locatable extent ()
    {
      return _extent;
    }

  final Type type ()
    {
      return _type.value();
    }

  final Type baseType ()
    {
      return _baseType.value();
    }

  final void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      type().baseType().unify(_baseType,typeChecker);
    }

  final public String toString ()
    {
      return super.toString() +
             ": (unify base type) type => " + type() +
             ", base type => " + baseType();
    }
}
