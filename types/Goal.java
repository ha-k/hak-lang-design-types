//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Thu Mar 24 12:27:31 2016 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.tools.Misc;
import hlt.language.util.Locatable;

/**
 * A <tt>Goal</tt> is anything that may be established and undone by a
 * <a href="TypeChecker.html"><tt>TypeChecker</tt></a>.
 * Here are the different types of goals:
 * <ul>
 * <li> <a href="BaseTypeGoal.html"><tt>BaseTypeGoal</tt></a>,
 * <li> <a href="ArrayIndexTypeGoal.html"><tt>ArrayIndexTypeGoal</tt></a>,
 * <li> <a href="CheckExitableGoal.html"><tt>CheckExitableGoal</tt></a>,
 * <li> <a href="EmptyGoal.html"><tt>EmptyGoal</tt></a>,
 * <li> <a href="GlobalTypingGoal.html"><tt>GlobalTypingGoal</tt></a>,
 * <li> <a href="PopExitableGoal.html"><tt>PopExitableGoal</tt></a>,
 * <li> <a href="PruningGoal.html"><tt>PruningGoal</tt></a>,
 * <li> <a href="PushExitableGoal.html"><tt>PushExitableGoal</tt></a>,
 * <li> <a href="ResiduatedGoal.html"><tt>ResiduatedGoal</tt></a>,
 * <li> <a href="ShadowUnifyGoal.html"><tt>ShadowUnifyGoal</tt></a>,
 * <li> <a href="TypingGoal.html"><tt>TypingGoal</tt></a>,
 * <li> <a href="UnifyBaseTypeGoal.html"><tt>UnifyBaseTypeGoal</tt></a>,
 * <li> <a href="UnifyGoal.html"><tt>UnifyGoal</tt></a>,
 * <li> <a href="NoVoidTypeGoal.html"><tt>NoVoidTypeGoal</tt></a>.
 * </ul>
 */

abstract public class Goal extends TimeStamp
{
  private boolean _isTrailable = true;

  final void setIsTrailable (boolean isTrailable)
    {
      _isTrailable = isTrailable;
    }

  final void trail (TypeChecker typeChecker)
    {
      if (_isTrailable)
        typeChecker.trail(this);
    }

  /**
   * This method is called by the type checker to try and establish this goal.
   */       
  abstract void prove (TypeChecker typeChecker) throws StaticSemanticsErrorException;

  /**
   * This method is called by the type checker upon popping it from the goal trail.
   * By default, it simply pushes this goal on the typechecker's goal stack.
   */       
  void undo (TypeChecker typeChecker)
    {
      typeChecker.pushGoal(this);
    }

  /**
   * Returns this goal's location's extent as a <a href="../../util/Locatable.html">
   * <tt>Locatable</tt></a>.
   */
  abstract Locatable extent ();

  public String toString ()
    {
      return /* Misc.locationString(extent()) + */ "Goal " + timeStamp();
    }
}
