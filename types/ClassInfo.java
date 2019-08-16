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
import hlt.language.design.backend.ObjectInstance;
import hlt.language.design.backend.RuntimeInt;
import hlt.language.design.backend.RuntimeReal;
import hlt.language.design.backend.Runtime;

import hlt.language.util.Stack;

import java.util.HashMap;
import java.util.AbstractList;

/**
 * This class holds information about a class type needed for compiling field
 * access and update, and enables specifying an <i>implementation</i> for methods
 * manipulating objects of this type.
 */
public class ClassInfo extends TypeTermScheme
{
  private DefinedEntry[] _fields;
  private DefinedEntry[] _methods;

  private int _intFieldsCount;
  private int _realFieldsCount;
  private int _objectFieldsCount;

  private boolean _isDeclared;
  private boolean _nestedInitialization;

  /**
   * Constructs an empty class info.
   */
  public ClassInfo ()
    {
    }

  /**
   * Fills this class info with all the information specified by its class declaration. It
   * creates the members of the specified class type from the given information; if some
   * members have duplicate signatures, a <tt>ClassDeclarationException</tt> is thrown and
   * no type definition for the class or any of its member is taken into account. Note
   * that the field offsets are not computed by, or kept in, the class info itself but by,
   * and in, the field's <a href="DefinedEntry.html"><tt>DefinedEntry</tt></a>'s. This
   * is not only much more efficient to have the offset readily available there when
   * compiling field accesses and updates, it is also necessary as these offsets can be
   * safely computed only once the fields have been fully type-checked to know which of an
   * object's sort array to offset them in.
   */
  public final void fillClassInfo (Tables tables, ClassType classType, AbstractList members,
                                   AbstractList types, AbstractList fieldInits, Type[] parameters)
    throws ClassDeclarationException
    {
      _parameters = (TypeParameter[])parameters;

      int fieldsCount = 0;

      for (int i=fieldInits.size(); i-->0;)
        fieldsCount += (fieldInits.get(i) == null ? 0 : 1);

      _fields = new DefinedEntry[fieldsCount];
      _methods = new DefinedEntry[fieldInits.size()-fieldsCount];

      int f = 0, m = 0;

      for (int i=0; i<members.size(); i++)
        {
          String member = (String)members.get(i);
          boolean isField = (fieldInits.get(i) != null);
          Type type = (Type)types.get(i);
          FunctionType globalType = new FunctionType(classType,type);

          globalType = (FunctionType)globalType.flatten();

          try
            {
              if (isField)
                {
                  if (type.isVoid())
                    throw new TypingErrorException("a class field may not have void type: "+
                                                   classType.name()+"."+member);

                  _fields[f++] = ((DefinedEntry)tables.symbol(member).getCodeEntry(globalType))
                                 .setIsField();
                }
              else
                _methods[m++] = (DefinedEntry)tables.symbol(member).getCodeEntry(globalType);
            }
          catch (TypingErrorException e)
            {
              throw new ClassDeclarationException(e.msg());
            }
          catch (DuplicateCodeEntryException e)
            {
              throw new ClassDeclarationException("duplicate member '"+member+" : "+
                                                  type+"' in class "+classType.name());
            }
        }

      _isDeclared = true;
    }

  public final boolean isDeclared ()
    {
      return _isDeclared;
    }

  /**
   * Resets all the information of this class info to that of an empty class info.
   */
  public final void undeclareClass (Tables tables, ClassType classType)
    {
      for (int i=0; i<_fields.length; i++)
        if (_fields[i] != null) _fields[i].symbol().removeLatestEntry();

      for (int i=0; i<_methods.length; i++)
        if (_methods[i] != null) _methods[i].symbol().removeLatestEntry();

      _fields = null;
      _methods = null;
      _parameters = null;
      _intFieldsCount = 0;
      _realFieldsCount = 0;
      _objectFieldsCount = 0;
      _isDeclared = false;
      _nestedInitialization = false;
    }

  final DefinedEntry[] fields ()
    {
      return _fields;
    }

  final DefinedEntry[] methods ()
    {
      return _methods;
    }

  final int intFieldsCount ()
    {
      return _intFieldsCount;
    }
    
  final int realFieldsCount ()
    {
      return _realFieldsCount;
    }
    
  final int objectFieldsCount ()
    {
      return _objectFieldsCount;
    }

  /**
   * Increments and returns the field offset in this class for the given sort.
   */
  final int nextOffset (byte sort)
    {
      switch (sort)
        {
        case Type.INT_SORT:
          return _intFieldsCount++;
        case Type.REAL_SORT:
          return _realFieldsCount++;
        }
      return _objectFieldsCount++;
    }

  /**
   * Initializes the fields of the specified object instance of the specified class type
   * using the specified <tt>Runtime</tt>.
   */
  final void initialize (ObjectInstance object, ClassType classType, Runtime runtime)
    throws ObjectInitializationException
    {
      if (_nestedInitialization)
        {
          _nestedInitialization = false;
          throw new ObjectInitializationException("infinite nesting in class "+classType);
        }

      _nestedInitialization = true;

      runtime.saveState();

      for (int i=0; i<_fields.length; i++)
        {
          runtime.pushObject(object);

          PushClosure pc = (PushClosure)_fields[i].initCode()[0];
          Instruction[] initCode = { pc
                                   , pc.apply()
                                   , Instruction.STOP
                                   };

          runtime.setCode(initCode);
          runtime.resetIP();

          try
            {
              runtime.run();
            }
          catch (Exception e)
            {
              // e.printStackTrace();
              throw new ObjectInitializationException("failed initializing field "+_fields[i]
                                                      + "\nDetail: " + e);
            }

          switch (_fields[i].fieldSort())
            {
            case Type.INT_SORT:
              {
                int value = runtime.resultSort() == Type.OBJECT_SORT
                          ? (((RuntimeInt)runtime.popObject()).value())
                          : runtime.popInt();
                object.setIntField(_fields[i].fieldOffset(),value);
              }
              break;
            case Type.REAL_SORT:
              {
                double value = runtime.resultSort() == Type.OBJECT_SORT
                             ? (((RuntimeReal)runtime.popObject()).value())
                             : runtime.popReal();
                object.setRealField(_fields[i].fieldOffset(),value);
              }
              break;
            default:
              {
                Object value =  runtime.resultSort() == Type.INT_SORT
                             ? (Object)new RuntimeInt(runtime.popInt())
                             : runtime.resultSort() == Type.REAL_SORT
                               ? (Object)new RuntimeReal(runtime.popReal())
                               : runtime.popObject();
                object.setObjectField(_fields[i].fieldOffset(),value);
              }
              break;
            }
        }

      runtime.restoreState();
      runtime.pushObject(object);

      _nestedInitialization = false;
    }
}
