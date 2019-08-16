//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.kernel.Scope;
import hlt.language.util.Locatable;

/**
 * A <tt>CheckExitableGoal</tt> unifies the return type of the latest scope typechecker's
 * stack of exitable scopes with its own exit value type.
 */

public class CheckExitableGoal extends Goal
{
  private Type _type;
  private Locatable _extent;

  public CheckExitableGoal (Type type, Locatable extent)
    {
      _type = type;
      _extent = extent;
    }

  final Type type ()
    {
      return _type.value();
    }

  final Locatable extent ()
    {
      return _extent;
    }

  final void prove (TypeChecker typeChecker) throws TypingErrorException
    {
      trail(typeChecker);
      Scope scope = typeChecker.checkExitable(_extent);
      new FunctionType(scope.arity(),_type).setNoCurrying().unify(scope.typeRef(),typeChecker);
    }

  public final String toString ()
    {
      return super.toString() +
             ": (check exitable) return type => " + type() +
             ", extent => " + _extent;
    }
}
