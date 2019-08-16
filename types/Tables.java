//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Thu Mar 24 12:28:27 2016 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.kernel.DefinitionException;
import hlt.language.design.kernel.UndefinedEqualityException;
import hlt.language.design.kernel.Global;
import hlt.language.design.kernel.Constant;
import hlt.language.design.instructions.Instruction;

import hlt.language.util.ArrayList;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Iterator;

/**
 * This class encapsulates the tables in which defined symbols and declared types
 * are registered.
 */

public class Tables
{
  private final static HashSet _EQUALITY_SYMBOLS = new HashSet();
  private static String _EQUALITY_SYMBOL;

  public final boolean isEquality (String name) throws UndefinedEqualityException
    {
      if (_EQUALITY_SYMBOL == null)
        throw new UndefinedEqualityException();
      name = name.intern();
      return name == _EQUALITY_SYMBOL || _EQUALITY_SYMBOLS.contains(name);
    }

  public final Symbol defineEqualitySymbol (String equality)
    {
      equality = equality.intern();
      if (_EQUALITY_SYMBOL == null)
        return symbol(_EQUALITY_SYMBOL = equality);
          
      _EQUALITY_SYMBOLS.add(equality);
      return symbol(equality);
    }

  public final Symbol equalitySymbol () throws UndefinedEqualityException
    {
      if (_EQUALITY_SYMBOL == null)
        throw new UndefinedEqualityException();
      return symbol(_EQUALITY_SYMBOL);
    }

  public final Global equality () throws UndefinedEqualityException
    {
      return new Global(equalitySymbol());
    }

  private String _inSymbol = "in";

  public final Global in ()
    {
      return new Global(this,_inSymbol);
    }

  public final void setInSymbol (String symbol)
    {
      _inSymbol = symbol;
    }

  //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

  /**
   * This table associates a global name to its <a href="Symbol.html">
   * <tt>Symbol</tt></a> object.
   */
  private final HashMap _symbolTable = new HashMap();

  /**
   * This table associates a type name to its <a href="TypeDefinition.html">
   * <tt>TypeDefinition</tt></a> object containing the specifics of its definition.
   */
  private final HashMap _typeTable = new HashMap();

  /**
   * This clears all definitions from all (symbol and type) tables.
   */
  public final void clear ()
    {
      _symbolTable.clear();
      _typeTable.clear();
    }

  /**
   * This resets the (symbol and type) tables, erasing all definitions except
   * for the built-ins.
   */
  public final void reset ()
    {
      clear();
      _redefineBuiltins();
      Constant.initialize(this);
    }

  //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

  public final Symbol symbol (String name)
    {
      Symbol s = (Symbol)_symbolTable.get(name);

      if (s == null)
        _symbolTable.put(name,s = new Symbol(name,_symbolTable.size()));

      return s;
    }

  public final Symbol symbol (String name, boolean flag)
    {
      return symbol(name).setNoCurrying(flag);
    }

  /**
   * Returns <tt>true</tt> iff the specified name is that of a defined symbol.
   */
  public final boolean isDefined (String name)
    {
      return _symbolTable.get(name) != null;
    }

  /**
   * Returns <tt>true</tt> iff the specified name is that of a defined symbol
   * with at least one non-functional type.
   */
  public final boolean isDefinedScalar (String name)
    {
      Symbol symbol = (Symbol)_symbolTable.get(name);

      if (symbol == null)
        return false;

      for (int i=symbol.typeTable().size(); i-->0;)
        if (((CodeEntry)symbol.typeTable().get(i)).type().kind() != Type.FUNCTION)
          return true;

      return false;
    }

  public final void showSymbols ()
    {
      System.out.println("Showing declared symbols:\n");

      for (Iterator i = _symbolTable.values().iterator(); i.hasNext();)
       ((Symbol)i.next()).showCodeEntries();
    }

  /**
   * This lists all the defined symbol in lexicographic order.
   */
  public final void showSortedSymbols ()
    {
      System.out.println("Showing known symbols in lexicographic order:\n");

      for (Iterator i = new TreeMap(_symbolTable).values().iterator(); i.hasNext();)
       ((Symbol)i.next()).showCodeEntries();
    }

  /**
   * This lists all the defined symbol in the order in which they have been defined.
   */
  public final void showOrderedSymbols ()
    {
      Symbol[] symbols = new Symbol[_symbolTable.size()];

      for (Iterator i = _symbolTable.values().iterator(); i.hasNext();)
        {
          Symbol symbol = (Symbol)i.next();
          symbols[symbol.index()] = symbol;
        }

      System.out.println("Showing known symbols in the order they have been defined:\n");

      for (int i=0; i<symbols.length; i++)
        symbols[i].showCodeEntries();
    }

  /**
   * This lists all the defined symbol in the (random) order of the symbol table's iterator.
   */
  public final void showDefined ()
    {
      System.out.println("Showing known symbols:\n");

      for (Iterator i = _symbolTable.values().iterator(); i.hasNext();)
       ((Symbol)i.next()).showDefinedEntries();
    }

  //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

  public final ClassType declareClass (String name, AbstractList members, AbstractList types,
                                       AbstractList fieldInits, AbstractList typeParameters)
    throws ClassDeclarationException
    {
      Type type = getType(name,typeParameters);

      if (type.kind() != Type.CLASS)
        throw new ClassDeclarationException("a type is already defined with name "+name);

      ClassType classType = (ClassType)type;

      if (classType.isDeclared())
        throw new ClassDeclarationException("duplicate class type declaration: "+name);

      if (classType.arguments() != null
          && typeParameters != null
          && typeParameters.size() != classType.arity())
        throw new ClassDeclarationException("wrong number of arguments for class "+name+
                                            "; expected: "+classType.arity()+", found: "+
                                            typeParameters.size());

      return classType.declareMembers(members,types,fieldInits,typeParameters);
    }

  /**
   * Registers a type alias definition for the specified name with the given type.
   */
  public final TypeDefinition defineTypeAlias (String name, Type definition)
    throws TypeDefinitionException
    {
      return defineTypeAlias(name,definition,null);
    }         

  /**
   * Registers a type alias definition for the specified name with the given type and the
   * list of type parameters.
   */
  public final TypeDefinition defineTypeAlias (String name, Type definition,
                                               AbstractList parameters)
    throws TypeDefinitionException
    {
      if (_typeTable.get(name) != null)
        throw new TypeDefinitionException("a type is already defined with name "+name);

      TypeDefinition typeDef = new TypeDefinition(name,definition,parameters);
      _typeTable.put(name,typeDef);

      return typeDef;
    }   
  
  /**
   * Registers a builtin type alias definition for the specified name with the given type.
   */
  public final TypeDefinition defineBuiltinTypeAlias (String name, Type definition)
    throws TypeDefinitionException
    {
      return defineBuiltinTypeAlias(name,definition,null);
    }         

  /**
   * Registers a builtin type alias definition for the specified name with the given type and parameters.
   */
  public final TypeDefinition defineBuiltinTypeAlias (String name, Type definition,
                                                      AbstractList parameters)
    throws TypeDefinitionException
    {
      TypeDefinition typeDef = defineTypeAlias(name,definition,parameters);
      _builtinTypeDefinitions.add(new BuiltinTypeDefinition(name,definition,parameters,false));
      return typeDef;
    }         

  /**
   * Registers a new (opaque) type definition for the specified name with the given type.
   */
  public final TypeDefinition defineNewType (String name, Type definition)
    throws TypeDefinitionException
    {
      return defineNewType(name,definition,null);
    }         

  /**
   * Registers a new (opaque) type definition for the specified name with the given type and the
   * list of type parameters.
   */
  public final TypeDefinition defineNewType (String name, Type definition, AbstractList parameters)
    throws TypeDefinitionException
    {
      if (_typeTable.get(name) != null)
        throw new TypeDefinitionException("a type is already defined with name "+name);

      TypeDefinition typeDef = new TypeDefinition(name,
                                                  new DefinedType(name,definition,parameters),
                                                  parameters);
      _typeTable.put(name,typeDef);

      return typeDef;
    }   
  
  /**
   * Registers a builtin new opaque type definition for the specified name with the given type.
   */
  public final TypeDefinition defineBuiltinNewType (String name, Type definition)
    throws TypeDefinitionException
    {
      return defineBuiltinNewType(name,definition,null);
    }         

  /**
   * Registers a builtin new opaque type definition for the specified name with the given type
   * and parameters.
   */
  public final TypeDefinition defineBuiltinNewType (String name, Type definition,
                                                    AbstractList parameters)
    throws TypeDefinitionException
    {
      TypeDefinition typeDef = defineNewType(name,definition,parameters);
      _builtinTypeDefinitions.add(new BuiltinTypeDefinition(name,definition,parameters,true));
      return typeDef;
    }         

  /**
   * Returns the <i>already</i> defined type with the specified name
   * from the type table.  <b>NB:</b> returns <tt>null</tt> if no type
   * is defined for this name.
   */
  public final Type getDefinedType (String name)
    {
      TypeDefinition typeDef = ((TypeDefinition)_typeTable.get(name));
      return typeDef == null ? null : typeDef.definition();
    }

  /**
   * Returns the type defined with the specified name in the type table; or,
   * if no type is defined with this name, a new registered undeclared class type
   * with this name.
   */
  public final Type getType (String name)
    {
      TypeDefinition typeDef = ((TypeDefinition)_typeTable.get(name));

      if (typeDef == null)
        _typeTable.put(name,typeDef = new TypeDefinition(name,new ClassType(this,name)));

      return typeDef.definition();
    }

  /**
   * Returns the type defined with the specified name in the type table
   * instantiated with the given types. If no type is declared with this name,
   * this will return an instance of a new undeclared class type and register
   * it as a definition for this name. If the number of types specified is not
   * the same as the expected arity, a <tt>StaticSemanticsErrorException</tt>
   * is thrown.
   */
  public final Type getType (String name, AbstractList types) throws StaticSemanticsErrorException
    {
      TypeDefinition typeDef = ((TypeDefinition)_typeTable.get(name));

      if (typeDef == null)
        {
          TypeParameter[] parameters = null;

          if (types != null)
            {
              parameters = new TypeParameter[types.size()];
              for (int i=0; i<parameters.length; i++)
                parameters[i] = new TypeParameter();
            }

          typeDef = new TypeDefinition(name,new ClassType(this,name).setArguments(parameters),
                                       parameters);
          _typeTable.put(name,typeDef);
        }

      return typeDef.instantiate(types);
    }

  /**
   * Shows the declared classes.
   */
  public final void showTypes ()
    {
      System.out.println("Showing registered types:\n");

      for (Iterator i = _typeTable.values().iterator(); i.hasNext();)
        System.out.println(i.next());
    }

  //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

  // These are conveniences to associate a new builtin instruction to a surface syntax
  // symbol and type. This will complain if the pair <i>(symbol,type)</i> is already
  // defined.

  public final void defineBuiltIn (Symbol symbol, Type type, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      symbol.defineBuiltIn(type,builtin);
      _builtinSymbols.add(new BuiltinDefinition(symbol.name(),type,builtin));
    }

  public final void defineBuiltIn (String name, Type type, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      defineBuiltIn(symbol(name),type,builtin);
    }

  public final void defineBuiltIn (Symbol symbol, Type domain, Type range, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      defineBuiltIn(symbol,new FunctionType(domain,range),builtin);
    }

  public final void defineBuiltIn (String name, Type domain, Type range, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      defineBuiltIn(symbol(name),domain,range,builtin);
    }

  public final void defineBuiltIn (Symbol symbol, Type domain1, Type domain2, Type range, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      Type[] domains = { domain1, domain2 };
      defineBuiltIn(symbol,new FunctionType(domains,range),builtin);
    }

  public final void defineBuiltIn (String name, Type domain1, Type domain2, Type range, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      defineBuiltIn(symbol(name),domain1,domain2,range,builtin);
    }

  public final void defineBuiltIn (Symbol symbol, Type domain1, Type domain2, Type domain3, Type range, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      Type[] domains = { domain1, domain2, domain3 };
      defineBuiltIn(symbol,new FunctionType(domains,range),builtin);
    }

  public final void defineBuiltIn (String name, Type domain1, Type domain2, Type domain3, Type range, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      defineBuiltIn(symbol(name),domain1,domain2,domain3,range,builtin);
    }

  public final void defineBuiltIn (Symbol symbol, Type[] domains, Type range, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      defineBuiltIn(symbol,new FunctionType(domains,range),builtin);
    }

  public final void defineBuiltIn (String name, Type[] domains, Type range, Instruction builtin)
    throws DuplicateCodeEntryException
    {
      defineBuiltIn(symbol(name),domains,range,builtin);
    }

  //\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

  private ArrayList _builtinSymbols = new ArrayList();
  private ArrayList _builtinTypeDefinitions = new ArrayList();

  private final void _redefineBuiltins () throws DuplicateCodeEntryException, TypeDefinitionException
    {
      for (Iterator i = _builtinSymbols.iterator(); i.hasNext();)
        {
          BuiltinDefinition bid = (BuiltinDefinition)i.next();
          symbol(bid.name).defineBuiltIn(bid.type,bid.instruction);
        }
      for (Iterator i =  _builtinTypeDefinitions.iterator(); i.hasNext();)
        {
          BuiltinTypeDefinition bid = (BuiltinTypeDefinition)i.next();
          if (bid.isNew)
            defineNewType(bid.name,bid.definition,bid.parameters);
          else
            defineTypeAlias(bid.name,bid.definition,bid.parameters);
        }
    }

  private static class BuiltinDefinition
    {
      String name;
      Type type;
      Instruction instruction;

      BuiltinDefinition (String name, Type type, Instruction instruction)
        {
          this.name = name;
          this.type = type;
          this.instruction = instruction;
        }
    }

  private static class BuiltinTypeDefinition
    {
      String name;
      Type definition;
      AbstractList parameters;
      boolean isNew;

      BuiltinTypeDefinition (String name, Type definition, AbstractList parameters, boolean isNew)
        {
          this.name = name;
          this.definition = definition;
          this.parameters = parameters;
          this.isNew = isNew;
        }
    }
}
