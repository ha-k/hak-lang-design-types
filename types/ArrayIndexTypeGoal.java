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
 * An <tt>ArrayIndexTypeGoal</tt> encapsulates a pair consisting of an expression and
 * an array type. Such a goal is <i>proven</i> in the context of a <tt>TypeChecker</tt>
 * by unifying the type of the expression with the array type's index type.
 */

public class ArrayIndexTypeGoal extends Goal
{
  private Expression _expression;
  private ArrayType _type;

  public ArrayIndexTypeGoal (Expression expression, ArrayType type)
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

  final Type arrayType ()
    {
      return _type;
    }

  final void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      _expression.type().unify(_type.indexType(),typeChecker);
    }

  public final String toString ()
    {
      return super.toString() +
             ": (array index type) array type => " + _type.value() +
             ", expression => " + _expression;
    }
}
