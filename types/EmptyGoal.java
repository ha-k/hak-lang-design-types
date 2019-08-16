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

/**
 * An <tt>EmptyyGoal</tt> is the trivial always <tt>true</tt> goal.
 * It is useful for marking the typechecker's goal stack at a given
 * place that needs to be recognized.
 */
public class EmptyGoal extends Goal
{
  private Locatable _extent;

  public EmptyGoal (Locatable extent)
    {
      _extent = extent;
    }

  final Locatable extent ()
    {
      return _extent;
    }

  final void prove (TypeChecker typeChecker)
    {
      trail(typeChecker);
    }

  public String toString ()
    {
      return super.toString() +
             ": (empty) extent => "+ _extent;
    }
}
