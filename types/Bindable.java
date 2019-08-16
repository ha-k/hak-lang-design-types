//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

/**
 * This is the interface of bindable objects. A bindable object is an
 * instance of a <a href="Valuable.html"><tt>Valuable</tt></a> that may
 * itself have another <tt>Valuable</tt> as value.
 */
public interface Bindable extends Valuable
{
  /**
   * Returns <tt>true</tt> iff this bindable is bound.
   */
  public boolean isBound ();

  /**
   * Returns <tt>true</tt> iff this bindable is unbound.
   */
  public boolean isUnbound ();

  /**
   * Binds this bindable to the given valuable.
   */
  public boolean bind (Valuable value);

  /**
   * Binds this bindable to the given valuable in the context of the
   * given goal prover. This binding is appropriately trailed in the
   * contextual prover for undoing purposes.
   */
  public boolean bind (Valuable value, GoalProver prover);

  /**
   * Unbinds this bindable.
   */
  public void unbind ();
}
