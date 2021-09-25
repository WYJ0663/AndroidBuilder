package com.qiqi.inject;

import com.qiqi.main.Main;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

class TraceClassAdapter(api: Int, cv: ClassVisitor?, var mInjectData: MatrixInjector.InjectData?) : ClassVisitor(api, cv) {

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        if (mInjectData?.methodName != null && mInjectData?.methodName.equals(name)) {// && opcode == Opcodes.RETURN
            val methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)

            return TraceMethodAdapter(api, methodVisitor, access, name, desc, mInjectData)
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }


    class TraceMethodAdapter(api: Int, mv: MethodVisitor?, access: Int, name: String?, desc: String?, var mInjectData: MatrixInjector.InjectData?)
        : AdviceAdapter(api, mv, access, name, desc) {

        override fun onMethodEnter() {
            super.onMethodEnter()
            if (mInjectData?.name != null && mInjectData?.name.equals(Main.builderExtension?.applicationName)) {
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/hotpatch/hack/HotPatchApplication", "init", "(Landroid/content/Context;)V", false);
            }
        }

        override fun onMethodExit(opcode: Int) {
            super.onMethodExit(opcode)
        }
    }

}

