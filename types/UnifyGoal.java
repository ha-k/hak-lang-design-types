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
 * A <tt>UnifyGoal</tt> encapsulates a pair of types that must be unified.
 * Such a goal is <i>proven</i> in the context of a <tt>TypeChecker</tt>
 * by unifying the two types.
 */
public class UnifyGoal extends Goal
{
  private Type _lhs;
  private Type _rhs;
  private Locatable _extent;

  public UnifyGoal (Type lhs, Type rhs)
    {
      _lhs = lhs;
      _rhs = rhs;
    }

  public UnifyGoal (Type lhs, Type rhs, Locatable extent)
    {
      _lhs = lhs;
      _rhs = rhs;
      _extent = extent;
    }

  final Locatable extent ()
    {
      return _extent;
    }

  final Type lhs ()
    {
      return _lhs.value();
    }

  final Type rhs ()
    {
      return _rhs.value();
    }

  void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      lhs().unify(rhs(),typeChecker);
    }

  public String toString ()
    {
      return super.toString() +
             ": (unify) lhs => " + lhs() +
             ", rhs => " + rhs();
    }
}
