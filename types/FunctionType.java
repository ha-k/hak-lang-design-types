//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.types;

/**
 * @version     Last modified on Thu Mar 24 12:25:40 2016 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.AbstractList;

import hlt.language.util.Locatable;
import hlt.language.design.kernel.ParameterStack;

public class FunctionType extends ConstructedType
{
  private Type[] _domains;
  private Type _range;
  private BoxingMask _mask;

  protected boolean _noCurrying = false;

  public final int numberOfTypeComponents ()
    {
      return _domains.length + 1;
    }

  public final Type typeRefComponent (int n)
    {
      if (n >= 0 && n < _domains.length)
        return _domains[n];

      if (n == _domains.length)
        return _range;

      throw new NoSuchTypeComponentException(this,n);
    }

  public final void setTypeRefComponent (int n, Type type)
    {
      if (n >= 0 && n < _domains.length)
        _domains[n] = type;
      else
        if (n == _domains.length)
          _range = type;
        else
          throw new NoSuchTypeComponentException(this,n);
    }

  public FunctionType ()
    {
      this(1);
    }

  public FunctionType (int arity)
    {
      this(arity,new TypeParameter());
    }

  public FunctionType (int arity, Type range)
    {
      _domains = new Type[arity];
      for (int i=0; i<arity; i++)
        _domains[i] = new TypeParameter();
      _range = range;
      _mask = new BoxingMask(arity);
      _setMaskBoxes();
    }

  public FunctionType (Type domain, Type range)
    {
      Type[] domains = { domain };
      _domains = domains;
      _range = range;
      _mask = new BoxingMask(1);
      _setMaskBoxes();
    }

  public FunctionType (Type domain1, Type domain2, Type range)
    {
      Type[] domains = { domain1, domain2 };
      _domains = domains;
      _range = range;
      _mask = new BoxingMask(1);
      _setMaskBoxes();
    }

  public FunctionType (Type[] domains, Type range)
    {
      _domains = domains;
      _range = range;
      _mask = new BoxingMask(domains.length);
      _setMaskBoxes();
    }
 
  public FunctionType (Type[] domains, Type range, BoxingMask mask)
    {
      _domains = domains;
      _range = range;
      _mask = mask;
    }

  public FunctionType (Type[] domains, Type range, FunctionType copy)
    {
      _domains = domains;
      _range = range;
      _mask = new BoxingMask(domains.length);
      _setMaskBoxes(copy);
    }

  public FunctionType (AbstractList domains, Type range)
    {
      if (!domains.isEmpty())
        {
          _domains = new Type[domains.size()];
          for (int i=0; i<_domains.length; i++)
            _domains[i] = (Type)domains.get(i);
        }
      else
        {
          _domains = new Type[1];
          _domains[0] = Type.VOID;
        }

      _range = range;
      _mask = new BoxingMask(_domains.length);
      _setMaskBoxes();
    }

  public final boolean noCurrying ()
    {
      return _noCurrying;
    }

  public final FunctionType setNoCurrying ()
    {
      return setNoCurrying(true);
    }

  public final FunctionType setNoCurrying (boolean flag)
    {
      _noCurrying = flag;
      return this;
    }

  /**
   * Returns the constant that identifies this as a function type. 
   */
  public final byte kind ()
    {
      return FUNCTION;
    }

  /**
   * Returns list of domain types.
   */
  public final Type[] domains ()
    {
      return _domains;
    }

  /**
   * Returns the undereferenced boxing mask.
   */
  public final BoxingMask maskRef ()
    {
      return _mask;
    }

  /**
   * Returns the dereferenced boxing mask.
   */
  public final BoxingMask mask ()
    {
      return _mask.value();
    }

  /**
   * Sets the list of domain types to the specified list of types.
   */
  public final void setDomains (Type[] domains)
    {
      _domains = domains;
    }

  /**
   * Sets the range type to the specified type.
   */
  public final void setRange (Type range)
    {
      _range = range;
    }  

  /**
   * Sets the boxing mask to the specified one.
   */
  final void setMask (BoxingMask mask)
    {
      _mask = mask;
    }  

  /**
   * Returns the dereferenced value of the i-th domain type.
   */
  public final Type domain (int i)
    {
      return _domains[i].value();
    }

  /**
   * Returns the undereferenced i-th domain type.
   */
  public final Type domainRef (int i)
    {
      return _domains[i];
    }

  /**
   * Returns the undereferenced range type.
   */
  public final Type rangeRef ()
    {
      return _range;
    }

  /**
   * Returns the dereferenced value of the range type.
   */
  public final Type range ()
    {
      return _range.value();
    }

  public final boolean isPolymorphic ()
    {
      for (int i=0; i<_domains.length; i++)
        if (domain(i).isPolymorphic()) return true;

      return range().isPolymorphic();
    }

  /**
   * If this function type is of the form <tt>D1,...,Dn -&gt; R</tt>, this
   * method returns <tt>D2,...,Dn -&gt; R</tt> if <tt>n</tt> &gt; 0, or
   * <tt>R</tt> if <tt>n</tt> = 0.
   */
  public final Type curryedRange ()
    {
      int arity = arity()-1;
      if (arity == 0)
        return range();

      Type[] domains = new Type[arity];
      BoxingMask mask = new BoxingMask(arity);

      for (int i = arity; i-->0;)
        {
          int j = i+1;
          domains[i] = domain(j);
          if (domainIsBoxed(j)) mask.setDomainBox(i);
        }

      if (rangeIsBoxed()) mask.setRangeBox();

      return new FunctionType(domains,range(),mask);
    }

  public final int arity ()
    {
      return _domains.length;
    }

  /**
   * Sets the boxing mask of this function type according to that of the specified
   * function type (always a clone of this).  */
  private final void _setMaskBoxes (FunctionType copy)
    {
      for (int i=0; i<_domains.length; i++)
        if (copy.domainIsBoxed(i))
          _mask.setDomainBox(i);

      if (copy.rangeIsBoxed())
        _mask.setRangeBox();
    }

  /**
   * Sets the boxing mask of this function type according to the types that occur in
   * it. Note that type parameters do not set the mask - this will happen only at copy
   * time when generalizing the type and installing it as a defined function. This is
   * necessary because this method is used in the FunctionType constructor during the
   * type-checking process when type parameters may yet become bound to unboxed types.
   */
  private final void _setMaskBoxes ()
    {
      for (int i=0; i<_domains.length; i++)
        if (_domains[i].isBoxedType() && !(_domains[i].kind() == PARAMETER))
          _mask.setDomainBox(i);

      if (_range.isBoxedType() && !(_range.kind() == PARAMETER))
        _mask.setRangeBox();
    }

  public final boolean trueDomainIsBoxed (int i)
    {
      return _mask.domainIsBoxed(i);
    }

  public final boolean domainIsBoxed (int i)
    {
      return mask().domainIsBoxed(i);
    }

  public final void setDomainBox (int i)
    {
      mask().setDomainBox(i);
    }

  public final void unsetDomainBox (int i)
    {
      mask().unsetDomainBox(i);
    }

  public final boolean trueRangeIsBoxed ()
    {
      return _mask.rangeIsBoxed();
    }

  public final boolean rangeIsBoxed ()
    {
      return mask().rangeIsBoxed();
    }

  public final void setRangeBox ()
    {
      mask().setRangeBox();
    }

  public final void unsetRangeBox ()
    {
      mask().unsetRangeBox();
    }

  public final boolean mustWrapArgument (FunctionType actualType, int i)
    {
      return !trueDomainIsBoxed(i) && actualType.trueDomainIsBoxed(i);
    }

  public final boolean mustUnwrapArgument (FunctionType actualType, int i)
    {
      return trueDomainIsBoxed(i) && !actualType.trueDomainIsBoxed(i);
    }

  public final boolean mustWrapResult (FunctionType actualType)
    {
      return trueRangeIsBoxed() && !actualType.trueRangeIsBoxed();
    }

  public final boolean mustUnwrapResult (FunctionType actualType)
    {
      return !trueRangeIsBoxed() && actualType.trueRangeIsBoxed();
    }

  public final boolean argumentSortsDisagree (FunctionType actualType, int i)
    {
      return mustWrapArgument(actualType,i) || mustUnwrapArgument(actualType,i);
    }

  public final boolean resultSortsDisagree (FunctionType actualType)
    {
      return mustWrapResult(actualType) || mustUnwrapResult(actualType);
    }

  /**
   * Flattens this function type and all function types that occur
   * in it by uncurrying all the domains. Note that care is taken to
   * copy the boxing masks which are set according to those of
   * the domains and range.
   */
  public final Type flatten ()
    {
      for (int i=0; i<arity(); i++)
        _domains[i] = _domains[i].flatten();
      
      _range = _range.flatten();

      if (_range.kind() == FUNCTION)
        {
          FunctionType range = (FunctionType)_range;
          Type[] domains = new Type[arity()+range.arity()];
          BoxingMask mask = new BoxingMask(arity()+range.arity());
          
          int i = 0;
          
          for (; i<_domains.length; i++)
            {
              domains[i] = _domains[i];
              if (domainIsBoxed(i))
                mask.setDomainBox(i);
            }
          
          for (; i<domains.length; i++)
            {
              domains[i] = range.domains()[i-_domains.length];
              if (range.domainIsBoxed(i-_domains.length))
                mask.setDomainBox(i);
            }

          if (range.rangeIsBoxed())
            mask.setRangeBox();

          _domains = domains;
          _range = range.range();
          _mask = mask;
        }

      return this;
    }

  /**
   * Transforms this function type by splitting its domains at the
   * specified depth, and records the old form in the appropriate trail of
   * the specified type-checker for undoing purposes. For example,
   * currying <tt>A[1],...,A[n] -&gt; A</tt> at depth <tt>k&lt;n</tt>,
   * transforms it into <tt>A[1],...,A[k] -&gt; (A[k+1],...,A[n] -&gt; A)</tt>.
   * Note that the boxing masks are carefully set to reflect the original
   * type boxing information that was obtained by the <tt>copy</tt> method
   * from the standardized types.
   */
  public final void curry (int depth, TypeChecker typeChecker)
    {
      if (depth >= arity()) return;

      Type[] actualDomains = new Type[depth];
      BoxingMask actualMask = new BoxingMask(depth);
      Type[] remainingDomains = new Type[arity()-depth];
      BoxingMask remainingMask = new BoxingMask(remainingDomains.length);

      for (int i=0; i<depth; i++)
        {
          actualDomains[i] = domain(i);
          if (domainIsBoxed(i))
            actualMask.setDomainBox(i);
        }

      actualMask.setRangeBox(); // since the range is a function, it must be boxed.

      for (int i=depth; i<arity(); i++)
        {
          remainingDomains[i-depth] = domain(i);
          if (domainIsBoxed(i))
            remainingMask.setDomainBox(i-depth);
        }

      if (rangeIsBoxed())
        remainingMask.setRangeBox();

      typeChecker.trail(this,_domains,_range,_mask);
      _domains = actualDomains;
      _range = new FunctionType(remainingDomains,_range,remainingMask);
      _mask = actualMask;
    }

  /**
   * Does the same as the version above, except that it is a one-time destructive
   * procedure (similar to the two versions of the unify method).
   */
  public final void curry (int depth)
    {
      if (depth >= arity()) return;

      Type[] actualDomains = new Type[depth];
      Type[] remainingDomains = new Type[arity()-depth];

      for (int i=0; i<depth; i++)
        actualDomains[i] = domain(i);

      for (int i=depth; i<arity(); i++)
        remainingDomains[i-depth] = domain(i);

      _domains = actualDomains;
      _range = new FunctionType(remainingDomains,_range);
    }

  /**
   * When this function type is of the form <tt>A[1],...,A[n] -&gt;
   * (B[1],...,B[m] -&gt; R)</tt>, this returns the function type
   * <tt>A[1],...,A[n],B[1],...,B[m] -&gt; R</tt>, making sure to preserves
   * boxing masks appropriately.
   */
  public final FunctionType uncurry ()
    {
      FunctionType curryedRange = (FunctionType)range();

      Type[] domains = new Type[arity()+curryedRange.arity()];
      BoxingMask mask = new BoxingMask(domains.length);

      for (int i=0; i<arity(); i++)
        {
          domains[i] = domain(i);
          if (domainIsBoxed(i))
            mask.setDomainBox(i);
        }

      for (int i=arity(); i<domains.length; i++)
        {
          int j = i-arity();
          domains[i] = curryedRange.domain(j);
          if (curryedRange.domainIsBoxed(j))
            mask.setDomainBox(i);
        }

      if (curryedRange.rangeIsBoxed())
        mask.setRangeBox();

      return new FunctionType(domains,curryedRange.range(),mask);
    }

  /**
   * Returns a copy of this FunctionType as per the renaming given by the
   * specified HashMap. Note that copying preserves original type boxing
   * information, including type parameters that must be treated as boxed.
   */
  public final Type copy (HashMap parameters)
    {
      Type[] domains = new Type[arity()];
      Type range;
      BoxingMask mask = new BoxingMask(arity());

      for (int i=0; i<arity(); i++)
        {
          if (domainIsBoxed(i) || domain(i).isBoxedType())
            mask.setDomainBox(i);

          if (domain(i).kind() == BOXABLE)
            domains[i] = new BoxableTypeConstant(domain(i),domainIsBoxed(i));
          else
            {
              domains[i] = domain(i).copy(parameters);
              if (domains[i].isPrimitive() && domainIsBoxed(i))
                domains[i] = new BoxableTypeConstant(domains[i],true);
            }              
        }

      if (rangeIsBoxed() || range().isBoxedType())
        mask.setRangeBox();

      if (range().kind() == BOXABLE)
        range = new BoxableTypeConstant(range(),rangeIsBoxed());
      else
        {
          range = range().copy(parameters);
          if (range.isPrimitive() && rangeIsBoxed())
            range = new BoxableTypeConstant(range,true);
        }

      return new FunctionType(domains,range,mask).setNoCurrying(_noCurrying);
    }

  /**
   * Returns an instantiated copy of this function type as per the specified
   * substitution. Note that the boxing mask is set according to the nature
   * of the domain and range types.
   */
  public final Type instantiate (HashMap substitution)
    {
      BoxingMask mask = new BoxingMask(arity());

      Type range = range().instantiate(substitution);
      if (range.isBoxedType()) mask.setRangeBox();
      
      Type[] domains = new Type[arity()];
      for (int i=0; i<domains.length; i++)
        {
          domains[i] = domain(i).instantiate(substitution);
          if (domains[i].isBoxedType()) mask.setDomainBox(i);
        }

      return new FunctionType(domains,range,mask).setNoCurrying(_noCurrying);
    }

  /**
   * Unifies this function type with the given type in the context of the
   * given type-checker. Note that this may cause currying transformations
   * to be applied as needed, unless currying has been explicitly forbidden
   * through an <a href="../kernel/Application.html"><tt>Application</tt></a>
   * whose <tt>setNoCurrying()</tt> method has been invoked to inhibit the
   * currying of its operation.
   *
   * <p>
   *
   * All bindings and currying transformations are appropriately trailed
   * in the contextual type-checker for undoing purposes. If unification
   * fails, a FailedUnificationException is thrown.
   */
  public final void unify (Type type, TypeChecker typeChecker) throws FailedUnificationException
    {
      if ((type = type.value()) == this)
        return;

      switch (type.kind())
        {
        case PARAMETER:
          type.unify(this,typeChecker);
          return;

        case FUNCTION:
          FunctionType other = (FunctionType)type;

          if (noCurrying() || other.noCurrying())
            {
              if (arity() != other.arity())
                typeChecker.error(new TypeClashException("wrong number of arguments"));
            }
          else
            {
              curry(other.arity(),typeChecker);
              other.curry(arity(),typeChecker);
            }

          for (int i=0; i<arity(); i++)
            domain(i).unify(other.domains()[i],typeChecker);
          range().unify(other.rangeRef(),typeChecker);
          //mask().unify(other.mask(),typeChecker);
          /*
          Mask unification is commented out because it interferes with expression padding the
          way it is done in Expression and used in Application. The real fix should be to pad
          an expression in Application in the typeCheck method *before* unifying the argument
          types (since this merges boxing masks and annihilates padding analysis which is based
          on mask diferences between actuals and formals). For now, NOT unifying masks does
          work, but is not foolproof... Indeed, unifying function types outside the context of
          an application may end up with function types with incorrect masks. -hlt
          */
          return;

        default:
          typeChecker.error(new TypeClashException(this,type));
        }
    }

  public final boolean unify (Type type)
    {
      if ((type = type.findValue()) == this)
        return true;

      switch (type.kind())
        {
        case PARAMETER:
          ((TypeParameter)type).bind(this);
          return true;

        case FUNCTION:
          FunctionType other = (FunctionType)type;

          if (!(noCurrying() || other.noCurrying()))
            {
              curry(other.arity());
              other.curry(arity());
            }

          int position = arity();
          boolean ok = (position == other.arity());

          while (ok && position-->0)
            ok &= _domains[position].findValue().unify(other.domains()[position]);

          return ok && rangeRef().findValue().unify(other.rangeRef());
        }

      return false;
    }

  public final void checkOccurrence (TypeParameter parameter, Type context, TypeChecker typeChecker)
    throws FailedUnificationException
    {
      for (int i=0; i<arity(); i++)
        domain(i).checkOccurrence(parameter,context,typeChecker);

      range().checkOccurrence(parameter,context,typeChecker);
    }

  public final HashSet getParameters (HashSet set)
    {
      for (int i=arity(); i-->0;)
        domain(i).getParameters(set);
      return range().getParameters(set);
    }

  public final int eqCode ()
    {
      int code =  kind() + arity() + range().eqCode();

      for (int i=0; i<arity(); i++)
        code += (i+1)*domain(i).eqCode();

      return code;
    }

  /**
   * Returns true iff this type is structurally isomorphic to the
   * specified type. Two type parameters will be considered equal only
   * if they are identical objects (i.e., same pointers).  Note that
   * this assumes that both types are in flattened form (i.e., fully
   * uncurryed) - which is the case of all defined symbol's types.
   */
  public final boolean isEqualTo (Type type)
    {
      if (this == type)
        return true;

      if (type.kind() != FUNCTION)
        return false;

      FunctionType functionType = (FunctionType)type;
          
      if (arity() != functionType.arity())
        return false;

      for (int i=0; i<arity(); i++)
        if (!domain(i).isEqualTo(functionType.domain(i)))
          return false;

      return range().isEqualTo(functionType.range());
    }

  /**
   * Returns true iff this type is structurally isomorphic to the
   * specified type, under the renaming of type parameters specified by
   * the given HashMap.  Note that this assumes that both types are in
   * flattened form (i.e., fully uncurryed) - which is the case of all
   * defined symbol's types.
   */
  public final boolean isEqualTo (Type type, HashMap parameters)
    {
      if (this == type)
        return true;

      if (type.kind() != FUNCTION)
        return false;

      FunctionType functionType = (FunctionType)type;
          
      if (arity() != functionType.arity())
        return false;

      for (int i=0; i<arity(); i++)
        if (!domain(i).isEqualTo(functionType.domain(i),parameters))
          return false;

      return range().isEqualTo(functionType.range(),parameters);
    }

  public final String maskString ()
    {
      StringBuilder buf = new StringBuilder();

      for (int i=0; i<arity(); i++)
        {
          if (domain(i).kind() == Type.FUNCTION)
            buf.append("(").append(((FunctionType)domain(i)).maskString()).append(")");
          else
            buf.append(domainIsBoxed(i) ? "[]" : "_");

          if (i < arity()-1) buf.append(", ");
        }
      
      buf.append(" -> ");

      if (range().kind() == Type.FUNCTION)
        buf.append(((FunctionType)range()).maskString());
      else
        buf.append(rangeIsBoxed() ? "[]" : "_");

      return buf.toString();
    }      

  /**
   * Returns a string form for a function type.
   */
  public final String toString ()
    {
      String s;

      if (arity() == 1)
        {
          s = domain(0).toString();

          if (domain(0).kind() == FUNCTION)
            s = "(" + s + ")";

          s += " -> " + range();
        }
      else
        {
          s = "(";

          for (int i=0; i<arity(); i++)
            {
              s += domain(i);
              if (i < arity()-1) s += ", ";
            }

          s += ") -> " + range();
        }

      return s ;
    }

//    /**
//     * Returns a string form for a function type with explicit
//     * boxing information - for debugging purposes only...
//     */
//    public final String toString ()
//      {
//        String s;

//        if (arity() == 1)
//          {
//            s = _bform(0);

//            if (domain(0).kind() == FUNCTION)
//              s = "(" + s + ")";

//            return s + " -> " + _bform();
//          }

//        s = "(";

//        for (int i=0; i<arity(); i++)
//          {
//            s += _bform(i);
//            if (i < arity()-1) s += ", ";
//          }

//        return s + ") -> " + _bform();
//      }

//    private final String _bform (int i)
//      {
//        if (domain(i).kind() != FUNCTION && domainIsBoxed(i))
//          return "#"+domain(i);

//        return domain(i).toString();
//      }

//    private final String _bform ()
//      {
//        if (range().kind() != FUNCTION && rangeIsBoxed()) return "#"+range();

//        return range().toString();
//      }

}
