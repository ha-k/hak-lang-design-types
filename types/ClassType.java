//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Thu Mar 24 12:24:31 2016 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.kernel.Expression;
import hlt.language.design.kernel.ParameterStack;
import hlt.language.design.backend.Runtime;
import hlt.language.design.backend.ObjectInstance;

import java.util.HashMap;
import java.util.AbstractList;

/**
 * This is the type of object structures. It declares an <i>interface</i>
 * (or member type signature) for a class of objects and the members comprising
 * its structure. It holds information for compiling field access and update,
 * and enables specifying an <i>implementation</i> for methods manipulating
 * objects of this type.
 *
 * <p>
 *
 * A class implementation uses the information declared in its interface.
 * It is interpreted as follows: only non-method members - hereafter
 * called <i>fields</i> - correspond to actual slots in an object
 * structure that is an instance of the class and thus may be
 * updated. On the other hand, all members (<i>i.e.</i>, both fields and
 * method members) are defined as global <i>functions</i> whose first
 * argument stands for the object itself (that must be explicitly
 * referred to as <span style="color:brown"><tt>this</tt></span> everywhere).
 *
 * Our prototype syntax for a class definition is of the form:
 * <pre>
 * <span style="color:darkgreen">
 *      <b>class</b> <span style="color:orange"><i>&lt;classname&gt;</i></span> { <span style="color:orange"><i>&lt;interface&gt;</i></span> } <span style="color:orange">[</span> { <span style="color:orange"><i>&lt;implementation&gt;</i></span> } <span style="color:orange">]</span>
 * </span>
 * </pre>
 * 
 * For example, one can declare a class to represent a simple counter as
 * follows:
 * <pre> 
 * <span style="color:darkgreen">
 * <b>class</b> Counter
 *       { value : int = 1;
 *         <b>method</b> set : int -&gt; Counter;
 *       }
 *       { set (n : int) : Counter = (<b>this</b>.value = n);
 *       }
 * </span>
 * </pre>
 * The first block specifies the interface for the class type <span
 * style="color:brown"> <tt>Counter</tt></span> defining two members: a
 * field <span style="color:brown"> <tt>value</tt></span> of type <span
 * style="color:brown"><tt>int</tt></span> and a method <span
 * style="color:brown"><tt>set</tt></span> taking an argument of type
 * <span style="color:brown"><tt>int</tt></span> and returning a <span
 * style="color:brown"> <tt>Counter</tt></span> object. It also
 * specifies an initialization expression (<span
 * style="color:brown"><tt>1</tt></span>) for the <span
 * style="color:brown"><tt>value</tt></span> field. Specifying a field's
 * initialization is optional - when missing, the field will be
 * initialized to a null value of appropriate type: <span
 * style="color:brown"><tt>0</tt></span> for an <span
 * style="color:brown"> <tt>int</tt></span>, <span
 * style="color:brown"><tt>0.0</tt></span> for a <span
 * style="color:brown"><tt>real</tt></span>, <span style="color:brown">
 * <tt>false</tt></span> for a <span style="color:brown">
 * <tt>boolean</tt></span>, <span style="color:brown"><tt>_</tt></span>
 * for <span style="color:brown"><tt>void</tt></span>, and <span
 * style="color:brown"><tt>null(T)</tt></span> for any other type <span
 * style="color:brown"><tt>T</tt></span>. [<b>NB:</b> Strictly speaking,
 * a field of type <span style="color:brown"><tt>void</tt></span> is
 * useless since it can only have the unique value of this type
 * (<i>i.e.</i>, <span style="color:brown"><tt>_</tt></span>). Thus, a
 * <span style="color:brown"> <tt>void</tt></span> field should arguably
 * be disallowed. On the other hand, allowing it is not semantically
 * unsound and may be tolerated for the sake of uniformity.]
 *
 * <p> The second (optional) block of a class declaration defines its
 * implementation.  Thus, the implementation block for the <span
 * style="color:brown"><tt>Counter</tt></span> class defines the body of
 * the <span style="color:brown"><tt>set</tt></span> method.
 *
 * <p> Note that a method's implementation can as well be given outside
 * the class declaration as a function whose first argument's type is
 * the class. For example, we could have defined the <span
 * style="color:brown"><tt>set</tt></span> method of the class <span
 * style="color:brown"><tt>Counter</tt></span> as:
 * <pre>
 * <span style="color:darkgreen">
 * def set (x : Counter, n : int) : Counter = (x.value = n);
 * </span>
 * </pre>
 *
 * On the other hand, although a field is also semantically a function
 * whose first argument's type is a class, it may <i>not</i> be defined
 * outside its class. Defining a declared field outside a class
 * declaration causes an error. This is because the code of a field is
 * always fixed and defined to return the value of an object's slot
 * corresponding to the field.  Note however that one may define a unary
 * function whose argument is a class type outside this class when it is
 * not a declared field for this class. It will be understood as a
 * <i>method</i> for the class (even though it takes no extra argument
 * and may be invoked in "dot notation" without parentheses as a field
 * is) and thus act as a "static field" for the class. Of course field
 * updates using dot notation will not be allowed on these pseudo
 * fields. However, they (like any global variable) may be (re)set using
 * a global (re)definition at the top level, or a nested global
 * assignment.
 *
 * <p>
 * Note also that a field may be functional without being a method -
 * the essential difference being that a field is part of the structure of
 * every object instance of a class and thus may be updated within an
 * object instance, while a method is common to all instances of a class
 * and may not be updated within a particular instance, but only globally
 * for all the class' instances.
 *
 * <p>
 * Thus, every time a <span style="color:brown"><tt>Counter</tt></span> object
 * is created with <span style="color:brown"><tt>new</tt></span> or
 * <span style="color:brown"><tt>static</tt></span>, as in, for example:
 * <pre>
 * <span style="color:darkgreen">
 *      c = <b>new</b> Counter;
 * </span>
 * </pre>
 * the value <span style="color:brown"><tt>1</tt></span> will be used to
 * initialize the slot that corresponds to the location of the
 * <span style="color:brown"><tt>value</tt></span> field.
 * Then, field and method invocation can be done using the familiar
 * "dot notation"; <i>e.g.</i>:
 * <pre>
 * <span style="color:darkgreen">
 *      c.set(c.value+2);
 *      write(c.value);
 * </span>
 * </pre>
 * This will set <span style="color:brown"><tt>c</tt></span>'s
 * <tt>value</tt> field to <span style="color:brown"><tt>3</tt></span>
 * and print out this value.  This code is exactly equivalent to:
 * <pre>
 * <span style="color:darkgreen">
 *      set(c,value(c)+2);
 *      write(value(c));
 * </span>
 * </pre>
 * <p>
 * Indeed, field and method invocation simply amounts to functional
 * application. This scheme offers the advantage that an object's fields
 * and methods may be manipulated as functions (<i>i.e.</i>, as first-class
 * citizens) and no additional setup is needed for type-checking and/or
 * type inference when it comes to objects.
 *
 * <p>
 * Incidentally, some or all type information may be omitted while specifying
 * a class's <i>implementation</i> (though not its <i>interface</i>) as long
 * as non-ambiguous types may be inferred. Thus, the class
 * <span style="color:brown"><tt>Counter</tt></span> above could be
 * defined simply as:
 * <pre>
 * <span style="color:darkgreen">
 *      <b>class</b> Counter
 *          {
 *             value : int = 1;
 *             <b>method</b> set : int -&gt; Counter;
 *          }
 *          {
 *             set (n) = (<b>this</b>.value = n);
 *          }
 * </span>
 * </pre>
 *
 * Declaring a class type and defining its implementation causes the
 * following:
 * <p>
 * <ul>
 * <li> the name of the class is entered with a new type for it in the
 *      type table in <tt>_tables</tt> (an object comprising symbol
 *      tables, of type <a href="Tables.html"><tt>Tables</tt></a>),
 *      where its type definition associates it with a <tt>ClassType</tt>
 *      whose class structure is encapsulated by an object of type
 *      <a href="ClassInfo.html"><tt>ClassInfo</tt></a> where
 *      code entries for all its members' types are recorded;
 *
 * <p>
 * <li> each field of a distinct type is assigned an offset in an
 *      array of slots (per sort);
 *
 * <p>
 * <li> each method and field expression is name-sanitized, type-checked,
 *      and sort-sanitized after closing it into an abstraction taking
 *      <tt>'this'</tt> as first argument;
 * <p>
 * <li> each method definition is then compiled into a global definition,
 *      and each field is compiled into a global function corresponding
 *      to accessing its value from the appropriate offset;
 * <p>
 * <li> finally, each field's initialization expression is compiled and
 *      recorded for it into the <tt>ClassType</tt> to be used at object
 *      creation time. An object may be created at run-time (using the
 *      <span style="color:brown"><tt>new</tt></span> operator followed by a
 *      class name).
 * <p>
 *      
 * </ul>
 * */

public class ClassType extends TypeTerm
{
  private ClassInfo _classInfo;
  private Tables _tables;

  public ClassType (Tables tables, String name, Type[] arguments, ClassInfo classInfo)
    {
      _tables = tables;
      _name = name.intern();
      _arguments = arguments;
      _classInfo = classInfo;
    }

  public ClassType (Tables tables, String name)
    {
      _tables = tables;
      _name = name.intern();
      _classInfo = new ClassInfo();
    }

  public ClassType (Tables tables, String name, AbstractList arguments)
    {
      this(tables,name);
      setArguments(arguments);
    }

  public final byte kind ()
    {
      return CLASS;
    }

  public final ClassInfo classInfo ()
    {
      return _classInfo;
    }

  public final boolean isDeclared ()
    {
      return _classInfo.isDeclared();
    }

  public final DefinedEntry[] fields ()
    {
      return _classInfo.fields();
    }

  public final DefinedEntry[] methods ()
    {
      return _classInfo.methods();
    }

  public final int intFieldsCount ()
    {
      return _classInfo.intFieldsCount();
    }
    
  public final int realFieldsCount ()
    {
      return _classInfo.realFieldsCount();
    }
    
  public final int objectFieldsCount ()
    {
      return _classInfo.objectFieldsCount();
    }

  /**
   * Increments and returns the field offset in this class for the given sort.
   */
  public final int nextOffset (byte sort)
    {
      return _classInfo.nextOffset(sort);
    }

  public final void bindArguments ()
    {
      _classInfo.bindParameters(_arguments);
    }

  public final void unbindArguments ()
    {
      _classInfo.unbindParameters();
    }

  /**
   * Declares the members of this class type from the specified information and
   * returns this class type.  If some members have duplicate signatures, a
   * <tt>ClassDeclarationException</tt> is thrown and no type definition for
   * the class or any of its member is taken into account. Note that the field
   * offsets are not computed by, nor kept in, the class type itself but by, and
   * in, the field's <a href="DefinedEntry.html"><tt>DefinedEntry</tt></a>.
   * This is not only much more efficient to have the offset readily available
   * there when compiling field accesses and updates, it is also necessary as
   * these offsets can be safely computed only once the fields have been fully
   * type-checked to know which of an object's sorted array to offset them in.
   */
  public final ClassType declareMembers (AbstractList members, AbstractList types,
                                         AbstractList fieldInits, AbstractList typeArguments)
    throws ClassDeclarationException
    {
      if (!(typeArguments == null || typeArguments.isEmpty()))
        {
          _arguments = new TypeParameter[typeArguments.size()];
          for (int i=0; i<_arguments.length; i++)
            _arguments[i] = (TypeParameter)typeArguments.get(i);
        }

      _classInfo.fillClassInfo(_tables,this,members,types,fieldInits,_arguments);
      return this;
    }

  public final void undeclareClass (Tables tables)
    {
      if (!_classInfo.isDeclared())
        return;

      _classInfo.undeclareClass(tables,this);
    }      

  /**
   * Initializes the fields of the specified object instance using the specified
   * <tt>Runtime</tt>.
   */
  public final void initialize (ObjectInstance object, Runtime init)
    throws ObjectInitializationException
    {
      _classInfo.initialize(object,this,init);
    }

  final private Type _memberType (DefinedEntry entry)
    {
      return ((FunctionType)entry.type()).curryedRange();
    }          

  public final String toFullString ()
    {
      bindArguments();
      StringBuilder buf = new StringBuilder(this.toString());

      if (isDeclared())
        {
          buf.append("\n\t{\n");

          for (int i=0; i<fields().length; i++)
            buf.append("\t  "+fields()[i].symbol()+" : "+_memberType(fields()[i])+";\n");

          if (methods().length > 0) buf.append("\n");

          for (int i=0; i<methods().length; i++)
            buf.append("\t  method "+methods()[i].symbol()+" : "+_memberType(methods()[i])+";\n");

          buf.append("\t}");
        }
      else
        buf.append("\t[undeclared class]");

      unbindArguments();
      return buf.toString();      
    }

}
