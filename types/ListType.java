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
 * This is the class denoting the type of lists. Types of this kind consist of list
 * types whose elements are of any base type (including lists - thus enabling
 * higher-order lists - and type parameters - thus enabling polymorphic lists).
 */
public class ListType extends CollectionType
{
  /**
   * Constructs a polymorphic list type; <i>i.e.</i>, one whose base type is a type
   * parameter.
   */
  public ListType ()
    {
      _baseType = new TypeParameter();
    }

  /**
   * Constructs a list type with the specified base type.
   */
  public ListType (Type baseType)
    {
      if (baseType.value().isVoid())
        throw new TypingErrorException("void list base type");

      _baseType = baseType;
    }

  /**
   * Returns a new list type with the specified base type.
   */
  public final CollectionType newCollectionType (Type basetype)
    {
      return new ListType(basetype);
    }

  /**
   * Returns the constant identifying list types; <i>i.e.</i>, <tt>Type.LIST</tt>.
   */
  public final byte kind ()
    {
      return LIST;
    }

  public final String toString ()
    {
      return "list{" + baseType() + "}";
    }

}
