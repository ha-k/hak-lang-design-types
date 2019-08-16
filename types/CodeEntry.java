//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.instructions.Instruction;

import java.util.HashMap;

/**
 * Objects of this class associate a <a href="Symbol.html"><tt>Symbol</tt></a>
 * and a <a href="Type.html"><tt>Type</tt></a> to code that may be executed
 * for it.  This code is either an array of instructions (if the symbol with
 * this type is user-defined) or a built-in <a href="../instructions/Instruction.html">
 * <tt>Instruction</tt></a> otherwise. The two concrete classes <a
 * href="DefinedEntry.html"><tt>DefinedEntry</tt></a> and <a
 * href="BuiltinEntry.html"><tt>BuiltinEntry</tt></a> correspond to each case,
 * respectively.
 */
public abstract class CodeEntry
{
  protected Symbol _symbol;      // symbol for this entry - or null if anonymous
  protected Type _type;          // this entry's type

  private final static HashMap _codeIds = new HashMap();

  public static class CodeId
    {
      int number;
      CodeEntry entry;

      CodeId (int number)
        {
          this.number = number;
        }

      public final String toString ()
        {
          return "CODE: #" + number + (entry == null ? "" : " "+entry);
        }
    }

  public final static CodeId getId (Instruction[] code)
    {
      CodeId id = (CodeId)_codeIds.get(code);

      if (id == null)
        _codeIds.put(code,id = new CodeId(_codeIds.size()));

      return id;
    }

  public final static void showCode (Instruction[] code)
    {
      _showCode(null,code,-1);
    }

  public final static void showCode (Instruction[] code, int ip)
    {
      _showCode(null,code,ip);
    }

  public final static void showCode (CodeEntry entry, Instruction[] code)
    {
      _showCode(entry,code,-1);
    }

  private final static void _showCode (CodeEntry entry, Instruction[] code, int ip)
    {
      CodeId cid = getId(code);
      if (entry != null)
        cid.entry = entry;
        
      System.out.println();
      System.out.println(cid);
      System.out.println();
      for (int i = 0; i < code.length; i++)
        {
          if (i == ip) System.out.print("====>");
          System.out.println("\t[" + i + "]\t" + code[i]);
        }
      System.out.println();
    }

  public final Symbol symbol ()
    {
      return _symbol;
    }

  public final Type type ()
    {
      return _type;
    }

  public final boolean isBuiltIn ()
    {
      return (this instanceof BuiltinEntry);
    }

  public boolean isProjection ()
    {
      return (this instanceof DefinedEntry && ((DefinedEntry)this).isProjection());
    }

  public boolean isField ()
    {
      return (this instanceof DefinedEntry && ((DefinedEntry)this).isField());
    }

  public boolean isDefinedField ()
    {
      return (this instanceof DefinedEntry && ((DefinedEntry)this).isDefinedField());
    }

  final public boolean equals (Object o)
    {
      if (this == o)
        return true;

      if (!(o instanceof CodeEntry))
        return false;

      return _symbol == ((CodeEntry)o).symbol()
          && _type.isEqualTo(((CodeEntry)o).type(),new HashMap());
    }

  public String toString ()
    {
      Type.resetNames();
      StringBuilder buf = new StringBuilder();

      String name = _symbol != null ? _symbol.toString() : "<no name>";

      buf.append(name+" : "+_type.toQuantifiedString());
      return buf.toString();
    }
}
