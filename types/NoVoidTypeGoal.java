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
 * A <tt>NoVoidType</tt> fails when its type component is void. This is typically
 * residuated.
 */

public class NoVoidTypeGoal extends Goal
{
  private Type _type;
  private Locatable _extent;
  private String _info = "";

  public NoVoidTypeGoal (Type type, Locatable extent, String info)
    {
      _type = type;
      _extent = extent;
      _info = info;
    }

  public NoVoidTypeGoal (Type type, Locatable extent)
    {
      _type = type;
      _extent = extent;
    }

  final Locatable extent ()
    {
      return _extent;
    }

  final Type type ()
    {
      return _type.value();
    }

  void prove (TypeChecker typeChecker) throws FailedUnificationException
    {
      trail(typeChecker);
      if (type().isVoid())
        typeChecker.error(new TypingErrorException("illegal void type"+_info),_extent);
    }

  public String toString ()
    {
      return super.toString() +
             ": (no void type) type => " + type() +
             ", extent => " + extent();
    }
}
