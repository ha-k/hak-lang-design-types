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
import hlt.language.design.kernel.Global;

/**
 * A <tt>GlobalTypingGoal</tt> manages the typing of <a
 * href="../kernel/Global.html"> <tt>Global</tt></a>s. Because of type
 * overloading, a global may have more than one possible
 * type. Therefore, this kind of goal is responsible for appropriately
 * creating a choice point when needed for a global and identifying
 * itself with each typing goal choice in turn. Therefore, it is simply
 * a wrapper for one, or a disjunction of several, <a
 * href="TypingGoal.html"><tt>TypingGoal</tt></a>s and takes care of
 * being identified to one disjunct after the other if backtracking is
 * needed. It does so by interacting with the typechecker, pushing and
 * popping choice points as needed.
 */
public class GlobalTypingGoal extends Goal
{
  /**
   * The <a href="../kernel/Global.html"><tt>Global</tt></a> being type-checked.
   */
  private Global _global;

  /**
   * The goal disjunct this is currently identified with (<i>i.e.</i>, the current choice).
   */
  private TypingGoal _currentGoal;

  /**
   * A flag to mark this as having been initialized or not.
   */
  private boolean _initialized = false;

  /**
   * If non-null, contains the alternative entries to choose from.
   */
  private ChoicePoint _choicePoint;

  /**
   * Constructs a global typing goal with the specified <a href="../kernel/Global.html">
   * <tt>Global</tt></a>.
   */
  public GlobalTypingGoal (Global global)
    {
      _global = global;
    }

  final Locatable extent ()
    {
      return _global;
    }

  /**
   * Returns this goal choice point. A single reusable structure is used.
   */
  private final ChoicePoint _choicePoint ()
    {
      if (_choicePoint == null) _choicePoint = new ChoicePoint();
      return _choicePoint;
    }

  /**
   * Initializes this global typing goal by computing the global's viable types (as per
   * the global's current filter type). If no viable type is found, a typing error is
   * signalled. If there are viable types, the first one is used to set the current
   * goal to an appropriate <a href="TypingGoal.html"><tt>TypingGoal</tt></a> with
   * the global as expression and the first viable type as type. If there are more
   * than one viable types, a choice point is created containing the second to last
   * such types, and it is pushed on the typechecker's choice point stack. In all
   * cases, this global typing goal is marked as initialized. This mark is to be reset
   * to false upon backtracking prior to this goal in order to recreate the full choice
   * point as necessary. This is done by the <tt>undo</tt> method which is called on
   * by the typechecker on all trailed goals as it pops them while unwinding its goal
   * trail.
   */
  private final void _initialize (TypeChecker typeChecker) throws FailedUnificationException
    {
      Iterator i = _global.viableTypes().iterator();

      if (!i.hasNext())
        {
          Type.resetNames();
          typeChecker.error(new TypeClashException("no type allowed for '"+_global+"' fits "+
                                                   _global.sieve().toQuantifiedString()),
                            _global);
        }

      _setCurrentGoal((CodeEntry)i.next());
      if (i.hasNext())
        {
          _choicePoint().setTimeStamp(this);
          while (i.hasNext())
            _choicePoint.push((CodeEntry)i.next());
          typeChecker.pushChoicePoint(_choicePoint);
        }

      _initialized = true;
    }
  
  /**
   * Sets the current goal to a new <a href="TypingGoal.html"><tt>TypingGoal</tt></a>
   * constructed from the specified <a href="CodeEntry.html"><tt>CodeEntry</tt></a>
   * corresponding to one viable typing for the global.
   */
  private final void _setCurrentGoal (CodeEntry entry)
    {
      _currentGoal = new TypingGoal(_global,entry.type().copy());
      _currentGoal.setIsTrailable(false);
      _currentGoal.setTimeStamp(this);
      _global.setCodeEntry(entry);
    }
  
  /**
   * Resets the current goal to a new <a href="TypingGoal.html"><tt>TypingGoal</tt></a>
   * constructed from the next <a href="CodeEntry.html"><tt>CodeEntry</tt></a> available
   * from the choice point. If this next choice is the last one, this will also pop the
   * typechecker's choice stack.
   * <p>
   * Note that there is necessarily a non-empty choice point when this is executed. Indeed,
   * execution gets here only when this goal is initialized (see the <tt>prove</tt> method
   * below). Therefore, if only one type was found to be viable in <tt>_initialize</tt>, no
   * choice point would have been created, and so backtracking - if any - would be to a goal
   * prior to this, at which point unwinding it from the goal trail would reset its mark to
   * uninitialized.
   */
  private final void _backtrack (TypeChecker typeChecker)
    {
      _setCurrentGoal(_choicePoint.pop());

      if (_choicePoint.isEmpty())
        typeChecker.popChoicePoint();
    }

  /**
   * Proving this kind of goal proceeds as follows:
   * <ul>
   * <li> trail this goal in the typechecker;
   * <li> if it is not initialized, initialize it (which sets the current goal to the first
   *      viable choice)
   * <li> otherwise, backtrack (which sets the current goal to the next viable choice -
   *      which necessarily exists as explained for the method <tt>_backtrack</tt>)
   * <li> prove the current goal.
   * </ul>
   */
  final void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);

      if (!_initialized)
        _initialize(typeChecker);
      else
        _backtrack(typeChecker);

      if (typeChecker.isTracing())
	typeChecker.showGoal(_currentGoal);

      _currentGoal.prove(typeChecker);
    }

  /**
   * Undoing this kind of goal amounts to marking it as uninitialized and pushing it
   * on the goal stack.
   */
  final void undo (TypeChecker typeChecker)
    {
      _initialized = false;
      typeChecker.pushGoal(this);
    }

  /**
   * Returns a string form of this typing goal.
   */
  final public String toString ()
    {
      return _initialized
           ? _currentGoal.toString()
           : super.toString() + ": (global) symbol => " + _global;
    }
}



