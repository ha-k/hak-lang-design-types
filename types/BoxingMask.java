//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.BitSet;

class BoxingMask extends BitSet implements Bindable
{
  private BoxingMask _value = this;
  private int _width;

  BoxingMask (int n)
    {
      super(n+1);
      _width = n;
    }

  BoxingMask (BoxingMask mask)
    {
      this(mask.width());
      or(mask);
    }

  private final void _set (int index, boolean flag)
    {
      if (flag)
        set(index);
      else
        clear(index);
    }

  final int width ()
    {
      return _width;
    }

  final void setRangeBox ()
    {
      set(0);
    }

  final void setRangeBox (boolean flag)
    {
      _set(0,flag);
    }

  final void unsetRangeBox ()
    {
      clear(0);
    }

  final boolean rangeIsBoxed ()
    {
      return get(0);
    }

  final void setDomainBox (int i)
    {
      set(i+1);
    }

  final void setDomainBox (int i, boolean flag)
    {
      _set(i+1,flag);
    }

  final void unsetDomainBox (int i)
    {
      clear(i+1);
    }

  final boolean domainIsBoxed (int i)
    {
      return get(i+1);
    }    

  public final boolean isBound ()
    {
      return _value != this;
    }

  public final boolean isUnbound ()
    {
      return _value == this;
    }

  public final boolean bind (Valuable value)
    {
      _value = (BoxingMask)value;
      return true;
    }

  /**
   * Binds this boxing mask to the given valuable in the context of
   * the given prover. This binding is appropriately trailed in the
   * contextual prover for undoing purposes. Returns <tt>true</tt>.
   */
  public final boolean bind (Valuable valuable, GoalProver prover)
    {
      valuable = valuable.getValue();
      if (this != valuable)
        {
          _value = (BoxingMask)valuable;
          prover.trail(this);
        }
      return true;
    }

  public final void unbind ()
    {
      _value = this;
    }

  public final Valuable getValue ()
    {
      return value();
    }

  public final BoxingMask value ()
    {
      return (_value == this ? _value : _value.value());
    }

  /**
   * Unifying two boxing masks amounts to taking their logical <tt>or</tt>.
   */
  private final BoxingMask _unify (BoxingMask mask)
    {
      BoxingMask newMask = new BoxingMask(this);
      newMask.or(mask);
      return newMask;
    }      

  public final void unify (BoxingMask mask, TypeChecker typeChecker)
    {
      mask = mask.value();
      if (equals(mask)) return;

      // NB: we could make sure that widths are equal - but this will be called
      // only on masks of function types of equal arities.

      BoxingMask newMask = _unify(mask);

      if (!equals(newMask))
        {
          bind(newMask);
          typeChecker.trail(this);
        }

      if (!mask.equals(newMask))
        {
          mask.bind(newMask);
          typeChecker.trail(mask);
        }
    }

  public final String toString ()
    {
      StringBuilder buf = new StringBuilder();

      for (int i=0; i<_width; i++)
        buf.append(domainIsBoxed(i) ? "[]" : "_");

      buf.append(" -> ");
      buf.append(rangeIsBoxed() ? "[]" : "_");

      return buf.toString();
    }
}
