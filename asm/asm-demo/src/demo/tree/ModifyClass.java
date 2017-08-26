package demo.tree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class ModifyClass {

	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) throws Exception {
		File file = new File("TreeExample.class");
		InputStream input = new FileInputStream(file);
        byte[] byt = new byte[input.available()];
        input.read(byt);
        
		ClassReader cr = new ClassReader(byt);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		
		//添加字段
		cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, 
				"one", "I", null, new Integer(1)));
		//删除方法
		Iterator<MethodNode> i = cn.methods.iterator();
        while (i.hasNext()) {
            MethodNode mn = i.next();
            if ("methodA".equals(mn.name)) {
                i.remove();
            }
        }
		
		ClassWriter cw = new ClassWriter(0);
		cn.accept(cw);
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(cw.toByteArray());
        fout.close();
	}
}
