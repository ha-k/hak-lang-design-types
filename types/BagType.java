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
 * This is the class denoting the type of bags. Types of this kind consist of bag
 * types whose elements are of any base type (including bags - thus enabling
 * higher-order bags - and type parameters - thus enabling polymorphic bags).
 */

public class BagType extends CollectionType
{
  /**
   * Constructs a polymorphic bag type; <i>i.e.</i>, one whose base type is a type
   * parameter.
   */
  public BagType ()
    {
      _baseType = new TypeParameter();
    }

  /**
   * Constructs a bag type with the specified base type.
   */
  public BagType (Type baseType)
    {
      if (baseType.value().isVoid())
        throw new TypingErrorException("void bag base type");

      _baseType = baseType;
    }

  /**
   * Returns a new bag type with the specified base type.
   */
  public final CollectionType newCollectionType (Type basetype)
    {
      return new BagType(basetype);
    }

  /**
   * Returns the constant identifying bag types; <i>i.e.</i>, <tt>Type.BAG</tt>.
   */
  public final byte kind ()
    {
      return BAG;
    }

  final public String toString ()
    {
      return "bag{" + baseType() + "}";
    }

}
