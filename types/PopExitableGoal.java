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
 * A <tt>PopExitableGoal</tt> simply pops the typechecker's stack of exitable scopes.
 */

public class PopExitableGoal extends Goal
{
  private Scope _scope;

  public PopExitableGoal (Scope scope)
    {
      _scope = scope;
    }

  final Locatable extent ()
    {
      return _scope;
    }

  final Scope scope ()
    {
      return _scope;
    }

  final void prove (TypeChecker typeChecker)
    {
      trail(typeChecker);
      typeChecker.popExitable();
    }

  final void undo (TypeChecker typeChecker)
    {
      typeChecker.pushExitable(_scope);
      typeChecker.pushGoal(this);
    }

  public String toString ()
    {
      return super.toString() +
             ": (pop exitable) scope => "+ _scope;
    }
}
