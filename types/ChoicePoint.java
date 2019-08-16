//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.util.Queue;
import hlt.language.tools.Misc;
import hlt.language.design.kernel.Global;

/**
 * A <tt>ChoicePoint</tt> extends a <a href="TypingState.html"><tt>TypingState</tt></a>
 * with a queue of <a href="CodeEntry.html"><tt>CodeEntry</tt></a> objects.
 */

public class ChoicePoint extends TypingState
{
  private Queue _entries = new Queue();

  final boolean isEmpty ()
    {
      return _entries.isEmpty();
    }

  final CodeEntry pop ()
    {
      return (CodeEntry)_entries.pop();
    }

  final void push (CodeEntry entry)
    {
      _entries.push(entry);
    }

  final Queue entries ()
    {
      return _entries;
    }

  final public String toString ()
    {
      return "Choice point " + timeStamp() + " ("+_entries.size()+" entr"+
             (_entries.size() > 1 ? "ies" : "y") + " remaining)";
    }
}
