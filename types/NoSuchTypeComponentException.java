//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.tools.Misc;

public class NoSuchTypeComponentException extends RuntimeException
{

  private String _msg = "(no such type component) ";

  public NoSuchTypeComponentException (Type type, int position)
    {
      _msg += Misc.simpleClassName(type)
            + " has no type component at position: " + position;
    }

  public final String getMessage ()
    {
      return _msg;
    }
}


