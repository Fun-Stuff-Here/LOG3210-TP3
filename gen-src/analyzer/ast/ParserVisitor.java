/* Generated By:JavaCC: Do not edit this line. ParserVisitor.java Version 7.0.2 */
package analyzer.ast;

public interface ParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTProgram node, Object data);
  public Object visit(ASTDeclaration node, Object data);
  public Object visit(ASTBlock node, Object data);
  public Object visit(ASTStmt node, Object data);
  public Object visit(ASTForStmt node, Object data);
  public Object visit(ASTIfStmt node, Object data);
  public Object visit(ASTWhileStmt node, Object data);
  public Object visit(ASTAssignStmt node, Object data);
  public Object visit(ASTExpr node, Object data);
  public Object visit(ASTBoolExpr node, Object data);
  public Object visit(ASTCompExpr node, Object data);
  public Object visit(ASTAddExpr node, Object data);
  public Object visit(ASTMulExpr node, Object data);
  public Object visit(ASTUnaExpr node, Object data);
  public Object visit(ASTNotExpr node, Object data);
  public Object visit(ASTGenValue node, Object data);
  public Object visit(ASTBoolValue node, Object data);
  public Object visit(ASTIdentifier node, Object data);
  public Object visit(ASTIntValue node, Object data);
  public Object visit(ASTSwitchStmt node, Object data);
  public Object visit(ASTDefaultStmt node, Object data);
  public Object visit(ASTCaseStmt node, Object data);
}
/* JavaCC - OriginalChecksum=9b4e8ac19e0d085200244ca6fa12b236 (do not edit this line) */
