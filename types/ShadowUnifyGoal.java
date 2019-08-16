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
 * A <tt>ShadowUnifyGoal</tt> extends a <a href="UnifyGoal.html"><tt>UnifyGoal</tt></a>
 * in that it unifies the <a href="Type.html#shadowType"><tt>shadow types</tt></a>
 * of the types themselves. This type of goal is used essentially for type checking
 * a <a href="../kernel/Homomorphism.html#typeChecking"><tt>Homomorphism</tt></a>.
 */

public class ShadowUnifyGoal extends UnifyGoal
{
  public ShadowUnifyGoal (Type lhs, Type rhs)
    {
      super(lhs,rhs);
    }

  public ShadowUnifyGoal (Type lhs, Type rhs, Locatable extent)
    {
      super(lhs,rhs,extent);
    }

  final void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      lhs().shadowType().unify(rhs().shadowType(),typeChecker);
    }

  final public String toString ()
    {
      return "Goal " + timeStamp() +
             ": (shadow unify) lhs => "+ lhs() + " (shadow => " + lhs().shadowType() + 
                           "), rhs => "+ rhs() + " (shadow => " + rhs().shadowType() + ")";
                                     
    }
}
