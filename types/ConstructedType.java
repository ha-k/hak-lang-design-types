//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.HashMap;

/**
 * This this the mother of all constructed type classes. A constructed type is a
 * type of the form
 * <tt>CONSTRUCTOR(...,e<sub>_i</sub>,...,T<sub>_j</sub>,....)</tt> where
 * <tt>CONSTRUCTOR</tt> is a type constructor and the <tt>e<sub>_i</sub></tt>'s
 * are expressions and <tt>T<sub>j</sub></tt>'s are types.
 */
public abstract class ConstructedType extends StaticType
{
}
