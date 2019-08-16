//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

public class TypeClashException extends FailedUnificationException
{
  public TypeClashException (Object detail)
    {
      _msg += "incompatible type";

      if (TypeChecker.GIVES_DETAILS)
        {
          Type.resetNames();
          _msg += ": "+detail;
        }
    }

  public TypeClashException (Type expected, Type found)
    {
      _msg += "incompatible type";

      if (TypeChecker.GIVES_DETAILS)
        {
          Type.resetNames();
          _msg += ": "+expected.toQuantifiedString()+", "+found.toQuantifiedString();
        }
    }
}
