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
 * This is the class denoting the type of sets. Types of this kind consist of set
 * types whose elements are of any base type (including sets - thus enabling
 * higher-order sets - and type parameters - thus enabling polymorphic sets).
 */
public class SetType extends CollectionType
{
  /**
   * Constructs a polymorphic set type; <i>i.e.</i>, one whose base type is a type
   * parameter.
   */
  public SetType ()
    {
      _baseType = new TypeParameter();
    }

  /**
   * Constructs a set type with the specified base type.
   */
  public SetType (Type baseType)
    {
      if (baseType.value().isVoid())
        throw new TypingErrorException("void set base type");

      _baseType = baseType;
    }

  /**
   * Returns a new set type with the specified base type.
   */
  public final CollectionType newCollectionType (Type baseType)
    {
      return new SetType(baseType);
    }

  /**
   * Returns the constant identifying set types; <i>i.e.</i>, <tt>Type.SET</tt>.
   */
  public final byte kind ()
    {
      return SET;
    }

//   final public String toString ()
//     {
//       return "set{" + baseType() + "}";
//     }

  final public String toString ()
    {
      return "{" + baseType() + "}";
    }

}
