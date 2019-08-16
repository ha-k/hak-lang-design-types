//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.Iterator;

import hlt.language.util.Locatable;
import hlt.language.design.kernel.Expression;

/**
 * A <tt>TypingGoal</tt> encapsulates a pair of the form <i>e:t</i>, where <i>e</i> is
 * and expression and <i>t</i> is a type. Such a goal is <i>proven</i> in the context
 * of a <tt>TypeChecker</tt> by unifying <i>e</i>'s type with <i>t</i>.
 */

public class TypingGoal extends Goal
{
  /**
   * The expression being type-checked.
   */
  private Expression _expression;
  /**
   * The candidate type to be checked for the expression.
   */
  private Type _type;

  /**
   * Constructs a typing goal with the specified expression and type.
   */
  public TypingGoal (Expression expression, Type type)
    {
      _expression = expression;
      _type = type;
    }

  /**
   * Returns this typing goal's expression.
   */
  final Expression expression ()
    {
      return _expression;
    }

  /**
   * Returns this typing goal's (undereferenced) type.
   */
  final Type type ()
    {
      return _type;
    }

  /**
   * Returns this typing goal's location's extent as a <a
   * href="../../util/Locatable.html"> <tt>Locatable</tt></a> (in fact,
   * this typing goal's expression).
   */
  final Locatable extent ()
    {
      return _expression;
    }

  final void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      _expression.type().unify(_type,typeChecker);

      if (_expression.otherTypes() != null)
        for (Iterator i=_expression.otherTypes().iterator(); i.hasNext();)
          _expression.type().unify((Type)i.next(),typeChecker);
    }

  /**
   * Returns a string form of this typing goal.
   */
  final public String toString ()
    {
      return super.toString() +
             ": (typing) type => " + _type.value() +
             ", expression => " + _expression;
    }
}
