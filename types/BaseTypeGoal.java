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
 * A <tt>BaseTypeGoal</tt> encapsulates a pair consisting of an expression and
 * a type. Such a goal is <i>proven</i> in the context of a <tt>TypeChecker</tt>
 * by unifying the type with the expression's type's base type. The base type of
 * a type is defined for collection types and array types to be the type of the
 * elements of the collection or the array; the base type of any other type is
 * the type itself.
 */

public class BaseTypeGoal extends Goal
{
  private Expression _expression;
  private Type _type;

  public BaseTypeGoal (Expression expression, Type type)
    {
      _expression = expression;
      _type = type;
    }

  final Locatable extent ()
    {
      return _expression;
    }

  final Expression expression ()
    {
      return _expression;
    }

  final Type type ()
    {
      return _type;
    }

  final void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      _expression.type().baseType().unify(_type,typeChecker);
    }

  public final String toString ()
    {
      return super.toString() +
             ": (base type) type => " + _type.value() +
             ", base type => " + _expression.type().baseType() +
             ", expression => " + _expression;
    }
}
