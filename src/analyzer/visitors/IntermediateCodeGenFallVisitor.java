package analyzer.visitors;

import analyzer.ast.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import sun.awt.Symbol;

import java.awt.*;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;


/**
 * Created: 19-02-15
 * Last Changed: 20-10-6
 * Author: Félix Brunet & Doriane Olewicki
 * Modified by: Gérard Akkerhuis
 *
 * Description: Ce visiteur explore l'AST et génère un code intermédiaire.
 */

public class IntermediateCodeGenFallVisitor implements ParserVisitor {

    //le m_writer est un Output_Stream connecter au fichier "result". c'est donc ce qui permet de print dans les fichiers
    //le code généré.
    private final PrintWriter m_writer;

    public IntermediateCodeGenFallVisitor(PrintWriter writer) {
        m_writer = writer;
    }
    public HashMap<String, VarType> SymbolTable = new HashMap<>();

    private int id = 0;
    private int label = 0;
    private String fall = "fall";

    /*
    génère une nouvelle variable temporaire qu'il est possible de print
    À noté qu'il serait possible de rentrer en conflit avec un nom de variable définit dans le programme.
    Par simplicité, dans ce tp, nous ne concidérerons pas cette possibilité, mais il faudrait un générateur de nom de
    variable beaucoup plus robuste dans un vrai compilateur.
     */
    private String genId() {
        return "_t" + id++;
    }

    //génère un nouveau Label qu'il est possible de print.
    private String genLabel() {
        return "_L" + label++;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTProgram node, Object data)  {
        String next = genLabel();
        node.childrenAccept(this, next);
        m_writer.println(next);
        return null;
    }

    /*
    Code fournis pour remplir la table de symbole.
    Les déclarations ne sont plus utile dans le code à trois adresse.
    elle ne sont donc pas concervé.
     */
    @Override
    public Object visit(ASTDeclaration node, Object data) {
        ASTIdentifier id = (ASTIdentifier) node.jjtGetChild(0);
        VarType t;
        if(node.getValue().equals("bool")) {
            t = VarType.Bool;
        } else {
            t = VarType.Number;
        }
        SymbolTable.put(id.getValue(), t);
        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if(i == node.jjtGetNumChildren() -1){
                node.jjtGetChild(i).jjtAccept(this, data);
            }
            else {
                String next = genLabel();
                node.jjtGetChild(i).jjtAccept(this, next);
                m_writer.println(next);
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTStmt node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTForStmt node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    /*
    le If Stmt doit vérifier s'il à trois enfants pour savoir s'il s'agit d'un "if-then" ou d'un "if-then-else".
     */
    @Override
    public Object visit(ASTIfStmt node, Object data) {
        String next = (String) data;
        if(node.jjtGetNumChildren() <=2) handleIf(node,next);
        if(node.jjtGetNumChildren() >2) handleIfElse(node,next);
        return null;
    }

    private void handleIf(ASTIfStmt node, String next){
        BoolLabel B = new BoolLabel(fall,next);
        node.jjtGetChild(0).jjtAccept(this,B);
        node.jjtGetChild(1).jjtAccept(this,next);
    }

    private void handleIfElse(ASTIfStmt node, String next){
        BoolLabel B = new BoolLabel(fall,genLabel());
        node.jjtGetChild(0).jjtAccept(this,B);
        node.jjtGetChild(1).jjtAccept(this,next);
        m_writer.println("goto "+next);
        m_writer.println(B.lFalse);
        node.jjtGetChild(2).jjtAccept(this,next);
    }


    @Override
    public Object visit(ASTWhileStmt node, Object data) {
        String next = (String) data;
        String begin = genLabel();
        BoolLabel B = new BoolLabel(fall,next);
        m_writer.println(begin);
        node.jjtGetChild(0).jjtAccept(this,B);
        node.jjtGetChild(1).jjtAccept(this,begin);
        m_writer.println("goto "+begin);
        return null;
    }


    @Override
    public Object visit(ASTAssignStmt node, Object data) {
        String id = ((ASTIdentifier) node.jjtGetChild(0)).getValue();
        VarType type = SymbolTable.get(id);
        if(type == VarType.Bool) {
            String next = (String) data;
            BoolLabel B = new BoolLabel(fall, genLabel());
            node.jjtGetChild(1).jjtAccept(this, B);
            m_writer.println(id+" = 1");
            m_writer.println("goto " + next);
            m_writer.println(B.lFalse);
            m_writer.println(id+" = 0");
        }
        else{
            String EAddr = (String) node.jjtGetChild(1).jjtAccept(this, data);
            m_writer.println(id + " = " + EAddr);
        }
        return null;
    }


    @Override
    public Object visit(ASTExpr node, Object data){
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    //Expression arithmétique
    /*
    Les expressions arithmétique add et mult fonctionne exactement de la même manière. c'est pourquoi
    il est plus simple de remplir cette fonction une fois pour avoir le résultat pour les deux noeuds.

    On peut bouclé sur "ops" ou sur node.jjtGetNumChildren(),
    la taille de ops sera toujours 1 de moins que la taille de jjtGetNumChildren
     */
    public Object codeExtAddMul(SimpleNode node, Object data, Vector<String> ops) {
        if (node.jjtGetNumChildren() == 1) return node.jjtGetChild(0).jjtAccept(this, data);
        String EAddr= genId();
        String E1Addr = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String E2Addr = (String) node.jjtGetChild(1).jjtAccept(this, data);
        m_writer.println(EAddr + " = "+ E1Addr + " "+ ops.get(0) +" "+E2Addr);
        return EAddr;
    }

    @Override
    public Object visit(ASTAddExpr node, Object data) {
        return codeExtAddMul(node, data, node.getOps());
    }

    @Override
    public Object visit(ASTMulExpr node, Object data) {
        return codeExtAddMul(node, data, node.getOps());
    }

    //UnaExpr est presque pareil au deux précédente. la plus grosse différence est qu'il ne va pas
    //chercher un deuxième noeud enfant pour avoir une valeur puisqu'il s'agit d'une opération unaire.
    @Override
    public Object visit(ASTUnaExpr node, Object data) {
        Vector<String> ops = node.getOps();
        if(ops.size()==0) return node.jjtGetChild(0).jjtAccept(this, data);
        String Ei1 = (String) node.jjtGetChild(0).jjtAccept(this, data);
        String Ei = genId();
        m_writer.println(Ei +" = "+ops.get(0) + " "+Ei1);
        for (int i = 1; i < ops.size(); i++) {
            Ei1 = Ei;
            Ei = genId();
            m_writer.println(Ei +" = "+ops.get(i) + " "+Ei1);
        }
        return Ei;
    }

    //expression logique
    @Override
    public Object visit(ASTBoolExpr node, Object data) {
        if (node.jjtGetNumChildren() == 1) return node.jjtGetChild(0).jjtAccept(this, data);
        BoolLabel B = (BoolLabel) data;
        String op = node.getOps().get(0).toString();
        if(op.equals("&&")) handleAnd(node,B);
        if(op.equals("||")) handleOr(node,B);
        return null;
    }

    private void handleAnd(ASTBoolExpr node, BoolLabel B){
        BoolLabel B1 = new BoolLabel(fall,B.lFalse);
        if(B.lFalse.equals(fall)){
            B1.lFalse = genLabel();
        }
        BoolLabel B2 = new BoolLabel(B.lTrue, B.lFalse);
        node.jjtGetChild(0).jjtAccept(this, B1);
        node.jjtGetChild(1).jjtAccept(this, B2);
        if(B.lFalse.equals(fall)){
            m_writer.println(B1.lFalse);
        }
    }

    private void handleOr(ASTBoolExpr node, BoolLabel B){
        BoolLabel B1 = new BoolLabel(B.lTrue,fall);
        if(B.lTrue.equals(fall)){
            B1.lTrue = genLabel();
        }
        BoolLabel B2 = new BoolLabel(B.lTrue,B.lFalse);
        node.jjtGetChild(0).jjtAccept(this, B1);
        node.jjtGetChild(1).jjtAccept(this, B2);
        if(B.lTrue.equals(fall)){
            m_writer.println(B1.lTrue);
        }
    }


    @Override
    public Object visit(ASTCompExpr node, Object data) {
        if (node.jjtGetNumChildren() ==1) return node.jjtGetChild(0).jjtAccept(this, data);
        BoolLabel B = (BoolLabel) data;
        String rel = node.getValue();
        if(B.lTrue.equals(fall) && B.lFalse.equals(fall)){
            m_writer.println("error");
            return null;
        }
        String E1Addr = node.jjtGetChild(0).jjtAccept(this, B).toString();
        String E2Addr = node.jjtGetChild(1).jjtAccept(this, B).toString();
        if(!B.lTrue.equals(fall)){
            m_writer.println("if "+E1Addr+" "+rel+" "+E2Addr+" goto "+B.lTrue);
            if(!B.lFalse.equals(fall)){
                m_writer.println("goto " + B.lFalse);
            }
        }
        else{
            m_writer.println("ifFalse "+E1Addr+" "+rel+" "+E2Addr+" goto "+B.lFalse);
        }
        return null;
    }


    /*
    Même si on peut y avoir un grand nombre d'opération, celle-ci s'annullent entre elle.
    il est donc intéressant de vérifier si le nombre d'opération est pair ou impaire.
    Si le nombre d'opération est pair, on peut simplement ignorer ce noeud.
     */
    @Override
    public Object visit(ASTNotExpr node, Object data) {
        if(data instanceof BoolLabel && node.getOps().size()%2==1){
            BoolLabel B = (BoolLabel) data;
            BoolLabel B1 = new BoolLabel(B.lFalse,B.lTrue);
            return node.jjtGetChild(0).jjtAccept(this, B1);
        }
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    @Override
    public Object visit(ASTGenValue node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    /*
    BoolValue ne peut pas simplement retourné sa valeur à son parent contrairement à GenValue et IntValue,
    Il doit plutôt généré des Goto direct, selon sa valeur.
     */
    @Override
    public Object visit(ASTBoolValue node, Object data) {
        BoolLabel B = (BoolLabel) data;
        if(node.getValue()){
            if(!B.lTrue.equals(fall)){
                m_writer.println("goto "+B.lTrue);
            }
        }
        else{
            if(!B.lFalse.equals(fall)){
                m_writer.println("goto "+B.lFalse);
            }
        }
        return null;
    }


    /*
    si le type de la variable est booléenne, il faudra généré des goto ici.
    le truc est de faire un "if value == 1 goto Label".
    en effet, la structure "if valeurBool goto Label" n'existe pas dans la syntaxe du code à trois adresse.
     */
    @Override
    public Object visit(ASTIdentifier node, Object data) {
        String id = node.getValue();
        VarType type = SymbolTable.get(id);
        if(type == VarType.Bool){
            BoolLabel B = (BoolLabel) data;
            if(!B.lTrue.equals(fall)){
                m_writer.println("if "+id+" == 1 goto "+B.lTrue);
                if(!B.lFalse.equals(fall)){
                    m_writer.println("goto "+B.lFalse);
                }
            }
            else if(!B.lFalse.equals(fall)){
                m_writer.println("ifFalse "+id+" == 1 goto "+B.lFalse);
            }
            else{
                m_writer.println("error");
            }
        }
        return id;
    }

    @Override
    public Object visit(ASTIntValue node, Object data) {
        return Integer.toString(node.getValue());
    }


    @Override
    public Object visit(ASTSwitchStmt node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild(i).jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTCaseStmt node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild(i).jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTDefaultStmt node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild(i).jjtAccept(this, data);
        }
        return null;
    }

    //des outils pour vous simplifier la vie et vous enligner dans le travail
    public enum VarType {
        Bool,
        Number
    }

    //utile surtout pour envoyé de l'informations au enfant des expressions logiques.
    private class BoolLabel {
        public String lTrue = null;
        public String lFalse = null;

        public BoolLabel(String t, String f) {
            lTrue = t;
            lFalse = f;
        }
    }


}
