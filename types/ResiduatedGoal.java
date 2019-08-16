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

/**
 * An <tt>ResiduatedGoal</tt> is a wrapper for a goal goal that is effectively proven
 * only when it contains no unbound type parameters.
 */

public class ResiduatedGoal extends Goal
{
  /**
   * The goal controlled by this residuation.
   */
  private Goal _goal;

  /**
   * The number of unbound type parameters in this residuation.
   */
  private int _refCount = 0;

  /**
   * Constructs a residuation with the specified goal.
   */
  public ResiduatedGoal (Goal goal)
    {
      setGoal(goal);
    }

  /**
   * Returns the goal controlled by this residuation.
   */
  final Goal goal ()
    {
      return _goal;
    }

  /**
   * Returns the extent of the goal controlled by this residuation.
   */
  final Locatable extent ()
    {
      return _goal.extent();
    }

  /**
   * Returns the number of unbound type parameters in this residuation.
   */
  public final int refCount ()
    {
      return _refCount;
    }

  /**
   * Sets the goal controlled by this residuation to be the specified goal, and
   * returns this residuation. Also ensures that the specified goal will not be
   * trailed.
   */
  public final ResiduatedGoal setGoal (Goal goal)
    {
      _goal = goal;
      _goal.setTimeStamp(this);
      _goal.setIsTrailable(false);
      return this;
    }

  /**
   * Sets the number of unbound type parameters in this residuation.
   */
  public final void setRefCount (int n)
    {
      _refCount = n;
    }

  /**
   * Installs a trigger link to this residuation from each unbound type parameter that
   * occurs in the specified type.
   */
  public final void addTrigger (Type type)
    {
      int savedRefCount = _refCount;
      for (Iterator i=type.value().getParameters().iterator(); i.hasNext();)
        if (((TypeParameter)i.next()).addResiduation(this,savedRefCount))
          _refCount++;
    }

  /**
   * Releases this residuation for the specified type in the context of the specified
   * goal prover.
   */
  public final void release (Type type, GoalProver prover) throws FailedUnificationException
    {
      _refCount--;
      addTrigger(type);
      trigger(prover);
    }

  /**
   * Proves this residuation in the context of the specified typechecker.
   */
  final void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      trigger(typeChecker);
    }

  /**
   * Triggers this residuation in the context of the specified goal prover.
   */
  public final void trigger (GoalProver prover) throws FailedUnificationException
    {
      if (_refCount == 0)
        _goal.prove((TypeChecker)prover);
    }

  /**
   * Returns a printable form for this residuation.
   */
  public String toString ()
    {
      return _refCount == 0 ? _goal.toString()
                            : super.toString() + ": (residuation) refs => " + _refCount
                                               + ", goal => " + _goal;
    }
}
