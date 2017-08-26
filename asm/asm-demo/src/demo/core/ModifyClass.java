package demo.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ModifyClass {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		File file = new File("Example.class");
		InputStream input = new FileInputStream(file);
        byte[] byt = new byte[input.available()];
        input.read(byt);
        
		ClassReader cr = new ClassReader(byt);
		ClassWriter cw = new ClassWriter(0);
        ClassVisitor cv = new ModifyClassMethodVisitor(cw);
        cr.accept(cv, 0);
        
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(cw.toByteArray());
        fout.close();
	}
	
	private static class ModifyClassMethodVisitor extends ClassVisitor{
		 
	    public ModifyClassMethodVisitor(int api) {
	        super(api);
	    }
	 
	    public ModifyClassMethodVisitor(ClassWriter cw) {
	        super(Opcodes.ASM5, cw);
	    }
	 
	    // 移除内部类
	    @Override
	    public void visitInnerClass(String name, String outerName, String innerName, int access) {
	    }
	 
	    @Override
	    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
	        if ("main".equals(name)) {
	            // 移除main方法
	            return null;
	        }
	        return cv.visitMethod(access, name, desc, signature, exceptions);
	    }
	    
	    // 添加一个字段
	    @Override
	    public void visitEnd() {
            FieldVisitor fv = cv.visitField(Opcodes.ACC_PRIVATE, "addedField", "I", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
	        cv.visitEnd();
	    }
	}
}
