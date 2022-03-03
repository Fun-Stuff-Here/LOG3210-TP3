/* Generated By:JavaCC: Do not edit this line. ParserDefaultVisitor.java Version 7.0.2 */
package analyzer.ast;

public class ParserDefaultVisitor implements ParserVisitor{
  public Object defaultVisit(SimpleNode node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(SimpleNode node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTProgram node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTDeclaration node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTBlock node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTForStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIfStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTWhileStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAssignStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTExpr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTBoolExpr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTCompExpr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTAddExpr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTMulExpr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTUnaExpr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTNotExpr node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTGenValue node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTBoolValue node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIdentifier node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTIntValue node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTSwitchStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTDefaultStmt node, Object data){
    return defaultVisit(node, data);
  }
  public Object visit(ASTCaseStmt node, Object data){
    return defaultVisit(node, data);
  }
}
/* JavaCC - OriginalChecksum=f4b0801d172fb3695e9bbd2a6a0e493b (do not edit this line) */