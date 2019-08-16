//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Sun Oct 21 00:02:08 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

public abstract class NamedType extends StaticType
{
  protected String _name;

  public final String name ()
    {
      return _name;
    }

  public final void setName (String name)
    {
      _name = name;
    }

  public int eqCode ()
    {
      return kind() + _name.hashCode();
    }

  public String toString ()
    {
      return _name;
    }

}
