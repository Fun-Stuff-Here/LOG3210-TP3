/* Generated By:JJTree: Do not edit this line. ASTGenValue.java Version 7.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package analyzer.ast;

public
class ASTGenValue extends SimpleNode {
  public ASTGenValue(int id) {
    super(id);
  }

  public ASTGenValue(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d55f914c8f1485a903612d15b1814ab5 (do not edit this line) */