// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.DataOutputStream;
import java.io.IOException;

import static jminusminus.CLConstants.*;

/**
 * Representation of cp_info structure.
 */
abstract class CLCPInfo {
    /**
     * Index of this object in the constant pool.
     */
    public int cpIndex;

    /**
     * cp_info.tag item.
     */
    public short tag;

    /**
     * Writes the contents of this constant pool item to the specified output stream.
     *
     * @param out output stream.
     * @throws IOException if an error occurs while writing.
     */
    public void write(CLOutputStream out) throws IOException {
        out.writeByte(tag);
    }

    /**
     * Return true if this CLCPInfo object is the same as other, and false otherwise.
     *
     * @param other the reference CLCPInfo object with which to compare.
     * @return true if this CLCPInfo object is the same as other, and false otherwise.
     */
    public boolean equals(Object other) {
        return false;
    }
}

/**
 * Representation of CONSTANT_Class_info structure.
 */
class CLConstantClassInfo extends CLCPInfo {
    /**
     * CONSTANT_Class_info.name_index item.
     */
    public int nameIndex;

    /**
     * Constructs a CLConstantClassInfo object.
     *
     * @param nameIndex CONSTANT_Class_info.name_index item.
     */
    public CLConstantClassInfo(int nameIndex) {
        super.tag = CONSTANT_Class;
        this.nameIndex = nameIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);
        out.writeShort(nameIndex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
        if (other instanceof CLConstantClassInfo) {
            CLConstantClassInfo c = (CLConstantClassInfo) other;
            if (c.nameIndex == nameIndex) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Abstract super class of CONSTANT_Fieldref_info, CONSTANT_Methodref_info,
 * CONSTANT_InterfaceMethodref_info structures.
 */
abstract class CLConstantMemberRefInfo extends CLCPInfo {
    /**
     * CONSTANT_Memberref_info.class_index item.
     */
    public int classIndex;

    /**
     * CONSTANT_Memberref_info.name_and_type_index item.
     */
    public int nameAndTypeIndex;

    /**
     * Constructs a CLConstantMemberRefInfo object.
     *
     * @param classIndex       CONSTANT_Memberref_info.class_index item.
     * @param nameAndTypeIndex CONSTANT_Memberref_info.name_and_type_index item.
     * @param tag              CONSTANT_Memberref_info.tag item.
     */
    protected CLConstantMemberRefInfo(int classIndex, int nameAndTypeIndex, short tag) {
        super.tag = tag;
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);
        out.writeShort(classIndex);
        out.writeShort(nameAndTypeIndex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
        if (other instanceof CLConstantMemberRefInfo) {
            CLConstantMemberRefInfo c = (CLConstantMemberRefInfo) other;
            if ((c.tag == tag) && (c.classIndex == classIndex)
                    && (c.nameAndTypeIndex == nameAndTypeIndex)) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Representation of CONSTANT_Fieldref_info structure.
 */
class CLConstantFieldRefInfo extends CLConstantMemberRefInfo {
    /**
     * Constructs a CLConstantFieldRefInfo object.
     *
     * @param classIndex       CONSTANT_Fieldref_info.class_index item.
     * @param nameAndTypeIndex CONSTANT_Fieldref_info.name_and_type_index item.
     */

    public CLConstantFieldRefInfo(int classIndex, int nameAndTypeIndex) {
        super(classIndex, nameAndTypeIndex, CONSTANT_Fieldref);
    }
}

/**
 * Representation of CONSTANT_Methodref_info structure.
 */
class CLConstantMethodRefInfo extends CLConstantMemberRefInfo {
    /**
     * Constructs a CLConstantMethodRefInfo object.
     *
     * @param classIndex       CONSTANT_Methodref_info.class_index item.
     * @param nameAndTypeIndex CONSTANT_Methodref_info.name_and_type_index item.
     */
    public CLConstantMethodRefInfo(int classIndex, int nameAndTypeIndex) {
        super(classIndex, nameAndTypeIndex, CONSTANT_Methodref);
    }
}

/**
 * Representation of CONSTANT_InterfaceMethodref_info structure.
 */
class CLConstantInterfaceMethodRefInfo extends CLConstantMemberRefInfo {
    /**
     * Constructs a CLConstantInterfaceMethodRefInfo object.
     *
     * @param classIndex       CONSTANT_InterfaceMethodref_info.class_index item.
     * @param nameAndTypeIndex CONSTANT_InterfaceMethodref_info.name_and_type_index item.
     */
    public CLConstantInterfaceMethodRefInfo(int classIndex, int nameAndTypeIndex) {
        super(classIndex, nameAndTypeIndex, CONSTANT_InterfaceMethodref);
    }
}

/**
 * Representation of CONSTANT_String_info structure.
 */
class CLConstantStringInfo extends CLCPInfo {
    /**
     * CONSTANT_String_info.string_index item.
     */
    public int stringIndex;

    /**
     * Constructs a CLConstantStringInfo object.
     *
     * @param stringIndex CONSTANT_String_info.string_index item.
     */
    public CLConstantStringInfo(int stringIndex) {
        super.tag = CONSTANT_String;
        this.stringIndex = stringIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);
        out.writeShort(stringIndex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
        if (other instanceof CLConstantStringInfo) {
            CLConstantStringInfo c = (CLConstantStringInfo) other;
            if (c.stringIndex == stringIndex) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Representation of CONSTANT_Integer_info structure.
 */
class CLConstantIntegerInfo extends CLCPInfo {
    /**
     * The int number.
     */
    public int i;

    /**
     * Constructs a CLConstantIntegerInfo object.
     *
     * @param i the int number.
     */
    public CLConstantIntegerInfo(int i) {
        super.tag = CONSTANT_Integer;
        this.i = i;
    }

    /**
     * Returns CONSTANT_Integer_info.bytes item.
     *
     * @return CONSTANT_Integer_info.bytes item.
     */
    public short[] bytes() {
        short[] s = new short[4];
        short mask = 0xFF;
        int k = i;
        for (int j = 0; j < 4; j++) {
            s[3 - j] = (short) (k & mask);
            k >>>= 8;
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);

        // out is cast to DataOutputStream to resolve the writeInt() ambiguity.
        ((DataOutputStream) out).writeInt(i);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
        if (other instanceof CLConstantIntegerInfo) {
            CLConstantIntegerInfo c = (CLConstantIntegerInfo) other;
            if (c.i == i) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Representation of CONSTANT_Float_info structure.
 */
class CLConstantFloatInfo extends CLCPInfo {
    /**
     * The floating-point number.
     */
    public float f;

    /**
     * Constructs a CLConstantFloatInfo object.
     *
     * @param f the floating-point number.
     */
    public CLConstantFloatInfo(float f) {
        super.tag = CONSTANT_Float;
        this.f = f;
    }

    /**
     * Returns CONSTANT_Float_info.bytes item.
     *
     * @return CONSTANT_Float_info.bytes item.
     */
    public short[] bytes() {
        short[] s = new short[4];
        short mask = 0xFF;
        int i = Float.floatToIntBits(f);
        for (int j = 0; j < 4; j++) {
            s[3 - j] = (short) (i & mask);
            i >>>= 8;
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);
        out.writeFloat(f);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
        if (other instanceof CLConstantFloatInfo) {
            CLConstantFloatInfo c = (CLConstantFloatInfo) other;
            if (c.f == f) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Representation of CONSTANT_Long_info structure.
 */
class CLConstantLongInfo extends CLCPInfo {
    /**
     * The long number.
     */
    public long l;

    /**
     * Returns the 8 bytes of the long value.
     *
     * @return the 8 bytes of the long value.
     */
    private short[] bytes() {
        short[] s = new short[8];
        short mask = 0xFF;
        long k = l;
        for (int j = 0; j < 8; j++) {
            s[7 - j] = (short) (k & mask);
            k >>>= 8;
        }
        return s;
    }

    /**
     * Constructs a CLConstantLongInfo object.
     *
     * @param l the long number.
     */
    public CLConstantLongInfo(long l) {
        super.tag = CONSTANT_Long;
        this.l = l;
    }

    /**
     * Returns CONSTANT_Long_info.low_bytes item.
     *
     * @return CONSTANT_Long_info.low_bytes item.
     */
    public short[] lowBytes() {
        short[] s = bytes();
        short[] l = new short[4];
        l[0] = s[4];
        l[1] = s[5];
        l[2] = s[6];
        l[3] = s[7];
        return l;
    }

    /**
     * Returns CONSTANT_Long_info.high_bytes item.
     *
     * @return CONSTANT_Long_info.high_bytes item.
     */
    public short[] highBytes() {
        short[] s = bytes();
        short[] h = new short[4];
        h[0] = s[0];
        h[1] = s[1];
        h[2] = s[2];
        h[3] = s[3];
        return h;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);
        out.writeLong(l);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
        if (other instanceof CLConstantLongInfo) {
            CLConstantLongInfo c = (CLConstantLongInfo) other;
            if (c.l == l) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Representation of CONSTANT_Double_info structure.
 */
class CLConstantDoubleInfo extends CLCPInfo {
    /**
     * The double precision floating-point number.
     */
    public double d;

    /**
     * Returns the 8 bytes of the double precision floating-point value.
     *
     * @return the 8 bytes of the double precision floating-point value.
     */
    private short[] bytes() {
        short[] s = new short[8];
        short mask = 0xFF;
        long l = Double.doubleToLongBits(d);
        for (int j = 0; j < 8; j++) {
            s[7 - j] = (short) (l & mask);
            l >>>= 8;
        }
        return s;
    }

    /**
     * Constructs a CLConstantDoubleInfo object.
     *
     * @param d the double precision floating-point number.
     */
    public CLConstantDoubleInfo(double d) {
        super.tag = CONSTANT_Double;
        this.d = d;
    }

    /**
     * Returns CONSTANT_Double_info.low_bytes item.
     *
     * @return CONSTANT_Double_info.low_bytes item.
     */
    public short[] lowBytes() {
        short[] s = bytes();
        short[] l = new short[4];
        l[0] = s[4];
        l[1] = s[5];
        l[2] = s[6];
        l[3] = s[7];
        return l;
    }

    /**
     * Returns CONSTANT_Double_info.high_bytes item.
     *
     * @return CONSTANT_Double_info.high_bytes item.
     */
    public short[] highBytes() {
        short[] s = bytes();
        short[] h = new short[4];
        h[0] = s[0];
        h[1] = s[1];
        h[2] = s[2];
        h[3] = s[3];
        return h;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);
        out.writeDouble(d);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
        if (other instanceof CLConstantDoubleInfo) {
            CLConstantDoubleInfo c = (CLConstantDoubleInfo) other;
            if (c.d == d) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Representation of CONSTANT_NameAndType_info structure.
 */
class CLConstantNameAndTypeInfo extends CLCPInfo {
    /**
     * CONSTANT_NameAndType_info.name_index item.
     */
    public int nameIndex;

    /**
     * CONSTANT_NameAndType_info.descriptor_index item.
     */
    public int descriptorIndex;

    /**
     * Constructs a CLConstantNameAndTypeInfo object.
     *
     * @param nameIndex       CONSTANT_NameAndType_info.name_index item.
     * @param descriptorIndex CONSTANT_NameAndType_info.descriptor_index item.
     */
    public CLConstantNameAndTypeInfo(int nameIndex, int descriptorIndex) {
        super.tag = CONSTANT_NameAndType;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);
        out.writeShort(nameIndex);
        out.writeShort(descriptorIndex);
    }

    /**
     * {@inheritDoc}
     */

    public boolean equals(Object other) {
        if (other instanceof CLConstantNameAndTypeInfo) {
            CLConstantNameAndTypeInfo c = (CLConstantNameAndTypeInfo) other;
            if ((c.nameIndex == nameIndex) && (c.descriptorIndex == descriptorIndex)) {
                return true;
            }
        }
        return false;
    }
}

/**
 * Representation of CONSTANT_Utf8_info structure.
 */
class CLConstantUtf8Info extends CLCPInfo {
    /**
     * CONSTANT_Utf8_info.bytes item.
     */
    public byte[] b;

    /**
     * Constructs a CLConstantUtf8Info object.
     *
     * @param b a constant string value.
     */
    public CLConstantUtf8Info(byte[] b) {
        super.tag = CONSTANT_Utf8;
        this.b = b;
    }

    /**
     * Returns CONSTANT_Utf8_info.length item.
     *
     * @return CONSTANT_Utf8_info.length item.
     */
    public int length() {
        return b.length;
    }

    /**
     * {@inheritDoc}
     */
    public void write(CLOutputStream out) throws IOException {
        super.write(out);
        out.writeUTF(new String(b));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object other) {
        if (other instanceof CLConstantUtf8Info) {
            CLConstantUtf8Info c = (CLConstantUtf8Info) other;
            if ((new String(b)).equals(new String(c.b))) {
                return true;
            }
        }
        return false;
    }
}
