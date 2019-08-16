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
 * This is the class of time-stamped objects used in a <a
 * href="TypeChecker.html"> <tt>TypeChecker</tt></a> (<i>i.e.</i>, <a
 * href="Goal.html"><tt>Goal</tt></a>s and <a
 * href="TypingState.html"><tt>TypingState</tt></a>s).  */
abstract public class TimeStamp
{
  /**
   * This static field records temporal precedence of the creation of time stamps.
   */
  private static long _TIME_STAMP = 0;

  /**
   * Resets the temporal origin of time stamping to 0 for all time-stamped objects.
   */
  static final void reset ()
    {
      _TIME_STAMP = 0;
    }

  /**
   * Constructs a time stamp.
   */
  public TimeStamp ()
    {
    }

  /**
   * The time stamp of this object.
   */
  private long _timeStamp = _TIME_STAMP++;

  /**
   * Returns the time stamp of this object.
   */
  public final long timeStamp ()
    {
      return _timeStamp;
    }

  /**
   * Sets the time stamp of this object with the specified stamp.
   */
  public final TimeStamp setTimeStamp (long stamp)
    {
      _timeStamp = stamp;
      return this;
    }
  /**
   * Sets the time stamp of this object with that of the specified <tt>TimeStamp</tt> object.
   */
  public final TimeStamp setTimeStamp (TimeStamp timeStamp)
    {
      _timeStamp = timeStamp.timeStamp();
      return this;
    }
}



