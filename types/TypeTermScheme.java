//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.util.Stack;

/**
 * Objects of this class are of the form <tt>name(T<sub>1</sub>,...T<sub>n</sub>)</tt>
 * where <tt>name</tt> is a type name and the <tt>T<sub>i</sub></tt>'s are distinct
 * type parameters.
 */
public class TypeTermScheme
{
  protected TypeParameter[] _parameters;

  public final TypeParameter[] parameters ()
    {
      return _parameters;
    }

  /**
   * Returns the number of type parameters of this type term scheme.
   */
  public final int arity ()
    {
      return _parameters == null ? 0 : _parameters.length;
    }

  private Stack _parameterBindings = new Stack();

  final void bindParameters (Type[] types)
    {
      for (int i=0; i<arity(); i++)
        {
          _parameterBindings.push(_parameters[i].value());
          _parameters[i].bind(types[i].value());
        }
    }

  final void unbindParameters ()
    {
      for (int i=arity(); i-->0;)
        _parameters[i].bind((Type)_parameterBindings.pop());
    }
}

