//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.instructions.Instruction;


/**
 * This class extends <a href="CodeEntry.html"><tt>CodeEntry</tt></a>
 * for the specific case of built-in (as opposed to user-defined) code
 * entries.
 */
public class BuiltinEntry extends CodeEntry
{
  private Instruction _builtIn;         // this entry's built-in instruction
  
  public BuiltinEntry (Symbol symbol, Type type, Instruction builtIn)
    {
      _symbol = symbol;
      _type = type;
      _builtIn = builtIn;
    }

  public final Instruction builtIn ()
    {
      return _builtIn;
    }

  public final String toString ()
    {
      return "[builtin] "+super.toString();
    }
}
