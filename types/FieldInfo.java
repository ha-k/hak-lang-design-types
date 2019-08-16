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

import java.util.HashSet;
import java.util.Iterator;

/**
 * Objects of this class encapsulate the additional information needed by a
 * a <a href="DefinedEntry.html"><tt>DefinedEntry</tt></a> when it is that
 * of a class field.
 */
public class FieldInfo
{
  private DefinedEntry _entry;           // the code entry using this info
  private int _fieldOffset = -1;         // the field's offset in the object
  private Type _fieldType;               // the type of the field's values
  private ClassType _objectType;         // the type of the object with this field
  private Instruction[] _initCode;       // the initialization code for this field
  
  FieldInfo (DefinedEntry entry)
    {
      _entry = entry;
      FunctionType type = (FunctionType)entry.type();
      _objectType = (ClassType)type.domain(0);
      _fieldType = type.curryedRange();
      _fieldOffset = _objectType.nextOffset(sort());
    }

  final ClassType objectType ()
    {
      return _objectType;
    }

  final Type fieldType ()
    {
      return _fieldType.value();
    }

  final byte sort ()
    {
      return fieldType().boxSort();
    }

  final int fieldOffset ()
    {
      return _fieldOffset;
    }

  final Instruction[] initCode ()
    {
      return _initCode;
    }

  final void setInitCode (Instruction[] code)
    {
      _initCode = code;
    }
}








