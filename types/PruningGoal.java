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
import hlt.language.design.kernel.Global;

/**
 * The meaning of a <tt>PruningGoal</tt> is to unify the filter field of a
 * specified <tt><a href="../kernel/Global.html">Global</a></tt> with the
 * specified filter. The purpose is to allow narrowing the sets of viable
 * types for the global when this global is subsequently typechecked (which
 * is done by a  <tt><a href="GlobalTypingGoal.html">GlobalTypingGoal</a></tt>).
 */
public class PruningGoal extends Goal
{
  /**
   * The global being pruned.
   */
  private Global _global;

  /**
   * The type filter.
   */
  private Type _filter;

  /**
   * The locatable extent.
   */
  private Locatable _extent;

  public PruningGoal (Global global, Type filter, Locatable extent)
    {
      _global = global;
      _filter = filter;
      _extent = extent;
    }

  /**
   * Returns this pruning goal's global.
   */
  final Global global ()
    {
      return _global;
    }

  /**
   * Returns this pruning goal's (undereferenced) filter.
   */
  final Type filter ()
    {
      return _filter;
    }

  final Locatable extent ()
    {
      return _extent;
    }

  /**
   * Trails this goal in the specified typechecker and unifies this goal's
   * global's filter with this goal's filter in the context of the typechecker.
   * NB: the effect of this filtering unification are effective in any subsequent
   * typechecking, but will be undone upon backtracking before this goal. The
   * trailing if this goal ensures that a new filter will be computed on different
   * branches of the backtracking search.
   */
  final void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      _global.filter().unify(_filter,typeChecker);
    }

  /**
   * Returns a string form of this typing goal.
   */
  final public String toString ()
    {
      return super.toString() +
             ": (pruning) global => " + _global +
             ", filter => " + _filter.value();
    }
}
