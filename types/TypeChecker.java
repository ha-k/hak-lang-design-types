//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Sun Oct 21 04:32:17 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.kernel.Global;
import hlt.language.design.kernel.Expression;
import hlt.language.design.kernel.Application;
import hlt.language.design.kernel.Scope;

import hlt.language.util.Locatable;
import hlt.language.util.ViewableStack;
import hlt.language.util.Stack;
import hlt.language.util.Queue;
import hlt.language.tools.Debug;
import hlt.language.tools.Misc;

import java.util.Iterator;
import java.util.AbstractList;

/**
 * A <tt>TypeChecker</tt> is a backtracking prover that establishes <a
 * href="Goal.html"><tt>Goal</tt></a> objects.  The most common goal is a <a
 * href="TypingGoal.html"><tt>TypingGoal</tt></a>, and consists of an expression
 * and a type.  Proving a typing goal amounts to unifying its expression
 * component's type with its type component. Such goals are spawned by the type
 * checking method of expressions as per their type checking rules.
 *
 * <p>
 *
 * Some globally defined symbols having multiple types, it is necessary to keep
 * choices of these and backtrack to alternative types upon failure. Therefore, a
 * special goal is used to handle the typing of a <a
 * href="../kernel/Global.html"><tt>Global</tt></a>: a <a
 * href="GlobalTypingGoal.html"><tt>GlobalTypingGoal</tt></a>. Thus, a
 * <tt>TypeChecker</tt> object maintains all the necessary structures for undoing
 * the effects that happened since the latest choice point. These effects are:
 *
 * <ol>
 * <li> type variable binding,
 * <li> function type currying,
 * <li> application expression currying.
 * </ol>
 *
 * In addition, it is also necessary to remember all <tt>Goal</tt>s that
 * were proven since the latest choice point in order to prove them anew
 * upon backtracking to an alternative choice. This is necessary because
 * the goals are spawned by <tt>typeCheck</tt> method of expressions
 * that may be exited long before a failure occurs. Then, all the
 * original typing goals that were spawned in the mean time since the
 * current choice point's goal must be reestablished. This is why we use
 * a <i>goal trail</i> that remembers the sequence of goals that was
 * spawned.  In order for this to work, any choice points that were
 * associated to these original goals must also be reinitialized. All
 * this machinery (including choice point stack pushing and popping) is
 * appropriately set up and managed as it should be by <a
 * href="GlobalTypingGoal.html"><tt>GlobalTypingGoal</tt></a>s.
 *
 * <p>
 *
 * In order to maintain consistency of the effects of type checking, a
 * typechecker object is passed as an argument to all goal proving as
 * well as type unification methods to enable recording of any such
 * effect in the appropriate trail. These effects may then be
 * accordingly undone upon backtracking by <i>unwinding</i> the trails
 * back to where the latest choice point indicates.
 *
 * <p>
 *
 * To recapitulate, the structures of a <tt>TypeChecker</tt> object are:
 *
 * <ul>
 * <li> a <i>goal stack</i> containing <tt>Goal</tt> objects that
 *      are yet to be proven;<p>
 * <li> a <i>binding trail stack</i> containing type variables and boxing
 *      masks to reset to "unbound" upon backtracking;<p>
 * <li> a <i>function type currying trail</i> containing 4-tuples of
 *      the form (function type, previous domains, previous range, previous
 *      boxing mask) for resetting the function type to the recorded domains,
 *      range, and mask upon backtracking;<p>
 * <li> an <i>application currying trail</i> containing triples of
 *      the form (application type, previous function, previous arguments)
 *      for resetting the application to the recorded function and arguments
 *      upon backtracking;<p>
 * <li> a <i>goal trail</i> containing <tt>Goal</tt> objects that
 *      have been proven since the last choice point, and must be reproven
 *      upon backtracking;<p>
 * <li> a <i>choice-point stack</i> whose entries consists of <a href="ChoicePoint.html">
 *      <tt>ChoicePoint</tt></a> objects containing:
 *      <p>
 *      <ul>
 *      <li> a queue of <a href="CodeEntry.html"><tt>CodeEntry</tt></a> objects wherefrom to
 *           constructs new <tt>TypingGoal</tt> objects to try upon failure;
 *      <li> pointers to all trails up to which to undo effects (a <a href="TypingState.html">
 *           <tt>TypingState</tt></a>).
 *      </ul>
 * </ul>
 */

public class TypeChecker implements GoalProver
{
  /**
   * This is the goal stack - it contains the goals remaining to be proved.
   */
  private Stack _goalStack = new Stack();
  /**
   * This is the choice-point stack - it contains the alternative choices
   * remaining to be made.
   */
  private Stack _chptStack = new Stack();
  /**
   * This stack records the bindable objects that have been affected.
   */
  private Stack _bindTrail = new Stack();
  /**
   * This stack records the components of function types affected by currying.
   */
  private Stack _typeTrail = new Stack();
  /**
   * This stack records the components of application expressions affected by
   * currying.
   */
  private Stack _applTrail = new Stack();
  /**
   * This stack records the goals that have been proven up to now.
   */
  private Stack _goalTrail = new Stack();
  /**
   * This stack records cut-point states of the type-checker before the latest
   * of which it will not backtrack.
   */
  private Stack _ctptStack = new Stack();
  /**
   * This stack records the exitable scopes that have been entered/exited.
   */
  private Stack _exitStack = new Stack();

  private boolean _tracing = false;

  public final boolean isTracing ()
    {
      return _tracing;
    }

  public final void setTracing (boolean flag)
    {
      if (flag) Type.resetNames();
      _tracing = flag;
    }

  public final void toggleTrace ()
    {
      setTracing(!_tracing);
    }

  /**
   * This is a static boolean switch to make type clash errors give the actual incompatible
   * types that it found. Due to backtracking over overloaded types, this information is often
   * wrong, and so it is not reported when this flag is <tt>false</tt> (default).
   */
  public static boolean GIVES_DETAILS = false;

//    /**
//     * This is a static boolean switch to dis/allow unification of int <i>vs.</i> real
//     * types; default is <tt>false</tt>.
//     */
//    public static boolean ALLOWS_LIBERAL_NUMBER_TYPING = false;

  /**
   * This is a boolean switch to dis/allow unification of position <i>vs.</i> named
   * tuple types; default is <tt>false</tt>.
   */
  public static boolean ALLOWS_POSITIONAL_NAMED_TUPLES = false;

  /**
   * This is a boolean switch to dis/allow unification of opaquely defined tuple types
   * against plain tuple types; default is <tt>false</tt>.
   */
  public static boolean ALLOWS_UNIFYING_OPAQUE_TUPLES = false;

  /**
   * Checks that the specified extent is within an exitable scope, and if
   * it is returns that scope. Otherwise, tiggers a typing error.
   */
  final Scope checkExitable (Locatable extent) throws TypingErrorException
    {
      if (_exitStack.isEmpty())
        error(new TypingErrorException("not within an exitable scope"),extent);

      return (Scope)_exitStack.peek();
    }

  /**
   * Pushes the specified goal on the goal stack.
   */
  public final void pushGoal (Goal goal)
    {
      _goalStack.push(goal);
    }

  /**
   * Pushes the specified exitable scope onto the exitable scope stack.
   */
  public final void pushExitable (Scope scope)
    {
      _exitStack.push(scope);
    }

  /**
   * Pops the latest exitable scope.
   */
  public final void popExitable ()
    {
      _exitStack.pop();
    }

  /**
   * Pushes the current typing state on the cut-point stack.
   */
  public final void pushCutPoint ()
    {
      _ctptStack.push(getTypingState());
    }

  /**
   * Pops and returns the latest typing state on the cut-point stack.
   */
  public final TypingState popCutPoint ()
    {
      return (TypingState)_ctptStack.pop();
    }

  /**
   * Pushes the specified <tt>Bindable</tt> object on the binding trail.
   */
  public final void trail (Bindable bindable)
    {
      _bindTrail.push(bindable);
    }

  /**
   * Pushes the specified four objects on the type trail (corresponding to
   * the form of a function type before being curryed).
   */
  final void trail (FunctionType type, Type[] domains, Type range, BoxingMask mask)
    {
      _typeTrail.push(mask);
      _typeTrail.push(range);
      _typeTrail.push(domains);
      _typeTrail.push(type);
    }

  /**
   * Pushes the specified four objects on the application trail (corresponding to
   * the form of an application before being curryed).
   */
  public final void trail (Application application, Expression function, Expression[] arguments)
    {
      _applTrail.push(arguments);
      _applTrail.push(function);
      _applTrail.push(application);
    }

  /**
   * Pushes the specified goal on the goal trail.
   */
  public final void trail (Goal goal)
    {
      _goalTrail.push(goal);
    }

  /**
   * Triggers the unification of the two specified types by proving a
   * <a href="UnifyGoal.html"><tt>UnifyGoal</tt></a> constructed with the
   * two types.
   */
  public final void unify (Type t1, Type t2) throws TypingErrorException
    {
      prove(new UnifyGoal(t1,t2));
    }

  /**
   * Triggers the unification of the two specified types by proving a
   * <a href="UnifyGoal.html"><tt>UnifyGoal</tt></a> constructed with the
   * two types and recording the specified location extent.
   */
  public final void unify (Type t1, Type t2, Locatable extent) throws TypingErrorException
    {
      prove(new UnifyGoal(t1,t2,extent));
    }

  /**
   * Proves a <a href="TypingGoal.html"><tt>TypingGoal</tt></a> constructed
   * with the specified expression and type.
   */
  public final void typeCheck (Expression expression, Type type) throws TypingErrorException
    {
      prove(new TypingGoal(expression,type));
    }

  /**
   * Triggers the proving of a <a href="PruningGoal.html"><tt>PruningGoal</tt></a>
   * for the specified global an filtering type, and records the specified location
   * extent into the goal.
   */
  public final void prune (Global global, Type filter, Locatable extent) throws TypingErrorException
    {
      prove(new PruningGoal(global,filter,extent));
    }

  /**
   * Constructs and proves a residuation wrapper for the specified goal in the
   * form of a <a href="ResiduatedGoal.html"><tt>ResiduatedGoal</tt></a> on
   * which the specified type is added as a trigger. Adding a type as a trigger
   * on a residuated goal enables control of the goal's proving based on the
   * type's state of instantiation: even though the <tt>prove</tt> method is called
   * immediately after on this goal, the effective proving happens only when the
   * triggering type becomes fully instantiated.
   */
  public final void residuate (Type type, Goal goal) throws TypingErrorException
    {
      ResiduatedGoal residuation = new ResiduatedGoal(goal);
      residuation.addTrigger(type);
      prove(goal);
    }

  /**
   * Proves a residuated <a href="NoVoidTypeGoal.html"><tt>NoVoidTypeGoal</tt></a>
   * for the specified type. In other words, this prevents the specified type from
   * ever being instantiated to void. The specified extent locates the goal and the
   * <tt>info</tt> is used to describe the specific reason for disallowing void.
   */
  public final void disallowVoid (Type type, Locatable extent, Object info) throws TypingErrorException
    {
      residuate(type,new NoVoidTypeGoal(type,extent,": "+info));
    }

  /**
   * Proves the specfied goal by pushing it on the goal stack and calling the
   * <tt>_typecheck()</tt> method.
   */
  public final void prove (Goal goal) throws TypingErrorException
    {
      _goalStack.push(goal);
      _typeCheck();
    }

  /**
   * This method pops and proves the goals from the goal stack as long as it is not
   * empty. Proving a goal simply delegates the work to the goal's <tt>prove</tt>
   * method taking <tt>this</tt> as argument. It is done is a <tt>try/catch</tt>
   * form wherein a <tt>FailedUnificationException</tt> will trigger backtracking.
   * Note that this method will stop when the goal stack is empty, not when all
   * possible typings have been found. In other words, it ensures that all the
   * goals have been proven once (thereby finding a most general consistent typing)
   * and leaves the typechecker in a state wherefrom it may resume further work if
   * needed.
   */
  private final void _typeCheck () throws TypingErrorException
    {
      while (!_goalStack.isEmpty())
        {
          _currentGoal   = (Goal)_goalStack.pop();
          _currentExtent = _currentGoal.extent();

          try
            {
              if (_tracing)
                {
                  if (!(_currentGoal instanceof GlobalTypingGoal)) showGoal(_currentGoal);
                  _showState();
                }

              _currentGoal.prove(this);

              if (_tracing)
		_showStep("Goal "+_currentGoal.timeStamp()+" succeeded; proceeding...");
            }
          catch (FailedUnificationException error)
            {
              if (_tracing)
		_showStep("Goal "+_currentGoal.timeStamp()+" failed: "+error.msg());

              _backtrack();
            }
        }
    }

  /**
   * Performs an exhaustive type checking of the specifed expression, recording
   * the types it finds in the specified list.
   */
  public final void allTypes (Expression expression, AbstractList types)
    {
      try
        {
	  // find a first type if possible, and if so, record it...
          expression.typeCheck(this);
          types.add(expression.type().copy());

          if (_tracing)
	    _showStep("Types so far: " + types);
        }
      catch (TypingErrorException error)
	// not even one type could be found: bail out...
        {
          return;
        }

      for (;;)
	// backtrack, find, and record new types, as long as we can carry on...
        try
          {
            if (_tracing)
	      _showStep("Backtracking to find more types...");

            _backtrack();
            _typeCheck();
            types.add(expression.type().copy());

            if (_tracing)
	      _showStep("Types so far: " + types);
          }
        catch (TypingErrorException error)
          {
            return;
          }
    }      

  /**
   * Finds all remaining types for the specifed expression starting in the current
   * typing state of this typechecker, recording the types it finds in the specified
   * list.
   */
  public final void remainingTypes (Expression expression, AbstractList types)
    {
      for (;;)
	// backtrack, find, and record new types, as long as we can carry on...
        try
          {
            if (_tracing)
              _showStep("Found type "+expression.type()+"; looking for more types...");

            _backtrack();
            _typeCheck();
            types.add( expression.type().copy());

            if (_tracing)
	      _showStep("Types so far: " + types);
          }
        catch (TypingErrorException error)
          {
            return;
          }
    }

  /**
   * Returns the current <a href="TypingState.html"><tt>TypingState</tt></a>
   * of this typechecker.
   */
  public final TypingState getTypingState ()
    {
      return new TypingState().save(_goalStack.size(),_chptStack.size(),_bindTrail.size(),
                                    _typeTrail.size(),_applTrail.size(),_goalTrail.size());
    }

  /**
   * Pushes the current state of this typechecker as a choice point onto the
   * choice point stack.
   */
  final void pushChoicePoint (ChoicePoint chpt)
    {
      _chptStack.push(chpt.save(_goalStack.size(),_chptStack.size(),_bindTrail.size(),
                                _typeTrail.size(),_applTrail.size(),_goalTrail.size()));
    }

  /**
   * Pops the choice point stack.
   */
  final void popChoicePoint ()
    {
      _chptStack.pop();
    }

  /**
   * Assumes that the choice point stack is not empty and returns the latest
   * choice point.
   */
  private final ChoicePoint _getChoicePoint ()
    {
      return (ChoicePoint)_chptStack.peek();
    }

  /**
   * Assumes that the cut point stack is not empty and returns the latest
   * cut point.
   */
  private final TypingState _getCutPoint ()
    {
      return (TypingState)_ctptStack.peek();
    }

  /**
   * Puts this type checker in a "virginal" state (<i>i.e.</i>, as if it had
   * just been constructed).
   */
  public final TypeChecker reset ()
    {
      _GENERIC_ERROR.setExtent(_currentExtent);
      _currentExtent = null;
      _error = null;
      _clearAllStacks();
      return this;
    }

  /**
   * Clears the contents of all the stacks.
   */
  private final void _clearAllStacks ()
    {
      _goalStack.clear();
      _chptStack.clear();
      _exitStack.clear();
      _goalTrail.clear();
      _unwindBindTrail();
      _unwindTypeTrail();
      _unwindApplTrail();
    }

  //\\//\\//\\//\\ Effects undoing code

  public final void undoCutPoint ()
    {
      if (_tracing)
        {
          System.out.println("Undoing cut point from state:");
          _showState();
        }

      TypingState ctpt = popCutPoint();

      while (!_goalTrail.isEmpty()
	     && ((Goal)_goalTrail.peek()).timeStamp() > ctpt.timeStamp())
        _goalTrail.pop();
      
      _unwindBindTrail(ctpt.bindTrailPoint());
      _unwindTypeTrail(ctpt.typeTrailPoint());
      _unwindApplTrail(ctpt.applTrailPoint());
    }

  private final boolean _noMoreChoices ()
    {
      return _chptStack.isEmpty()
          || !_ctptStack.isEmpty()
          && _getChoicePoint().timeStamp() < _getCutPoint().timeStamp();
    }

  private final void _backtrack () throws TypingErrorException
    {
      if (_noMoreChoices())
          reportError();

      ChoicePoint chpt = (ChoicePoint)_chptStack.peek();
      
      _unwindGoalTrail(chpt.timeStamp());
      _unwindBindTrail(chpt.bindTrailPoint());
      _unwindTypeTrail(chpt.typeTrailPoint());
      _unwindApplTrail(chpt.applTrailPoint());

      if (_tracing) _show("Retrying Goal " + chpt.timeStamp() + " ...");
    }

  private final void _unwindBindTrail ()
    {
      _unwindBindTrail(0);
    }

  private final void _unwindBindTrail (int point)
    {
      while (_bindTrail.size() > point)
        ((Bindable)_bindTrail.pop()).unbind();
    }

  private final void _unwindTypeTrail ()
    {
      _unwindTypeTrail(0);
    }

  private final void _unwindTypeTrail (int point)
    {
      while (_typeTrail.size() > point)
        {
          FunctionType type = (FunctionType)_typeTrail.pop();
          type.setDomains((Type[])_typeTrail.pop());
          type.setRange((Type)_typeTrail.pop());
          type.setMask((BoxingMask)_typeTrail.pop());
        }
    }

  private final void _unwindApplTrail ()
    {
      _unwindApplTrail(0);
    }

  private final void _unwindApplTrail (int point)
    {
      while (_applTrail.size() > point)
        {
          Application application = (Application)_applTrail.pop();
          application.setFunction((Expression)_applTrail.pop());
          application.setArguments((Expression[])_applTrail.pop());
        }
    }

  private final void _unwindGoalTrail (long stamp)
    {
      while (((Goal)_goalTrail.peek()).timeStamp() > stamp)
        ((Goal)_goalTrail.pop()).undo(this);

      _goalStack.push(_goalTrail.pop());      
    }

  //\\//\\//\\//\\ Error handling code

  private static final TypingErrorException _GENERIC_ERROR
    = new TypingErrorException("ill-typed form");
  private StaticSemanticsErrorException _error;
  private Goal _currentGoal;
  private Locatable _currentExtent;

  private final void _assessCulprit (StaticSemanticsErrorException error)
    {
      if (_error == null || _currentGoal.timeStamp() > _error.stamp())
        {
          _error = error.setExtent(_currentExtent);
          if (_currentGoal != null)
            _error.setStamp(_currentGoal.timeStamp());
        }
    }

  public final void error (StaticSemanticsErrorException error)
    throws StaticSemanticsErrorException
    {
      _assessCulprit(error);
      throw _error;
    }

  public final void error (StaticSemanticsErrorException error, Locatable locatable)
    throws StaticSemanticsErrorException
    {
      _currentExtent = locatable;
      error(error);
    }

  public final void reportError () throws StaticSemanticsErrorException
    {
      StaticSemanticsErrorException error = _error;

      //      if (_ctptStack.isEmpty()) reset();
      
      throw error != null ? error : _GENERIC_ERROR;
    }    

  //\\//\\//\\//\\//\\ State showing code

  final private void _showState ()
    {
      if (_tracing) _show(_showNonEmpty(_goalStack,"        Goal stack")+
                    _showNonEmpty(_goalTrail,"        Goal trail")+
                    _showNonEmpty(_exitStack,"        Exit stack")+
                    _showNonEmpty(_ctptStack,"   Cut point stack")+
                    _showNonEmpty(_chptStack,"Choice point stack")+
                    _showChoicePointEntries());
    }

  final private String _showNonEmpty (ViewableStack stack, String header)
    {
      return stack.isEmpty() ? "" : Misc.view(stack,header,0,100);
    }

  final private String _showChoicePointEntries ()
    {
      if (_chptStack.isEmpty()) return "";

      StringBuilder buf = new StringBuilder();
      
      for (int i=0; i < _chptStack.size(); i++)
        {
          ChoicePoint chpt = (ChoicePoint)_chptStack.get(i);
          buf.append(Misc.view(chpt.entries(),
                               "Next choices for goal "+chpt.timeStamp(),
                               0,86));
        }

      return buf.toString();
    }

  private final void _show (String s)
    {
      System.out.println(s);
    }

  private final void _showStep (String s)
    {
      Debug.step(s);
    }

  final void showGoal (Goal goal)
    {
      if (_tracing) _show(Misc.etc(120,"Currently attempting ==> "+goal));
    }

}
