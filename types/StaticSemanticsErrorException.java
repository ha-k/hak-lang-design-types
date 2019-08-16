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

public abstract class StaticSemanticsErrorException extends RuntimeException
{
  private long _stamp;
  private Locatable _extent;
  protected String _msg = "";

  public final String getMessage ()
    {
      return _msg;
    }
  
  public final String msg ()
    {
      return _msg;
    }

  public final Locatable extent ()
    {
      return _extent;
    }

  public final long stamp ()
    {
      return _stamp;
    }

  public final StaticSemanticsErrorException setExtent (Locatable extent)
    {
      if (extent != null) _extent = extent;
      return this;
    }

  public final StaticSemanticsErrorException setStamp (long stamp)
    {
      _stamp = stamp;
      return this;
    }
}

