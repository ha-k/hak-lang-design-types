//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.kernel.DefinitionException;
import hlt.language.design.instructions.Instruction;

import hlt.language.util.ArrayList;
import hlt.language.util.Comparable;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A symbol object is essentially a (global) name and its type table. The type
 * table is a list of <a href="CodeEntry.html"><tt>CodeEntry</tt></a>
 * objects.
 */
public class Symbol
{
  private String _name;
  private int _index;
  private boolean _noCurrying = false;
  private ArrayList _typeTable = new ArrayList();

  public Symbol (String name)
    {
      _name = name.intern();
    }

  public Symbol (String name, int index)
    {
      _name = name.intern();
      _index = index;
    }

  public final String name ()
    {
      return _name;
    }

  public final int index ()
    {
      return _index;
    }

  public final ArrayList typeTable ()
    {
      return _typeTable;
    }

  public final Symbol setNoCurrying (boolean flag)
    {
      _noCurrying = flag;
      return this;
    }

  //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

  /**
   * This is a dummy symbol that is given the types allowed for array index sets.
   * It is used for type-checking array dimension expressions for allocation via a
   * dummy <a href="Global.html"><tt>Global</tt></a> constructed with this
   * symbol, thus enabling a choice point to be created for typing these expressions.
   */
  public static final Symbol INDEX_SET = new Symbol("INDEX_SET");
  static
    {
      INDEX_SET.getCodeEntry(Type.INT());
      INDEX_SET.getCodeEntry(Type.INT_RANGE);
      INDEX_SET.getCodeEntry(new SetType());
    }

  /**
   * This is a dummy symbol that is given the types allowed for map index sets.
   * It is used for type-checking array dimension expressions for allocation via a
   * dummy <a href="../kernel/Global.html"><tt>Global</tt></a> constructed with this
   * symbol, thus enabling a choice point to be created for typing these expressions.
   */
  public static final Symbol INDEXABLE = new Symbol("INDEXABLE");
  static
    {
      INDEXABLE.getCodeEntry(Type.INT_RANGE);
      INDEXABLE.getCodeEntry(new SetType());
    }

  /**
   * This is a dummy symbol that is given the types allowed for collections.  It
   * is used for type-checking it using a dummy <a href="../kernel/Global.html">
   * <tt>Global</tt></a> constructed with this symbol, thus enabling choice points
   * to be created for typing its expression.
   */
  public static final Symbol COLLECTION = new Symbol("COLLECTION");

  static
    { // NB: Although this is currently identical to INDEXABLE, this will not be the case
      // when other collections such as lists and bags are implemented.

      COLLECTION.getCodeEntry(Type.INT_RANGE);
      COLLECTION.getCodeEntry(new SetType());
//        COLLECTION.getCodeEntry(new ListType());
//        COLLECTION.getCodeEntry(new BagType());
    }

  //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

  /**
   * This method registers a definitively checked type of this global symbol.
   * It is meant to be called <i>only</i> (and it is) by its namesake method in
   * the class <a href="../kernel/Definition.html"><tt>Definition</tt></a>,
   * itself only called after type-checking of this symbol's definition has
   * been completed; namely, after the <tt>setCheckedType</tt> method has been
   * invoked on the definition, which ensures that its type argument has been
   * <i>standardized</i>.
   */
  public final DefinedEntry registerCodeEntry (Type type) throws DefinitionException
    {
      CodeEntry entry = getCodeEntry(type);

      if (entry.isBuiltIn() || entry.isDefinedField())
        throw new DefinitionException("cannot redefine "+entry);

      return (DefinedEntry)entry;
    }

  /**
   * Assigns and returns a <a href="CodeEntry.html"><tt>CodeEntry</tt></a>
   * corresponding to the specified <a href="Type.html"><tt>Type</tt></a>
   * for this symbol if one does not exist for with this type; otherwise, returns
   * the existing one.
   */
  public final CodeEntry getCodeEntry (Type type)
    {
      return getCodeEntry(type,false);
    }

  /**
   * Assigns and returns a <a href="CodeEntry.html"><tt>CodeEntry</tt></a>
   * corresponding to the specified <a href="Type.html"><tt>Type</tt></a>
   * for this symbol if one does not exist for with this type.  If the
   * <tt>noDuplicates</tt> is true, a <tt>DuplicateCodeEntryException</tt>
   * is thrown whenever an entry already existed for this type; otherwise,
   * returns the existing code entry.
   */
  public final CodeEntry getCodeEntry (Type type, boolean noDuplicates)
    throws DuplicateCodeEntryException
    {
      CodeEntry entry = null;
      int index = _codeEntryIndex(type);
      if (index == -1)
        {
          _typeTable.add(entry = new DefinedEntry(this,type));
          return entry;
        }

      if (noDuplicates)
        {
          Type.resetNames();
          throw new DuplicateCodeEntryException("Attempt to duplicate code entry for "+
                                                this+" : "+type);
        }

      return (CodeEntry)_typeTable.get(index);
    }

  /**
   * Returns the index in the type table of the code type whose type matches
   * the specified type; or -1 if no match is found.
   */
  private final int _codeEntryIndex (Type type)
    {
      for (int i=_typeTable.size(); i-->0;)
        if (type.isEqualTo(((CodeEntry)_typeTable.get(i)).type(),new HashMap()))
          return i;

        return -1;
    }

  /**
   * Removes the latest code entry in this symbol's type table.
   */
  public final void removeLatestEntry ()
    {
      _typeTable.remove(_typeTable.size()-1);
    }      

  /**
   * Installs this symbol as a built-in with specified type and instruction.
   * <b>N.B.:</b> If this type was defined for this symbol before, a
   * <tt>DuplicateCodeEntryException</tt> is thrown.
   */
  public final void defineBuiltIn (Type type, Instruction builtIn)
    throws DuplicateCodeEntryException
    {
//        if (isDefined(type))
//          throw new DuplicateCodeEntryException(this + " : " + type);

      if (type.kind() == Type.FUNCTION && _noCurrying)
        ((FunctionType)type).setNoCurrying();

      _typeTable.add(new BuiltinEntry(this,type.standardize(),builtIn));
    }

  public final boolean isDefined ()
    {
      return !_typeTable.isEmpty();
    }

  public final boolean isDefined (Type type)
    {
      if (!isDefined())
        return false;

      type = type.standardize();

      for (int i=_typeTable.size(); i-->0;)
        if (type.equals(((CodeEntry)_typeTable.get(i)).type()))
          return true;

      return false;
    }

  public final boolean equals (Object other)
    {
      if (this == other)
        return true;

      if (!(other instanceof Symbol))
        return false;

      return _name == ((Symbol)other).name();
    }

  public final void showDefinedEntries ()
    {
      boolean foundEntries = false;

      for (Iterator i=_typeTable.iterator(); i.hasNext();)
        {
          CodeEntry entry = (CodeEntry)i.next();
          if (entry.isBuiltIn()) continue;
          Type.resetNames();
          System.out.println("\t"+entry);
          foundEntries = true;
        }

      if (foundEntries) System.out.println();
    }

  public final void showCodeEntries ()
    {
      for (Iterator i=_typeTable.iterator(); i.hasNext();)
        {
          Type.resetNames();
          System.out.println("\t"+i.next());
        }

      System.out.println();
    }

  public final String toString ()
    {
      return _name;
    }
}
