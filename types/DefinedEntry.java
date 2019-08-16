//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.instructions.*;

import java.util.HashSet;
import java.util.Iterator;

/**
 * This class extends <a href="CodeEntry.html"><tt>CodeEntry</tt></a>
 * for the specific case of user-defined (as opposed to built-in) code
 * entries.
 */
public class DefinedEntry extends CodeEntry
{
  private Instruction[] _code;          // this entry's code
  private boolean _inlinable = true;    // if true, this entry's code will be inlined
  private boolean _setOnEval = false;   // if true, this entry's code will set when evaluated

  private HashSet _unsafeEntries;       // set of unsafe CodeEntry's this depends on
  private HashSet _releasableEntries;   // unsafe CodeEntry's to release when this is safe
  
  private FieldInfo _fieldInfo;         // extra info when this is a class field's entry
  private boolean _isField   = false;   // indicates that this is a field

  private boolean _isProjection = false; // indicates that this is named tuple projection

  public DefinedEntry (Type type)
    {
      _type = type;
    }

  public DefinedEntry (Symbol symbol, Type type)
    {
      this(type);
      _symbol = symbol;
    }

  /**
   * Indicates that this code entry is that of a named tuple projection.
   */
  public final boolean isProjection ()
    {
      return _isProjection;
    }

  /**
   * Sets this code entry to be that of a named tuple projection.
   */
  public final DefinedEntry setIsProjection ()
    {
      _isProjection = true;
      return this;
    }

  /**
   * Indicates that this code entry is that of a class field, although
   * this may be a code entry that is undefined as yet.
   */
  public final boolean isField ()
    {
      return _isField;
    }

  /**
   * Sets this code entry to be that of a class field (defined or not), and
   * returns this defined entry.
   */
  public final DefinedEntry setIsField ()
    {
      _isField = true;
      return this;
    }

  /**
   * Indicates that this code entry is that of a defined class field.
   */
  public final boolean isDefinedField ()
    {
      return _fieldInfo != null;
    }

  /**
   * Sets this code entry to be a defined class field's.
   */
  public final void setFieldInfo ()
    {
      _fieldInfo = new FieldInfo(this);
      _isField = true;
    }

  public final byte fieldSort ()
    {
      return fieldType().boxSort();
    }

  public final int fieldOffset ()
    {
      return _fieldInfo.fieldOffset();
    }

  public final ClassType objectType ()
    {
      return _fieldInfo.objectType();
    }

  public final Type fieldType ()
    {
      return _fieldInfo != null ? _fieldInfo.fieldType()
                                : ((FunctionType)type()).curryedRange();
    }

  public final boolean isSetOnEvaluation ()
    {
      return _setOnEval;
    }

  public final void setOnEvaluation ()
    {
      _setOnEval = true;
      _inlinable = false;
    }

  public final Instruction[] initCode ()
    {
      return _fieldInfo.initCode();
    }

  public final void setInlinable (boolean flag)
    {
      _inlinable = flag;
    }

  public final boolean isInlinable ()
    {
      return _inlinable && _code != null;
    }

  public final TupleComponentInstruction projection ()
    {
      for (int i=_code.length; i-->0;)
        if (_code[i] instanceof TupleComponentInstruction)
          return (TupleComponentInstruction)_code[i];

      return null;
    }

  public final void setCode (Instruction[] code)
    {
      _code = code;
    }

  public final Instruction[] code ()
    {
      return _code;
    }

  public final boolean hasNoCode ()
    {
      return _code == null;
    }

  public final boolean isUnsafe ()
    {
      return (_code == null || _unsafeEntries != null || (isField() && fieldOffset() == -1));
    }

  /**
   * Adds the specified code entry to the set of unsafe entries of this code entry.
   */
  public final void registerUnsafeEntry (CodeEntry entry)
    {
      if (_unsafeEntries == null)
        _unsafeEntries = new HashSet();

      _unsafeEntries.add(entry);
    }

  /**
   * Removes the specified code entry from the set of usafe entries of this code entry,
   * and sets the latter to <tt>null</tt> if it becomes empty.
   */
  public final void removeUnsafeEntry (CodeEntry entry)
    {
      if (_unsafeEntries == null)
        return;
      
      _unsafeEntries.remove(entry);

      if (_unsafeEntries.isEmpty())
        _unsafeEntries = null;
    }

  /**
   * Adds the specified code entry to the set of releasable entries of this code entry.
   */
  public final void registerReleasableEntry (CodeEntry entry)
    {
      if (_releasableEntries == null)
        _releasableEntries = new HashSet();

      _releasableEntries.add(entry);
    }

  /**
   * Releases all releasable entries of the unsafe entries of this <tt>CodeEntry</tt>.
   * If this contains unsafe entries, any releasable entry being released also has
   * its unsafe set augmented with this entry's unsafe set.
   */
  public final void releaseUnsafeEntries ()
    {
      if (_releasableEntries == null)
        return;

      for (Iterator i=_releasableEntries.iterator(); i.hasNext();)
        {
          DefinedEntry releasable = (DefinedEntry)i.next();

          releasable.removeUnsafeEntry(this);

          if (_unsafeEntries != null)
            for (Iterator j=_unsafeEntries.iterator(); j.hasNext();)
              {
                DefinedEntry unsafe = (DefinedEntry)j.next();
                
                releasable.registerUnsafeEntry(unsafe);
                unsafe.registerReleasableEntry(releasable);
              }
          }

      _releasableEntries = null;
    }

  public final void setValue (int value)
    {
      Instruction[] code = { new PushValueInt(value), Instruction.END };
      _code = code;
      releaseUnsafeEntries();
      _setOnEval = false;
    }

  public final void setValue (double value)
    {
      Instruction[] code = { new PushValueReal(value), Instruction.END };
      _code = code;
      releaseUnsafeEntries();
      _setOnEval = false;
    }

  public final void setValue (Object value)
    {
      Instruction[] code = { new PushValueObject(value), Instruction.END };
      _code = code;
      releaseUnsafeEntries();
      _setOnEval = false;
    }

  public final void setInitCode ()
    {
      PushClosure pc = new PushClosure(2,0,0,1,0,0,0);
      pc.setNonExitable();
      Instruction[] fieldAccessCode
        = { pc
          , Instruction.END
          , new PushOffsetObject(0)
          , _getField()
          , _returnField()
          };
      fieldAccessCode[0] = pc.setReferenceCode(fieldAccessCode);

      _fieldInfo.setInitCode(_code);
      _code = fieldAccessCode;
    }

  private final Instruction _getField ()
    {
      switch (fieldSort())
        {
        case Type.INT_SORT:
            return new GetIntField(this);
        case Type.REAL_SORT:
            return new GetRealField(this);
        case Type.OBJECT_SORT:
          return new GetObjectField(this);
        }
      return null;
    }

  private final Instruction _returnField ()
    {
      switch (fieldSort())
        {
        case Type.INT_SORT:
          return Instruction.RETURN_I;
        case Type.REAL_SORT:
          return Instruction.RETURN_R;
        case Type.OBJECT_SORT:
          return Instruction.RETURN_O;
        }
      return null;
    }

  public final void showCode ()
    {
      if (_code == null)
        {
          System.out.println(this+" has no code!");
          return;
        }

      System.out.println(this+" has the following code -\n");
      CodeEntry.showCode(_code);

      if (_fieldInfo == null) return;

      if (initCode() == null)
        {
          System.out.println(this+" has no initialization code!");
          return;
        }
      System.out.println(this+" has the following initialization code -\n");
      CodeEntry.showCode(initCode());
    }

  public final String toString ()
    {
      return "["+(isField()?"field":(_code == null?"un":"")+"defined")+"] "+super.toString();
    }
}
