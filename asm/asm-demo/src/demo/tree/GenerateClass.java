package demo.tree;

import java.io.File;
import java.io.FileOutputStream;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

//要生成的class：
//public abstract class TreeExample {
//	  
//  public static final int zero = 0;
//  
//  public abstract int methodA(java.lang.Object arg0);
//}
public class GenerateClass {

	public static void main(String[] args){
		ClassWriter cw = new ClassWriter(Opcodes.ASM5);
        ClassNode cn = generate();
        cn.accept(cw);
        try {
            File file = new File("TreeExample.class");
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(cw.toByteArray());
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	@SuppressWarnings("unchecked")
	private static ClassNode generate() {
        ClassNode classNode = new ClassNode();
        classNode.version = Opcodes.V1_7;
        classNode.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT;
        classNode.name = "demo/tree/TreeExample";
        classNode.superName = "java/lang/Object";
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, 
        		"zero", "I", null, new Integer(0)));
        classNode.methods.add(new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, 
        		"methodA", "(Ljava/lang/Object;)I", null, null));
        return classNode;
    }
}
