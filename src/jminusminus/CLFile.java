// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.IOException;
import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * This class provides a representation of the ClassFile structure.
 */
class CLFile {
    /**
     * ClassFile.magic item.
     */
    public long magic; // 0xCAFEBABE

    /**
     * ClassFile.minor_version item.
     */
    public int minorVersion;

    /**
     * ClassFile.major_version item.
     */
    public int majorVersion;

    /**
     * ClassFile.constant_pool_count item.
     */
    public int constantPoolCount;

    /**
     * ClassFile.constant_pool item.
     */
    public CLConstantPool constantPool;

    /**
     * ClassFile.access_flags item.
     */
    public int accessFlags;

    /**
     * ClassFile.this_class item.
     */
    public int thisClass;

    /**
     * ClassFile.super_class item.
     */
    public int superClass;

    /**
     * ClassFile.interfaces_count item.
     */
    public int interfacesCount;

    /**
     * ClassFile.interfaces item.
     */
    public ArrayList<Integer> interfaces;

    /**
     * ClassFile.fields_count item.
     */
    public int fieldsCount;

    /**
     * ClassFile.fields item.
     */
    public ArrayList<CLFieldInfo> fields;

    /**
     * ClassFile.methods_count item.
     */
    public int methodsCount;

    /**
     * ClassFile.methods item.
     */
    public ArrayList<CLMethodInfo> methods;

    /**
     * ClassFile.attributes_count item.
     */
    public int attributesCount;

    /**
     * ClassFile.attributes item.
     */
    public ArrayList<CLAttributeInfo> attributes;

    /**
     * Writes the contents of this class to the specified output stream.
     *
     * @param out output stream.
     * @throws IOException if an error occurs while writing.
     */
    public void write(CLOutputStream out) throws IOException {
        out.writeInt(magic);
        out.writeShort(minorVersion);
        out.writeShort(majorVersion);
        out.writeShort(constantPoolCount);
        constantPool.write(out);
        out.writeShort(accessFlags);
        out.writeShort(thisClass);
        out.writeShort(superClass);
        out.writeShort(interfacesCount);
        for (Integer index : interfaces) {
            out.writeShort(index.intValue());
        }
        out.writeShort(fieldsCount);
        for (CLMemberInfo fieldInfo : fields) {
            if (fieldInfo != null) {
                fieldInfo.write(out);
            }
        }
        out.writeShort(methodsCount);
        for (CLMemberInfo methodInfo : methods) {
            if (methodInfo != null) {
                methodInfo.write(out);
            }
        }
        out.writeShort(attributesCount);
        for (CLAttributeInfo attributeInfo : attributes) {
            if (attributeInfo != null) {
                attributeInfo.write(out);
            }
        }
    }

    /**
     * Returns a string identifying the inner class access permissions and properties contained in
     * the specified mask of flags.
     *
     * @param accessFlags mask of access flags.
     * @return a string identifying the inner class access permissions and properties.
     */
    public static String innerClassAccessFlagsToString(int accessFlags) {
        StringBuffer b = new StringBuffer();
        if ((accessFlags & ACC_PUBLIC) != 0) {
            b.append("public ");
        }
        if ((accessFlags & ACC_PRIVATE) != 0) {
            b.append("private ");
        }
        if ((accessFlags & ACC_PROTECTED) != 0) {
            b.append("protected ");
        }
        if ((accessFlags & ACC_STATIC) != 0) {
            b.append("static ");
        }
        if ((accessFlags & ACC_FINAL) != 0) {
            b.append("final ");
        }
        if ((accessFlags & ACC_INTERFACE) != 0) {
            b.append("interface ");
        }
        if ((accessFlags & ACC_ABSTRACT) != 0) {
            b.append("abstract ");
        }
        if ((accessFlags & ACC_SYNTHETIC) != 0) {
            b.append("synthetic ");
        }
        if ((accessFlags & ACC_ANNOTATION) != 0) {
            b.append("annotation ");
        }
        if ((accessFlags & ACC_ENUM) != 0) {
            b.append("enum ");
        }
        return b.toString().trim();
    }

    /**
     * Returns a string identifying the field access permissions and properties contained in the
     * specified mask of flags.
     *
     * @param accessFlags mask of access flags.
     * @return a string identifying the field access permissions and properties.
     */
    public static String fieldAccessFlagsToString(int accessFlags) {
        StringBuffer b = new StringBuffer();
        if ((accessFlags & ACC_PUBLIC) != 0) {
            b.append("public ");
        }
        if ((accessFlags & ACC_PRIVATE) != 0) {
            b.append("private ");
        }
        if ((accessFlags & ACC_PROTECTED) != 0) {
            b.append("protected ");
        }
        if ((accessFlags & ACC_STATIC) != 0) {
            b.append("static ");
        }
        if ((accessFlags & ACC_FINAL) != 0) {
            b.append("final ");
        }
        if ((accessFlags & ACC_VOLATILE) != 0) {
            b.append("volatile ");
        }
        if ((accessFlags & ACC_TRANSIENT) != 0) {
            b.append("transient ");
        }
        if ((accessFlags & ACC_NATIVE) != 0) {
            b.append("native ");
        }
        if ((accessFlags & ACC_SYNTHETIC) != 0) {
            b.append("synthetic ");
        }
        if ((accessFlags & ACC_ENUM) != 0) {
            b.append("enum ");
        }
        return b.toString().trim();
    }

    /**
     * Returns a string identifying the method access permissions and properties contained in the
     * specified mask of flags.
     *
     * @param accessFlags mask of access flags.
     * @return a string identifying the method access permissions and properties.
     */
    public static String methodAccessFlagsToString(int accessFlags) {
        StringBuffer b = new StringBuffer();
        if ((accessFlags & ACC_PUBLIC) != 0) {
            b.append("public ");
        }
        if ((accessFlags & ACC_PRIVATE) != 0) {
            b.append("private ");
        }
        if ((accessFlags & ACC_PROTECTED) != 0) {
            b.append("protected ");
        }
        if ((accessFlags & ACC_STATIC) != 0) {
            b.append("static ");
        }
        if ((accessFlags & ACC_FINAL) != 0) {
            b.append("final ");
        }
        if ((accessFlags & ACC_SYNCHRONIZED) != 0) {
            b.append("synchronized ");
        }
        if ((accessFlags & ACC_BRIDGE) != 0) {
            b.append("bridge ");
        }
        if ((accessFlags & ACC_VARARGS) != 0) {
            b.append("varargs ");
        }
        if ((accessFlags & ACC_NATIVE) != 0) {
            b.append("native ");
        }
        if ((accessFlags & ACC_ABSTRACT) != 0) {
            b.append("abstract ");
        }
        if ((accessFlags & ACC_STRICT) != 0) {
            b.append("strict ");
        }
        if ((accessFlags & ACC_SYNTHETIC) != 0) {
            b.append("synthetic ");
        }
        return b.toString().trim();
    }

    /**
     * Returns the integer value (mask) corresponding to the specified access flag.
     *
     * @param accessFlag access flag.
     * @return the integer value (mask) corresponding to the specified access flag.
     */
    public static int accessFlagToInt(String accessFlag) {
        int flag = 0;
        if (accessFlag.equals("public")) {
            flag = ACC_PUBLIC;
        } else if (accessFlag.equals("private")) {
            flag = ACC_PRIVATE;
        } else if (accessFlag.equals("protected")) {
            flag = ACC_PROTECTED;
        } else if (accessFlag.equals("static")) {
            flag = ACC_STATIC;
        } else if (accessFlag.equals("final")) {
            flag = ACC_FINAL;
        } else if (accessFlag.equals("super")) {
            flag = ACC_SUPER;
        } else if (accessFlag.equals("synchronized")) {
            flag = ACC_SYNCHRONIZED;
        } else if (accessFlag.equals("volatile")) {
            flag = ACC_VOLATILE;
        } else if (accessFlag.equals("bridge")) {
            flag = ACC_BRIDGE;
        } else if (accessFlag.equals("transient")) {
            flag = ACC_TRANSIENT;
        } else if (accessFlag.equals("varargs")) {
            flag = ACC_VARARGS;
        } else if (accessFlag.equals("native")) {
            flag = ACC_NATIVE;
        } else if (accessFlag.equals("interface")) {
            flag = ACC_INTERFACE;
        } else if (accessFlag.equals("abstract")) {
            flag = ACC_ABSTRACT;
        } else if (accessFlag.equals("strict")) {
            flag = ACC_STRICT;
        } else if (accessFlag.equals("synthetic")) {
            flag = ACC_SYNTHETIC;
        } else if (accessFlag.equals("annotation")) {
            flag = ACC_ANNOTATION;
        } else if (accessFlag.equals("enum")) {
            flag = ACC_ENUM;
        }
        return flag;
    }
}
