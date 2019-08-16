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
 * A <tt>TypingState</tt> consists of the following pointers, each recording
 * the size of the corresponding stack in the <a href="TypeChecker.html">
 * <tt>TypeChecker</tt></a> at state-saving time:
 * <ul>
 * <li> a <tt>goalStack</tt> pointer;
 * <li> a <tt>chptStack</tt> pointer;
 * <li> a <tt>bindTrail</tt> pointer;
 * <li> a <tt>typeTrail</tt> pointer;
 * <li> a <tt>applTrail</tt> pointer;
 * <li> a <tt>goalTrail</tt> pointer;
 * </ul>
 */

public class TypingState extends TimeStamp
{
  private int _goalStackPoint;
  private int _chptStackPoint;
  private int _bindTrailPoint;
  private int _typeTrailPoint;
  private int _applTrailPoint;
  private int _goalTrailPoint;

  final TypingState save (int goalStackPoint,
                          int chptStackPoint,
                          int bindTrailPoint,
                          int typeTrailPoint,
                          int applTrailPoint,
                          int goalTrailPoint)
    {
      _goalStackPoint = goalStackPoint;
      _chptStackPoint = chptStackPoint;
      _bindTrailPoint = bindTrailPoint;
      _typeTrailPoint = typeTrailPoint;
      _applTrailPoint = applTrailPoint;
      _goalTrailPoint = goalTrailPoint;
      return this;
    }
    
  final int goalStackPoint ()
    {
      return _goalStackPoint;
    }

  final int chptStackPoint ()
    {
      return _chptStackPoint;
    }

  final int bindTrailPoint ()
    {
      return _bindTrailPoint;
    }

  final int typeTrailPoint ()
    {
      return _typeTrailPoint;
    }

  final int applTrailPoint ()
    {
      return _applTrailPoint;
    }

  final int goalTrailPoint ()
    {
      return _goalTrailPoint;
    }

  public String toString ()
    {
      return "TypingState " +  timeStamp() + " <" +
          "goalStackPoint = " + _goalStackPoint + ", " +
          "chptStackPoint = " + _chptStackPoint + ", " +
          "bindTrailPoint = " + _bindTrailPoint + ", " +
          "typeTrailPoint = " + _typeTrailPoint + ", " +
          "applTrailPoint = " + _applTrailPoint + ", " +
          "goalTrailPoint = " + _goalTrailPoint + ">";
    }
}
