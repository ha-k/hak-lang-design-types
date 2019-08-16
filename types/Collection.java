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
 * This is an interface for types that are collections of a certain base type.
 */
public interface Collection
{
  /**
   * Returns the undereferenced base type.
   */
  public Type baseTypeRef ();

  /**
   * Returns the base type's value; that is, the current type binding of the
   * collection's elements.
   */
  public Type baseType ();
}
